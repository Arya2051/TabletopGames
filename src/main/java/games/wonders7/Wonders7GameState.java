package games.wonders7;// import core.interfaces.IfeatureRepresentation;
// import core.interfaces.IVectorObservation

import core.AbstractGameState;
import core.AbstractParameters;
import core.actions.AbstractAction;
import core.components.Component;
import core.components.Deck;
import games.wonders7.cards.Wonder7Card;
import games.wonders7.cards.Wonder7Board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static games.GameType.Wonders7;

public class Wonders7GameState extends AbstractGameState {

    int currentAge; // int from 1,2,3 of current age
    int[] playerVictoryPoints;
    List<HashMap<Wonders7Constants.resources, Integer>> playerResources;
    List<Deck<Wonder7Card>> playerHands; // 7 Cards player has to choose from
    List<Deck<Wonder7Card>> playedCards; // Player used cards
    Deck<Wonder7Card> AgeDeck; // The 'draw deck' for the Age
    Deck<Wonder7Card> discardPile; // Discarded cards

    Deck<Wonder7Board> wonderBoardDeck; // The deck of wonder board that decide a players wonder

    Wonder7Board[] playerWonderBoard; // Every player's assigned Wonder Board

    AbstractAction[] turnActions; // The round's actions chosen by each player


    public Wonders7GameState(AbstractParameters gameParameters, int nPlayers) {
        super(gameParameters, new Wonders7TurnOrder(nPlayers), Wonders7);
        // Sets game in Age 1
        currentAge = 1;

        // Each player starts off with no resources
        playerResources = new ArrayList<>(); // New arraylist , containing different hashmaps for each player
        for (int i=0; i<getNPlayers(); i++){
            playerResources.add(new HashMap<>());

        }

        // Then fills every player's hashmaps, so each player has 0 of each resource
        for (int i=0; i<getNPlayers(); i++){ // For each
            for (Wonders7Constants.resources type: Wonders7Constants.resources.values()){playerResources.get(i).put(type, 0);}
        }


    }

    public List<Component> _getAllComponents(){
        // return a list of all (parents, and those nested as well) components in your game state. The method is called after game setup, so you may assume all components are already created. Decks and Areas have all of their nested components automatically added.

        return new ArrayList<>();
    }

    public AbstractGameState _copy(int playerId){
        // define a reduced, player-specific, copy of your game state
        // Including components that player with the given ID will see.
        // For example, some decks may be face down and unobservable to the player
        // All the components in the observation should be copies of those in the game state
        // (pay attention to any references that need reassigning)
        // For more detail see Hiding information
        Wonders7GameState copy = new Wonders7GameState(gameParameters.copy(), getNPlayers());
        copy.playerResources = new ArrayList<>();
        copy.playerHands = new ArrayList<>();
        copy.playedCards = new ArrayList<>();
        copy.playerWonderBoard = new Wonder7Board[copy.getNPlayers()];

        for (HashMap<Wonders7Constants.resources, Integer> map: playerResources) {
            copy.playerResources.add(new HashMap<>(map));
        }
        for (Deck<Wonder7Card> deck: playerHands) {
            copy.playerHands.add(deck.copy());
        }
        for (Deck<Wonder7Card> deck: playedCards) {
            copy.playedCards.add(deck.copy());
        }

        copy.AgeDeck = AgeDeck.copy();
        copy.discardPile = discardPile.copy();
        copy.currentAge = currentAge;
        copy.playerWonderBoard = playerWonderBoard;
        copy.turnActions = turnActions;

        return copy;
    }

    public void _reset(){
        // reset any variables that would have been changed 
        // (and not directly reset in the ForwardModel._setup() method) to their initial state
    }

    @Override
    protected boolean _equals(Object o) {
        return false;
    }

    int _getGameScore(int playerId){
        // return the players score for the current game state.
        // This may not apply for all games
        return playerId;
    }

    public double _getHeuristicScore(int playerId){
        // Implement a rough-and-ready heuristic (or a very sophisticated one) 
        // that gives an estimate of how well a player is doing in the range [-1, +1], 
        // where -1 is immediate loss, and +1 is immediate win
        // This is used by a number of agents as a default, including MCTS, to value the current state. If the game has a direct score, then the simplest approach here is just to scale this in line with some plausible maximum
        // see DominionGameState._getHeuristicScore() for an example of this; and contrast to DominionHeuristic for a more sophisticated approach
        return 0;
    }

    @Override
    public double getGameScore(int playerId) {
        return 0;
    }
    public int getCurrentAge(){return currentAge;}

    public Deck<Wonder7Card> getAgeDeck(){return AgeDeck;}

    public Deck<Wonder7Card> getPlayerHand(int index){return playerHands.get(index);} // Get player hand
    public List<Deck<Wonder7Card>> getPlayerHands(){return playerHands;}

    public Deck<Wonder7Card> getPlayedCards(int index){return playedCards.get(index);} // Get player 'played' cards

    public Deck<Wonder7Card>  getDiscardPile(){return discardPile;}
    public AbstractAction getTurnAction(int index){return turnActions[index];}
    public void setTurnAction(int index, AbstractAction action){turnActions[index] = action;}
    public Wonder7Board getPlayerWonderBoard(int index){return playerWonderBoard[index];}
    public void setPlayerWonderBoard(int index, Wonder7Board wonder){playerWonderBoard[index] = wonder;}


    public List<HashMap<Wonders7Constants.resources, Integer>> getAllPlayerResources(){return playerResources;} // Return all player's resources hashmap

    public HashMap<Wonders7Constants.resources, Integer> getPlayerResources(int index){return playerResources.get(index);} // Return players resource hashmap
}