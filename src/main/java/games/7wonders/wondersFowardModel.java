


class wondersFowardModel extends AbstractFowardModel{
// The rationale of the ForwardModel is that it contains the core game logic, while the GameState contains the underlying game data. 
// Usually this means that ForwardModel is stateless, and this is a good principle to adopt, but as ever there will always be exceptions.

    void _setup(AbstractGameState state){

    } 

    void _next(AbstractGameState state, AbstractAction action){
        action.execute(state); //Apply the given action to the game state. This logic should be in the AbstractAction.execute() method, which well get to later, so this should be as simple as action.execute(state) 
        // Execute any other required game rules (e.g. change the phase of the game);
        // Check for game end;
        // Move to the next player (if required, and if the game has not ended). This is usually achieved with state.getTurnOrder().endPlayerTurn(state), with this logic encapsulated in FoobarTurnOrder.
    }

    // In the _computeAvailableActions(AbstractGameState gameState) method, return a list with all actions available for the current player, in the context of the game state object
    // In the _copy() method, return a new instance of the Forward Model object with any variables copied.
    // You may override the endGame() method if your game requires any extra end of game computation (e.g. to update the status of players still in the game to winners).
    // !!!
    // Note: Forward model classes can instead extend from the core.rules.AbstractRuleBasedForwardModel.java abstract class instead, if they wish to use the rule-based system instead; this class provides basic functionality and documentation for using rules and an already implemented _next() function.
    // !!!

    
}

