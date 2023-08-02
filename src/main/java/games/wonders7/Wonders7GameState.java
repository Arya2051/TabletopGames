package games.wonders7;

import core.AbstractGameState;
import core.AbstractParameters;
import core.actions.AbstractAction;
import core.components.Component;
import core.components.Deck;
import games.wonders7.cards.Wonders7Card;
import games.wonders7.cards.Wonders7Board;

import java.util.*;

import static core.CoreConstants.VisibilityMode.VISIBLE_TO_ALL;
import static games.GameType.Wonders7;

public class Wonders7GameState extends AbstractGameState {

    int currentAge; // int from 1,2,3 of current age
    List<HashMap<Wonders7Constants.resources, Integer>> playerResources; // Each player's full resource counts
    List<Deck<Wonders7Card>> playerHands; // Player Hands
    List<Deck<Wonders7Card>> playedCards; // Player used cards
    Deck<Wonders7Card> AgeDeck; // The 'draw deck' for the Age
    Deck<Wonders7Card> discardPile; // Discarded cards

    Deck<Wonders7Board> wonderBoardDeck; // The deck of wonder board that decide a players wonder

    Wonders7Board[] playerWonderBoard; // Every player's assigned Wonder Board

    AbstractAction[] turnActions; // The round's actions chosen by each player


    public Wonders7GameState(AbstractParameters gameParameters, int nPlayers) {
        super(gameParameters, new Wonders7TurnOrder(nPlayers), Wonders7);
        // Sets game in Age 1
        currentAge = 1;

        // Each player starts off with no resources
        playerResources = new ArrayList<>(); // New arraylist , containing different hashmaps for each player
        for (int i = 0; i < getNPlayers(); i++) {
            playerResources.add(new HashMap<>());

        }

        // Then fills every player's hashmaps, so each player has 0 of each resource
        for (int i = 0; i < getNPlayers(); i++) { // For each
            for (Wonders7Constants.resources type : Wonders7Constants.resources.values()) {
                playerResources.get(i).put(type, 0);
            }
        }
    }

    public List<Component> _getAllComponents() {
        // return a list of all (parents, and those nested as well) components in your game state. The method is called after game setup, so you may assume all components are already created. Decks and Areas have all of their nested components automatically added.
        return new ArrayList<Component>() {{
            addAll(playerHands);
            addAll(playedCards);
            add(discardPile);
        }};
        //return new ArrayList<>();
    }

    public AbstractGameState _copy(int playerId) {
        // define a reduced, player-specific, copy of your game state
        // Including components that player with the given ID will see.
        // For example, some decks may be face down and unobservable to the player
        // All the components in the observation should be copies of those in the game state
        Wonders7GameState copy = new Wonders7GameState(gameParameters.copy(), getNPlayers());
        //Wonders7TurnOrder turnOrder = new Wonders7TurnOrder(getNPlayers());
        copy.playerResources = new ArrayList<>();
        copy.playerHands = new ArrayList<>();
        copy.playedCards = new ArrayList<>();
        copy.playerWonderBoard = new Wonders7Board[getNPlayers()];
        copy.turnActions = new AbstractAction[getNPlayers()]; // Player actions are not visible

        for (HashMap<Wonders7Constants.resources, Integer> map : playerResources) {
            copy.playerResources.add(new HashMap<>(map));
        }
        for (Deck<Wonders7Card> deck : playerHands) {
            copy.playerHands.add(deck.copy());
        }
        for (Deck<Wonders7Card> deck : playedCards) {
            copy.playedCards.add(deck.copy());
        }
        for (int i = 0; i < getNPlayers(); i++) {
            copy.playerWonderBoard[i] = playerWonderBoard[i].copy();  // Every player's wonder boards are known
        }
        copy.AgeDeck = AgeDeck.copy();
        copy.discardPile = discardPile.copy();
        copy.currentAge = currentAge;


        if (getCoreGameParameters().partialObservable && playerId != -1) {
            // Player does not know the other players hands and discard pile
            // All the cards of other players and discard pile are shuffled
            Random r = new Random(copy.gameParameters.getRandomSeed());
            for (int i = 0; i < getNPlayers(); i++) {
                if (i != playerId) {
                    copy.AgeDeck.add(copy.playerHands.get(i)); // Groups other players cards (except for current player's hand) into the ageDeck (along with any cards that were not in the game at that age)
                }
            }
            copy.AgeDeck.add(copy.discardPile); // Groups the discard pile into the ageDeck
            copy.AgeDeck.shuffle(r); // Shuffle all the cards
            for (int i = 0; i < getNPlayers(); i++) {
                if (i != playerId) {
                    Deck<Wonders7Card> hand = copy.playerHands.get(i);
                    int nCards = hand.getSize();
                    hand.clear();  // Empties the accurate player hands, except for the next players hand
                    for (int j = 0; j < nCards; j++) {
                        hand.add(copy.AgeDeck.draw());  // Fills player hand from shuffled cards
                    }
                }
            }
            Deck<Wonders7Card> discPile = copy.discardPile;
            int nDisc = discPile.getSize();
            discPile.clear(); // Empties the accurate pile
            for (int i=0;i<nDisc;i++){
                discPile.add(copy.AgeDeck.draw()); // Fills the pile with the remaining shuffled cards in the ageDeck
            }

        }
        return copy;
    }

