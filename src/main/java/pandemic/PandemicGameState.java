package pandemic;

import actions.*;
import components.*;
import content.*;
import core.Area;
import core.Game;
import core.GameState;
import utilities.Hash;
import utilities.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PandemicGameState extends GameState {

    //TODO: remove 'static' from these?
    static int playerHandHash = Hash.GetInstance().hash("playerHand");
    public static int playerCardHash = Hash.GetInstance().hash("playerCard");
    public static int pandemicBoardHash = Hash.GetInstance().hash("pandemicBoard");

    public static String[] colors = new String[]{"yellow", "red", "blue", "black"};

    public Board world;
    public int numAvailableActions = 0;

    public void setupAreas()
    {
        //1. Library of area setups: game.setAreas();
        //2. Like this:

        // For each player, initialize their own areas: they get a player hand and a player card
        for (int i = 0; i < nPlayers; i++) {
            Area playerArea = new Area();
            playerArea.setOwner(i);
            playerArea.addComponent(playerHandHash, new Deck(7));
            playerArea.addComponent(playerCardHash, new Card());  // TODO: properties?
            areas.put(i, playerArea);
        }

        // Initialize the game area: board, player deck, player discard deck, infection deck, infection discard
        // infection rate counter, outbreak counter, diseases x 4
        Area gameArea = new Area();
        gameArea.setOwner(-1);
        gameArea.addComponent(pandemicBoardHash, world);
        areas.put(-1, gameArea);
    }


    public void setup(Game game)
    {
        // load the board
        world = game.findBoard("cities"); //world.getNode("name","Valencia");

        setupAreas(); // TODO: This should be called from GameState.java

        Area gameArea = areas.get(-1);

        // Set up the counters
        Counter infection_rate = game.findCounter("Infection Rate");
        Counter outbreaks = game.findCounter("Outbreaks");
        gameArea.addComponent(Hash.GetInstance().hash("Infection Rate"), infection_rate);
        gameArea.addComponent(Hash.GetInstance().hash("Outbreaks"), outbreaks);

        for (String color : colors) {
            // todo get countervalue from data
            int counterValue = 24;
            int hash = Hash.GetInstance().hash("Disease " + color);
            Counter diseaseC = game.findCounter("Disease " + color);
            diseaseC.setValue(counterValue);
            gameArea.addComponent(hash, diseaseC);

            hash = Hash.GetInstance().hash("Disease Cube " + color);
            Counter diseaseCubeCounter = game.findCounter("Disease Cube " + color);
            gameArea.addComponent(hash, diseaseCubeCounter);
        }

        // Set up decks
        Deck playerDeck = new Deck("Player Deck");
        Deck playerDiscard = new Deck("Player Deck Discard");
        Deck infDiscard = new Deck("Infection Discard");

        gameArea.addComponent(Hash.GetInstance().hash("Player Deck"), playerDeck);  // TODO: combine cities+events
        gameArea.addComponent(Hash.GetInstance().hash("Player Deck Discard"), playerDiscard);
        gameArea.addComponent(Hash.GetInstance().hash("Infection Discard"), infDiscard);
        gameArea.addComponent(Hash.GetInstance().hash("Infections"), game.findDeck("Infections"));
        gameArea.addComponent(Hash.GetInstance().hash("Player Roles"), game.findDeck("Player Roles"));

        // add them to the list of decks, so they are accessible by the game.findDeck() function
        game.addDeckToList(playerDeck);
        game.addDeckToList(infDiscard);
        game.addDeckToList(playerDiscard);

        // Set up tokens
        Token research_stations = game.findToken("Research Stations");
        int hash = Hash.GetInstance().hash("Research Stations");
        gameArea.addComponent(hash, research_stations);

        //TODO: add pawn tokens

    }

    @Override
    public GameState copy() {
        //TODO: copy pandemic game state
        return this;
    }

    @Override
    public int nInputActions() {
        return 4;  // Pandemic requires up to 4 actions per player per turn.
    }

    @Override
    public int nPossibleActions() {
        return this.numAvailableActions;
    }

    @Override
    public List<Action> possibleActions() {
        // todo there are some repetitive iterations, could collect all the necessary information in a single loop
        // todo actions that require a card do not seem to remove the card
        // Create a list for possible actions
        ArrayList<Action> actions = new ArrayList<>();
        Deck playerDeck = ((Deck)this.areas.get(activePlayer).getComponent(playerHandHash));
        if (playerDeck.isOverCapacity()){
            // need to discard a card
            for (int i = 0; i < playerDeck.getCards().size(); i++){
                actions.add(new DiscardCard(playerDeck, i));
            }
            this.numAvailableActions = actions.size();
            return actions;
        }

        // add do nothing action
        actions.add(new DoNothing());

        // Drive / Ferry add actions for travelling immediate cities
        Property playerLocation = this.areas.get(activePlayer).getComponent(playerCardHash).getProperty(Hash.GetInstance().hash("playerLocation"));
        BoardNode cityNode = world.getNodeByProperty(Hash.GetInstance().hash("name"), playerLocation);
        for (BoardNode otherCity : cityNode.getNeighbours()){
            if (((PropertyString)otherCity.getProperty(Hash.GetInstance().hash("name"))).value == null){
                System.out.println("null");
            }
            actions.add(new MovePlayer(activePlayer, ((PropertyString)otherCity.getProperty(Hash.GetInstance().hash("name"))).value));
        }

        // Direct Flight, discard city card and travel to that city
        // get player's cards and create actions that take from current location to those location
        for (Card card: playerDeck.getCards()){
            // get the city from the card

            // next line is the full way to get the first card in the player's hand
//            ((Deck)this.areas.get(activePlayer).getComponent(playerHandHash)).getCards().get(0).getProperty(Hash.GetInstance().hash("name"));

            //  check if card has country to determine if it is city card or not
            if ((card.getProperty(Hash.GetInstance().hash("country"))) != null){
                actions.add(new MovePlayer(activePlayer, ((PropertyString)card.getProperty(Hash.GetInstance().hash("name"))).value));
            }
        }

        // charter flight, discard city that matches your card and travel to any city
        for (Card card: playerDeck.getCards()){
            // get the city from the card
            if (playerLocation.equals(((PropertyString)card.getProperty(Hash.GetInstance().hash("name"))).value)){
                // add all the cities
                // iterate over all the cities in the world
                for (BoardNode bn: this.world.getBoardNodes()) {
                    PropertyString destination = (PropertyString) bn.getProperty(Hash.GetInstance().hash("name"));

                    // only add the ones that are different from the current location
                    if (!destination.equals(playerLocation)) {
                        actions.add(new MovePlayer(activePlayer, destination.value));
                    }
                }
            }
        }

        // shuttle flight, move from city with research station to any other research station
        // get research stations from board
        ArrayList<PropertyString> researchStations = new ArrayList<>();
        boolean currentHasStation = false;
        for (BoardNode bn: this.world.getBoardNodes()){
            if (((PropertyBoolean)bn.getProperty(Hash.GetInstance().hash("researchStation"))).value == true){
                if (bn.getProperty(Hash.GetInstance().hash("name")).equals(playerLocation)){
                    currentHasStation = true;
                }
                else {
                    // researchStations do not contain the current station
                    researchStations.add((PropertyString)bn.getProperty(Hash.GetInstance().hash("name")));
                }
            }
        }
        // if current city has research station, add every city that has research stations
        if (currentHasStation){
            for (PropertyString station: researchStations){
                actions.add(new MovePlayer(activePlayer, station.value));
            }
        }


        // build research station, discard card with that city to build one, if 6 are used, then take one from board
        // 1, check if player has the city in the hand
        // 2, new AddResearchStation action with city name
        // todo check if city has research station or not
        for (Card card: playerDeck.getCards()){
            Property cardName = card.getProperty(Hash.GetInstance().hash("name"));
            if (cardName.equals(playerLocation)){
                if (((PropertyString)cardName).value == null){
                    System.out.println("null");
                }
                actions.add(new AddResearchStation(((PropertyString)cardName).value));
            }
            // todo removing 6th city is not in the list, so in that case the action would contain which city to be removed
        }

        // Treat disease
        // should check if city has disease
        for (BoardNode bn: this.world.getBoardNodes()){
            if ((bn.getProperty(Hash.GetInstance().hash("name"))).equals(playerLocation)){
                // bn is the node where player is standing
                // todo test with infections being present on the board
                PropertyIntArray cityInfections = (PropertyIntArray)bn.getProperty(Hash.GetInstance().hash("infection"));
                // remove one disease cube
                for (int i = 0; i < cityInfections.getValues().length; i++){
                    if (cityInfections.getValues()[i] > 0){
                        // todo test with actual diseases
                        actions.add(new TreatDisease(pandemic.PandemicGameState.colors[i]));
                    }
                }
            }
        }


        //  share knowledge, give or take card, player can only have 7 cards
        // can give any card to anyone
        for (Card card: playerDeck.getCards()){
            for (int i = 0; i < nPlayers; i++){
                if (i != activePlayer){
                    actions.add(new GiveCard(card, i));
                }
            }
        }
        // can take any card from anyone
        for (int i = 0; i < nPlayers; i++){
            if (i != activePlayer){
                Deck otherDeck = (Deck)this.areas.get(activePlayer).getComponent(playerHandHash);
                for (Card card: otherDeck.getCards()){
                    actions.add(new TakeCard(card, i));
                }
            }
        }


        // discover a cure, 5 cards of the same colour at a research station
        int[] colourCounter = new int[pandemic.PandemicGameState.colors.length];
        for (Card card: playerDeck.getCards()){
            Property p  = card.getProperty(Hash.GetInstance().hash("color"));
            if (p != null){
                // Only city cards have colours, events don't
                String color = ((PropertyColor)p).valueStr;
                colourCounter[Utils.indexOf(pandemic.PandemicGameState.colors, color)]++;
            }
        }
        for (int i =0 ; i < colourCounter.length; i++){
            if (colourCounter[i] >= 5){
                actions.add(new TreatDisease(pandemic.PandemicGameState.colors[i]));
            }
        }


        // TODO event cards don't count as action and can be played anytime
        for (Card card: playerDeck.getCards()){
            Property p  = card.getProperty(Hash.GetInstance().hash("color"));
            if (p == null){
                // Event card's don't have colour
                actions.addAll(actionsFromEventCard(card));
            }
        }


        this.numAvailableActions = actions.size();

        return actions;  // TODO
    }

    void setActivePlayer(int activePlayer) {
        this.activePlayer = activePlayer;
    }

    private List actionsFromEventCard(Card card){
        // todo event cards get in here, but most actions are not created
        ArrayList<Action> actions = new ArrayList<>();
        String cardString = ((PropertyString)card.getProperty(Hash.GetInstance().hash("name"))).value;
        if (cardString.equals("Resilient Population")){
            System.out.println("Resilient Population");
//            System.out.println("Remove any 1 card in the Infection Discard Pile from the game. You may play this between the Infect and Intensify steps of an epidemic.");
        } else if (cardString.equals("Airlift")){
            System.out.println("Airlift");
//            System.out.println("Move any 1 pawn to any city. Get permission before moving another player's pawn.");
        }
        else if (cardString.equals("Government Grant")){
            System.out.println("Government Grant");
//            System.out.println("Add 1 research station to any city (no City card needed).");
            for (BoardNode bn: this.world.getBoardNodes()){
                if (!((PropertyBoolean)bn.getProperty((Hash.GetInstance().hash("researchStation")))).value);
                    actions.add(new AddResearchStation(((PropertyString)this.world.getBoardNodes().get(0).getProperty(Hash.GetInstance().hash("name"))).value));
            }
        }
        else if (cardString.equals("One quiet night")){
            System.out.println("One quiet night");
//            System.out.println("Skip the next Infect Cities step (do not flip over any Infection cards).");
        }
        else if (cardString.equals("Forecast")){
            System.out.println("Forecast");
//            System.out.println("Draw, look at, and rearrange the top 6 cards of the Infection Deck. Put them back on top.");
        }

        return actions;
    }
}
