// import core.interfaces.IfeatureRepresentation; 
// import core.interfaces.IVectorObservation

class wondersGameState extends AbstractGameState{

    List<Component> _getAllComponents(){
        // return a list of all (parents, and those nested as well) components in your game state. The method is called after game setup, so you may assume all components are already created. Decks and Areas have all of their nested components automatically added.
    }

    void _copy(int playerId){
        // define a reduced, player-specific, copy of your game state
        // Including components that player with the given ID will see.
        // For example, some decks may be face down and unobservable to the player
        // All of the components in the observation should be copies of those in the game state 
        // (pay attention to any references that need reassigning)
        // For more detail see Hiding information

    }

    void _reset(){
        // reset any variables that would have been changed 
        // (and not directly reset in the ForwardModel._setup() method) to their initial state
    }
     
    int _getGameScore(int playerId){
        // return the players score for the current game state.
        // This may not apply for all games
    }

    int _getHeuristicScore(int playerId){
        // Implement a rough-and-ready heuristic (or a very sophisticated one) 
        // that gives an estimate of how well a player is doing in the range [-1, +1], 
        // where -1 is immediate loss, and +1 is immediate win
        // This is used by a number of agents as a default, including MCTS, to value the current state. If the game has a direct score, then the simplest approach here is just to scale this in line with some plausible maximum
        // see DominionGameState._getHeuristicScore() for an example of this; and contrast to DominionHeuristic for a more sophisticated approach
    }
}