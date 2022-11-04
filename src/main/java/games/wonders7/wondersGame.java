package games.wonders7;// wonders game class

import core.AbstractForwardModel;
import core.AbstractGameState;
import core.AbstractPlayer;
import core.Game;
import games.GameType;

import java.util.List;

class wondersGame extends Game {

    public wondersGame(GameType type, List<AbstractPlayer> players, AbstractForwardModel realModel, AbstractGameState gameState) {
        super(type, players, realModel, gameState);
    }

    public static void main(String[] args) {
    // ...use this for running the game either with or without a GUI. See any existing game for ideas here...
    }
}