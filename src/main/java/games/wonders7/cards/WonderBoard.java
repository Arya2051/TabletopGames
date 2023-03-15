package games.wonders7.cards;

import core.components.Card;

public class WonderBoard extends Card {
    public enum wonder {
        colossus,

        lighthouse,

        temple,

        gardens,

        statue,

        mausoleum,

        pyramids
    }

    public final String wonderName;
    public WonderBoard(String wonderName) {

        this.wonderName = wonderName;
    }
}
