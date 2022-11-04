package games.wonders7;

import core.AbstractParameters;

class wondersParams extends AbstractParameters {
    public wondersParams(long seed) {
        super(seed);
    }

    @Override
    protected AbstractParameters _copy() {
        return null;
    }

    @Override
    protected boolean _equals(Object o) {
        return false;
    }
    // The Parameters of the game should contain any Game constants. 
    // Having them all in one place makes it easy to amend them, and also to tune them as part of Game Design.
    // For straightforward and simple examples, have a look at those for Diamant and DotsAndBoxes.
    // 
}