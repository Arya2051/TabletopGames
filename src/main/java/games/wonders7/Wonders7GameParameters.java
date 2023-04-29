package games.wonders7;

import core.AbstractGameState;
import core.AbstractParameters;

public class Wonders7GameParameters extends AbstractParameters {
    // The Parameters of the game should contain any Game constants.
    // Having them all in one place makes it easy to amend them, and also to tune them as part of Game Design.
    // For straightforward and simple examples, have a look at those for Diamant and DotsAndBoxes.
    //

    public int nWonderBoards = 7;
    public int nWonderCardsPerPlayer = 7;
    public int nAge1Cards = 49;
    public int nAge2Cards, nAge3Cards = 50;
    public int nConflictTokens = 46;
    public int nValue3Coins = 24;
    public int getnValue1Coins = 46;



    public Wonders7GameParameters(long seed) {
        super(seed);
    }

    @Override
    protected AbstractParameters _copy() {
        return new Wonders7GameParameters(System.currentTimeMillis());
    }

    @Override
    protected boolean _equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Wonders7GameParameters)) return false;
        if (!super.equals(o)) return false;
        Wonders7GameParameters that = (Wonders7GameParameters) o;
        return nWonderCardsPerPlayer == that.nWonderCardsPerPlayer;
    }
}