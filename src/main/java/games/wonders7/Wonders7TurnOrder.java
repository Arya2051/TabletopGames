package games.wonders7;

import core.turnorders.AlternatingTurnOrder;
import core.turnorders.TurnOrder;

class Wonders7TurnOrder extends AlternatingTurnOrder {
    // track whose turn it is, and move on to the next player correctly
    // If the game has a simple alternating structure of each player taking their turn in order,
    // then you can just use AlternatingTurnOrder

    @Override
    protected void _reset() {

    }

    @Override
    protected TurnOrder _copy() {

        return new Wonders7TurnOrder();
    }

}