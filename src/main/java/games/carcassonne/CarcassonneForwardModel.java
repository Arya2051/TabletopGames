package games.carcassonne;

import core.actions.AbstractAction;
import core.AbstractForwardModel;
import core.AbstractGameState;
import core.components.Deck;

import java.util.Arrays;
import java.util.List;

public class CarcassonneForwardModel extends AbstractForwardModel {

    public CarcassonneForwardModel(){

    }


    @Override
    public void setup(AbstractGameState firstState) {
        CarcassonneGameState cgs = (CarcassonneGameState) firstState;
        cgs.points = new int[cgs.getNPlayers()];
        cgs.unusedMeeple = new int[cgs.getNPlayers()];
        Arrays.fill(cgs.unusedMeeple, 7);
        cgs.gameBoard = new CarcassonneGameState.CarcassonneBoard();

        Deck<CarcassonneGameState.CarcassonneTile> drawPile = new Deck<>("Draw Pile");
        //drawPile.add(new CarcassonneTile());
    }

    @Override
    protected List<AbstractAction> _computeAvailableActions(AbstractGameState gameState) {
        return null;  // TODO
    }

    @Override
    protected AbstractForwardModel getCopy() {
        return new CarcassonneForwardModel();
    }

    @Override
    public void next(AbstractGameState currentState, AbstractAction action) {

    }

}