    public void _reset() {
        // reset any variables that would have been changed 
        // (and not directly reset in the ForwardModel._setup() method) to their initial state
        currentAge = 1;

        // Fills every player's hashmaps, so each player has 0 of each resource
        for (int i = 0; i < getNPlayers(); i++) { // For each
            for (Wonders7Constants.resources type : Wonders7Constants.resources.values()) {
                playerResources.get(i).put(type, 0);
            }
        }
    }

    @Override
    protected boolean _equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Wonders7GameState)) return false;
        if (!super.equals(o)) return false;
        Wonders7GameState that = (Wonders7GameState) o;
        return Objects.equals(playerResources, that.playerResources) &&
                Objects.equals(playerHands, that.playerHands) &&
                Objects.equals(playedCards, that.playedCards) &&
                Objects.equals(AgeDeck, that.AgeDeck) &&
                Objects.equals(discardPile, that.discardPile) &&
                Objects.equals(wonderBoardDeck, that.wonderBoardDeck) &&
                Arrays.equals(playerWonderBoard, that.playerWonderBoard) &&
                Arrays.equals(turnActions, that.turnActions) &&
                currentAge == that.currentAge;

    }


    @Override
    public double _getHeuristicScore(int playerId) {
        // Implement a rough-and-ready heuristic (or a very sophisticated one)
        // that gives an estimate of how well a player is doing in the range [-1, +1], 
        // where -1 is immediate loss, and +1 is immediate win
        // This is used by a number of agents as a default, including MCTS, to value the current state. If the game has a direct score, then the simplest approach here is just to scale this in line with some plausible maximum
        // see DominionGameState._getHeuristicScore() for an example of this; and contrast to DominionHeuristic for a more sophisticated approach
        return new Wonders7Heuristic().evaluateState(this, playerId);
    }

    @Override
    public double getGameScore(int playerId) {
        // return the players score for the current game state.
        // This may not apply for all games

        if (currentAge == 4) return playerResources.get(playerId).get(Wonders7Constants.resources.victory);


        List<HashMap<Wonders7Constants.resources, Integer>> playerResourcesCopy = new ArrayList<>();
        for (HashMap<Wonders7Constants.resources, Integer> map : playerResources) {
            playerResourcesCopy.add(new HashMap<>(map));
        }
        // Evaluate military conflicts
        int nextplayer = (playerId +1)% getNPlayers();
        if(playerResourcesCopy.get(playerId).get(Wonders7Constants.resources.shield) > playerResourcesCopy.get(nextplayer).get(Wonders7Constants.resources.shield)){ // IF PLAYER i WINS
            playerResourcesCopy.get(playerId).put(Wonders7Constants.resources.victory,  playerResourcesCopy.get(playerId).get(Wonders7Constants.resources.victory)+(2*currentAge-1)); // 2N-1 POINTS FOR PLAYER i
            playerResourcesCopy.get(nextplayer).put(Wonders7Constants.resources.victory,  playerResourcesCopy.get(nextplayer).get(Wonders7Constants.resources.victory)-1); // -1 FOR THE PLAYER i+1
        }
        else if (playerResourcesCopy.get(playerId).get(Wonders7Constants.resources.shield) < playerResourcesCopy.get(nextplayer).get(Wonders7Constants.resources.shield)){ // IF PLAYER i+1 WINS
            playerResourcesCopy.get(playerId).put(Wonders7Constants.resources.victory,  playerResourcesCopy.get(playerId).get(Wonders7Constants.resources.victory)-1);// -1 POINT FOR THE PLAYER i
            playerResourcesCopy.get(nextplayer).put(Wonders7Constants.resources.victory,  playerResourcesCopy.get(nextplayer).get(Wonders7Constants.resources.victory)+(2*currentAge-1));// 2N-1 POINTS FOR PLAYER i+1
        }

        // Treasury
        playerResourcesCopy.get(playerId).put(Wonders7Constants.resources.victory, playerResourcesCopy.get(playerId).get(Wonders7Constants.resources.victory)+playerResourcesCopy.get(playerId).get(Wonders7Constants.resources.coin)/3);
        // Scientific
        playerResourcesCopy.get(playerId).put(Wonders7Constants.resources.victory, playerResourcesCopy.get(playerId).get(Wonders7Constants.resources.victory)+(int)Math.pow(playerResourcesCopy.get(playerId).get(Wonders7Constants.resources.cog),2));
        playerResourcesCopy.get(playerId).put(Wonders7Constants.resources.victory, playerResourcesCopy.get(playerId).get(Wonders7Constants.resources.victory)+(int)Math.pow(playerResourcesCopy.get(playerId).get(Wonders7Constants.resources.compass),2));
        playerResourcesCopy.get(playerId).put(Wonders7Constants.resources.victory, playerResourcesCopy.get(playerId).get(Wonders7Constants.resources.victory)+(int)Math.pow(playerResourcesCopy.get(playerId).get(Wonders7Constants.resources.tablet),2));
        playerResourcesCopy.get(playerId).put(Wonders7Constants.resources.victory, playerResourcesCopy.get(playerId).get(Wonders7Constants.resources.victory)+7*Math.min(Math.min(playerResourcesCopy.get(playerId).get(Wonders7Constants.resources.cog),playerResourcesCopy.get(playerId).get(Wonders7Constants.resources.compass)),playerResourcesCopy.get(playerId).get(Wonders7Constants.resources.tablet))); // Sets of different science symbols

        return playerResourcesCopy.get(playerId).get(Wonders7Constants.resources.victory);
    }


    public int countBuiltWonderStages(int playerId) {
        Deck<Wonders7Board> allBoards;
        allBoards = new Deck<>("temp", VISIBLE_TO_ALL);
        for (int i=0; i<getNPlayers(); i++) allBoards.add(playerWonderBoard[i]);

        // Use stream to get the sum of all BuildStage's in the wgs
        int totalWonderStages = allBoards.stream()
                .mapToInt(board -> board.getWonderStage() - 1) // WonderStage starts value at 1
                .sum();

        return totalWonderStages;
    }

    public int nCardsOfType(Wonders7Card.Wonder7CardType type, int playerId) {
        Deck<Wonders7Card> allCards;
        allCards = new Deck<>("temp", VISIBLE_TO_ALL);
        for (int i=0; i<getNPlayers(); i++) {allCards.add(getPlayedCards(i));}
        return (int) allCards.stream().filter(c -> c.getCardType() == type).count(); // Counts all the cards (of type) that have been played in the current gs
    }

    public int getCurrentAge() {
        return currentAge;
    }

    public Deck<Wonders7Card> getAgeDeck() {
        return AgeDeck;
    }

    public Deck<Wonders7Card> getPlayerHand(int index) {
        return playerHands.get(index);
    } // Get player hand

    public List<Deck<Wonders7Card>> getPlayerHands() {
        return playerHands;
    }

    public Deck<Wonders7Card> getPlayedCards(int index) {
        return playedCards.get(index);
    } // Get player 'played' cards

    public Deck<Wonders7Card> getDiscardPile() {
        return discardPile;
    }

    public AbstractAction getTurnAction(int index) {
        return turnActions[index];
    }

    public void setTurnAction(int index, AbstractAction action) {
        turnActions[index] = action;
    }

    public Wonders7Board getPlayerWonderBoard(int index) {
        return playerWonderBoard[index];
    }

    public void setPlayerWonderBoard(int index, Wonders7Board wonder) {
        playerWonderBoard[index] = wonder;
    }


    public List<HashMap<Wonders7Constants.resources, Integer>> getAllPlayerResources() {
        return playerResources;
    } // Return all player's resources hashmap

    public HashMap<Wonders7Constants.resources, Integer> getPlayerResources(int index) {
        return playerResources.get(index);
    } // Return players resource hashmap

    public int getResource(int player, Wonders7Constants.resources resource){
        return playerResources.get(player).get(resource);
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), playerResources, playerHands, playedCards, AgeDeck, discardPile, wonderBoardDeck, Arrays.hashCode(playerWonderBoard), Arrays.hashCode(turnActions));
    }
}