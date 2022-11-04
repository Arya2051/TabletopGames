package games.wonders7;

import core.turnorders.TurnOrder;

class wondersTurnOrder extends TurnOrder {
    @Override
    protected void _reset() {

    }

    @Override
    protected TurnOrder _copy() {
        return null;
    }
    // track whose turn it is, and move on to the next player correctly
    // If the game has a simple alternating structure of each player taking their turn in order, 
    // then you can just use AlternatingTurnOrder
}