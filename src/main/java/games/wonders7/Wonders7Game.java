package games.wonders7;// wonders game class

import core.*;
import games.GameType;
import players.simple.OSLAPlayer;
import players.simple.RandomPlayer;

import java.util.ArrayList;
import java.util.List;

class Wonders7Game extends Game {

    public Wonders7Game(List<AbstractPlayer> players, AbstractParameters gameParam) {
        super(GameType.Wonders7, players, new Wonders7ForwardModel(), new Wonders7GameState(gameParam, players.size()));
    }

    public static void main(String[] args) {
        // ...use this for running the game either with or without a GUI. See any existing game for ideas here...
         AbstractParameters gameParameters = new Wonders7GameParameters(System.currentTimeMillis());
        ArrayList<AbstractPlayer> players = new ArrayList<>();
        players.add(new RandomPlayer());
        players.add(new RandomPlayer());

        Game game = new Wonders7Game(players, gameParameters);
        game.run();
    }
}