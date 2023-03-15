package games.wonders7;
import core.AbstractForwardModel;
import core.AbstractGameState;
import core.CoreConstants;
import core.actions.AbstractAction;
import core.components.Deck;
import games.wonders7.actions.DiscardCard;
import games.wonders7.actions.PlayCard;
import games.wonders7.cards.Wonder7Card;
import games.wonders7.cards.WonderBoard;
import utilities.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Wonders7ForwardModel extends AbstractForwardModel {
// The rationale of the ForwardModel is that it contains the core game logic, while the GameState contains the underlying game data. 
// Usually this means that ForwardModel is stateless, and this is a good principle to adopt, but as ever there will always be exceptions.

    public void _setup(AbstractGameState state){
        Wonders7GameState wgs = (Wonders7GameState) state;

        // Sets up each player's hand and their played cards, and their resources
        wgs.playerHand = new ArrayList<>();
        wgs.playedCards = new ArrayList<>();
        for (int i=0; i<wgs.getNPlayers(); i++){
            wgs.playerHand.add(new Deck<>("Player hand" + i, i, CoreConstants.VisibilityMode.HIDDEN_TO_ALL));
            wgs.playedCards.add(new Deck<>("Played Cards", CoreConstants.VisibilityMode.VISIBLE_TO_ALL));
        }

        // Cards that have been discarded all players
        wgs.discardedCards = new Deck<Wonder7Card>("Discarded Cards", CoreConstants.VisibilityMode.HIDDEN_TO_ALL);

        // Creates Age1 Deck and shuffle
        wgs.AgeDeck = new Deck<Wonder7Card>("Age 1 Deck", CoreConstants.VisibilityMode.HIDDEN_TO_ALL); //Going
        createDeck(wgs); // Fills Age1 deck with cards
        Random r = new Random(wgs.getGameParameters().getRandomSeed());
        wgs.AgeDeck.shuffle(r);

        // Give each player their 7 cards in the beginning of the Age
        for (int player=0; player < wgs.getNPlayers(); player++){
            for (int card=0; card< ((Wonders7GameParameters) wgs.getGameParameters()).nWonderCardsPerPlayer; card++) {
                wgs.getPlayerHand(player).add(wgs.AgeDeck.draw());
            }
        }

        // Player 0 starts
        wgs.getTurnOrder().setTurnOwner(0);

    } 

    public void _next(AbstractGameState state, AbstractAction action){
        Wonders7GameState wgs = (Wonders7GameState) state;
        action.execute(wgs); //Apply the given action to the game state. This logic should be in the AbstractAction.execute() method, which well get to later, so this should be as simple as action.execute(state)
        wgs.getTurnOrder().endPlayerTurn(wgs); // Move to the next player (if required, and if the game has not ended). This is usually achieved with state.getTurnOrder().endPlayerTurn(state), with this logic encapsulated in FoobarTurnOrder.
        checkAgeEnd(wgs); // Check for game end;
    }

    @Override
    protected List<AbstractAction> _computeAvailableActions(AbstractGameState gameState) {
        Wonders7GameState wgs = (Wonders7GameState) gameState;
        ArrayList<AbstractAction> actions = new ArrayList<>();

        // All playable cards in player hand
        for (int i=0; i<wgs.getPlayerHand(gameState.getCurrentPlayer()).getSize(); i++){ // Goes through each card in hand
            if (wgs.getPlayerHand(wgs.getCurrentPlayer()).get(i).isPlayable(wgs)){
                actions.add(new PlayCard(wgs.getPlayerHand(wgs.getCurrentPlayer()).get(i).cardName)); // Adds the card
            }
        }
        // All discard-able cards in player hand
        for (int i=0; i<wgs.getPlayerHand(gameState.getCurrentPlayer()).getSize(); i++){
            actions.add(new DiscardCard(wgs.getPlayerHand(wgs.getCurrentPlayer()).get(i).cardName)); //
        }
        return actions;
    }

    @Override
    protected AbstractForwardModel _copy() {
        // In the _copy() method, return a new instance of the Forward Model object with any variables copied.

        return new Wonders7ForwardModel();
    }

    protected void createDeck(Wonders7GameState wgs){
        // This method will create the deck for the current Era
        // All the hashmaps containing different number of resources

        for (int i = 0; i<2; i++){
            // Raw Materials
            wgs.AgeDeck.add(new Wonder7Card("Lumber Yard", Wonder7Card.Wonder7CardType.RawMaterials, wgs.getNPlayers(), 1, createCardHash(new Wonder7Card.resources[]{Wonder7Card.resources.wood}, new int[]{1})));
            wgs.AgeDeck.add(new Wonder7Card("Ore Vein", Wonder7Card.Wonder7CardType.RawMaterials, wgs.getNPlayers(), 1, createCardHash(new Wonder7Card.resources[]{Wonder7Card.resources.ore}, new int[]{1})));
            wgs.AgeDeck.add(new Wonder7Card("Clay Pool", Wonder7Card.Wonder7CardType.RawMaterials, wgs.getNPlayers(), 1, createCardHash(new Wonder7Card.resources[]{Wonder7Card.resources.clay}, new int[]{1})));
            wgs.AgeDeck.add(new Wonder7Card("Stone Pit", Wonder7Card.Wonder7CardType.RawMaterials, wgs.getNPlayers(), 1, createCardHash(new Wonder7Card.resources[]{Wonder7Card.resources.stone}, new int[]{1})));
            // Manufactured Goods
            wgs.AgeDeck.add(new Wonder7Card("Loom", Wonder7Card.Wonder7CardType.ManufacturedGoods, wgs.getNPlayers(), 1, createCardHash(new Wonder7Card.resources[]{Wonder7Card.resources.textile}, new int[]{1})));
            wgs.AgeDeck.add(new Wonder7Card("GlassWorks", Wonder7Card.Wonder7CardType.ManufacturedGoods, wgs.getNPlayers(), 1, createCardHash(new Wonder7Card.resources[]{Wonder7Card.resources.glass}, new int[]{1})));
            wgs.AgeDeck.add(new Wonder7Card("Press", Wonder7Card.Wonder7CardType.ManufacturedGoods, wgs.getNPlayers(), 1, createCardHash(new Wonder7Card.resources[]{Wonder7Card.resources.papyrus}, new int[]{1})));
            // Civilian Structures
            wgs.AgeDeck.add(new Wonder7Card("Altar", Wonder7Card.Wonder7CardType.CivilianStructures, wgs.getNPlayers(), 1, createCardHash(new Wonder7Card.resources[]{}, new int[]{}), createCardHash(new Wonder7Card.resources[]{Wonder7Card.resources.victory}, new int[]{2})));
            // Scientific Structures
            wgs.AgeDeck.add(new Wonder7Card("Apothecary", Wonder7Card.Wonder7CardType.ScientificStructures, wgs.getNPlayers(), 1, createCardHash(new Wonder7Card.resources[]{Wonder7Card.resources.textile}, new int[]{1}), createCardHash(new Wonder7Card.resources[]{Wonder7Card.resources.compass}, new int[]{1})));
            wgs.AgeDeck.add(new Wonder7Card("Workshop", Wonder7Card.Wonder7CardType.ScientificStructures, wgs.getNPlayers(), 1, createCardHash(new Wonder7Card.resources[]{Wonder7Card.resources.glass}, new int[]{1}), createCardHash(new Wonder7Card.resources[]{Wonder7Card.resources.cog}, new int[]{1})));
            wgs.AgeDeck.add(new Wonder7Card("Scriptorium", Wonder7Card.Wonder7CardType.ScientificStructures, wgs.getNPlayers(), 1, createCardHash(new Wonder7Card.resources[]{Wonder7Card.resources.papyrus}, new int[]{1}), createCardHash(new Wonder7Card.resources[]{Wonder7Card.resources.tablet}, new int[]{1})));
            // Commercial Structures

            // MilitaryStructures
            wgs.AgeDeck.add(new Wonder7Card("Stockade", Wonder7Card.Wonder7CardType.MilitaryStructures, wgs.getNPlayers(), 1, createCardHash(new Wonder7Card.resources[]{Wonder7Card.resources.wood}, new int[]{1}), createCardHash(new Wonder7Card.resources[]{Wonder7Card.resources.shield}, new int[]{1})));
        }
        for (int i = 0; i<3; i++){
            wgs.AgeDeck.add(new Wonder7Card("Tavern", Wonder7Card.Wonder7CardType.CommercialStructures, wgs.getNPlayers(), 1, createCardHash(new Wonder7Card.resources[]{Wonder7Card.resources.coin}, new int[]{5})));

        }
    }

    protected HashMap<Wonder7Card.resources, Integer> createCardHash(Wonder7Card.resources[] resource, int[] number){
        // This will have to create the resource hashmaps for each card and return them
        HashMap<Wonder7Card.resources, Integer> card = new HashMap<>();
        for (int i=0; i < number.length; i++){
            card.put(resource[i], number[i]);
        }
        return card;
    }

    protected void checkAgeEnd(Wonders7GameState wgs){
        if (wgs.getPlayerHand(wgs.getCurrentPlayer()).getSize() == 0){  // If all players hands are empty
            wgs.getTurnOrder().endRound(wgs); // Ends the round,
            // Resolves military conflicts
            for (int i=0; i< wgs.getNPlayers(); i++){
                int nextplayer = (i+1)% wgs.getNPlayers();
                if(wgs.getPlayerResources(i).get(Wonder7Card.resources.shield) > wgs.getPlayerResources(nextplayer).get(Wonder7Card.resources.shield)){ // IF PLAYER i WINS
                    wgs.getPlayerResources(nextplayer).put(Wonder7Card.resources.victory,  wgs.getPlayerResources(nextplayer).get(Wonder7Card.resources.victory)+(2*wgs.currentAge-1)); // 2N-1 POINTS FOR PLAYER i
                    wgs.getPlayerResources(i).put(Wonder7Card.resources.victory,  wgs.getPlayerResources(i).get(Wonder7Card.resources.victory)-1); // -1 FOR THE PLAYER i+1
                }
                else { // IF PLAYER i+1 WINS
                    wgs.getPlayerResources(i).put(Wonder7Card.resources.victory,  wgs.getPlayerResources(i).get(Wonder7Card.resources.victory)-1);// -1 POINT FOR THE PLAYER i
                    wgs.getPlayerResources(nextplayer).put(Wonder7Card.resources.victory,  wgs.getPlayerResources(nextplayer).get(Wonder7Card.resources.victory)+(2*wgs.currentAge-1));// 2N-1 POINTS FOR PLAYER i+1
                }
            }

            wgs.currentAge += 1; // Next age starts
            checkGameEnd(wgs); // Checks if the game has ended!
        }

    }

    protected void checkGameEnd(Wonders7GameState wgs){
        if (wgs.currentAge == 2){
            // Calculate victory points in order of:
            // treasury, scientific, commercial and finally guilds
            for (int i=0; i< wgs.getNPlayers(); i++){
                // Treasury
                wgs.playerResources.get(i).put(Wonder7Card.resources.victory, wgs.getPlayerResources(i).get(Wonder7Card.resources.coin)/3);
                // Scientific
                wgs.getPlayerResources(i).put(Wonder7Card.resources.victory, wgs.getPlayerResources(i).get(Wonder7Card.resources.victory)+(int)Math.pow(wgs.getPlayerResources(i).get(Wonder7Card.resources.cog),2));
                wgs.getPlayerResources(i).put(Wonder7Card.resources.victory, wgs.getPlayerResources(i).get(Wonder7Card.resources.victory)+(int)Math.pow(wgs.getPlayerResources(i).get(Wonder7Card.resources.compass),2));
                wgs.getPlayerResources(i).put(Wonder7Card.resources.victory, wgs.getPlayerResources(i).get(Wonder7Card.resources.victory)+(int)Math.pow(wgs.getPlayerResources(i).get(Wonder7Card.resources.tablet),2));
                wgs.getPlayerResources(i).put(Wonder7Card.resources.victory, wgs.getPlayerResources(i).get(Wonder7Card.resources.victory)+7*Math.min(Math.min(wgs.getPlayerResources(i).get(Wonder7Card.resources.cog),wgs.getPlayerResources(i).get(Wonder7Card.resources.compass)),wgs.getPlayerResources(i).get(Wonder7Card.resources.tablet))); // Sets of different science symbols

            }

            int winner = 0;
            for (int i=0; i<wgs.getNPlayers(); i++){
                if (wgs.getPlayerResources(i).get(Wonder7Card.resources.victory) >= wgs.getPlayerResources(winner).get(Wonder7Card.resources.victory)){
                    wgs.setPlayerResult(Utils.GameResult.LOSE,winner); // SETS PREVIOUS WINNER AS LOST
                    wgs.setPlayerResult(Utils.GameResult.WIN,i); // SETS NEW WINNER AS PLAYER i
                    winner = i; // Sets the new winner as i, THIS DOESN'T WORK FOR TIES
                }
                else {
                    wgs.setPlayerResult(Utils.GameResult.LOSE,i); // Sets this player as LOST
                }
            }

            wgs.setGameStatus(Utils.GameResult.GAME_END); // CHANGE THE NUMBER!!!
            System.out.println("FINAL AGE HAS ENDED!!!!!!!!!!!!!");
            System.out.println("The winner is Player  " + winner +"!!!!");
        }
    }

    @Override
    protected void endGame(AbstractGameState gameState) {
        //
    }
    // You may override the endGame() method if your game requires any extra end of game computation (e.g. to update the status of players still in the game to winners).
    // !!!
    // Note: Forward model classes can instead extend from the core.rules.AbstractRuleBasedForwardModel.java abstract class instead, if they wish to use the rule-based system instead; this class provides basic functionality and documentation for using rules and an already implemented _next() function.
    // !!!

    
}

