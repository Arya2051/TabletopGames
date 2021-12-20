package evaluation;

import core.AbstractParameters;
import core.AbstractPlayer;
import core.Game;
import core.ParameterFactory;
import core.interfaces.IGameListener;
import core.interfaces.IStatisticLogger;
import games.GameType;
import players.PlayerFactory;
import players.mcts.BasicMCTSPlayer;
import players.mcts.MCTSPlayer;
import players.rmhc.RMHCPlayer;
import players.simple.OSLAPlayer;
import players.simple.RandomPlayer;
import utilities.FileStatsLogger;

import java.io.File;
import java.util.*;

import static utilities.Utils.GameResult;
import static utilities.Utils.getArg;


public class RoundRobinTournament extends AbstractTournament {
    int[] pointsPerPlayer;
    LinkedList<Integer> agentIDs;
    private final int gamesPerMatchUp;
    private int matchUpsRun;
    public final boolean selfPlay;
    private int gameCounter;
    private FileStatsLogger dataLogger;
    protected List<String> listenerClasses;
    protected List<String> listenerFiles;

    /**
     * Main function, creates an runs the tournament with the given settings and players.
     */
    @SuppressWarnings({"UnnecessaryLocalVariable", "ConstantConditions"})
    public static void main(String[] args) {
        List<String> argsList = Arrays.asList(args);
        if (argsList.contains("--help") || argsList.contains("-h")) {
            System.out.println(
                    "There are a number of possible arguments:\n" +
                            "\tgame=          The name of the game to play. Defaults to Uno.\n" +
                            "\tnPlayers=      The number of players in each game. Defaults to 2.\n" +
                            "\tplayers=       The directory containing agent JSON files for the competing Players\n" +
                            "\t               If not specified, this defaults to very basic OSLA, RND, RHEA and MCTS players.\n" +
                            "\tgameParams=    (Optional) A JSON file from which the game parameters will be initialised.\n" +
                            "\tlogFile=       (Optional) The name of a log file to record the results of the Tournament\n" +
                            "\tgamesPerMatchup  Defaults to 1. The number of games to play for each combination.\n" +
                            "\tselfPlay=      If true, then multiple copies of the same agent can be in one game.\n" +
                            "\t               Defaults to false\n" +
                            "\tmode=          exhaustive|random - defaults to exhaustive.\n" +
                            "\t               exhaustive will iterate exhaustively through every possible matchup: \n" +
                            "\t               every possible player in every possible position. This can be excessive\n" +
                            "\t               for a large number of players, and random will have a random matchup \n" +
                            "\t               in each game, while ensuring no duplicates, and that all players get the\n" +
                            "\t               the same number of games in total.\n" +
                            "\tmatchups=      The total number of matchups to run if mode=random\n" +
                            "\tlistener=      (Optional) The full class name of an IGameListener implementation. \n" +
                            "\t               Defaults to utilities.GameReportListener. \n" +
                            "\t               A pipe-delimited string can be provided to gather many types of statistics \n" +
                            "\t               from the same set of games." +
                            "\tlogger=        (Optional) The full class name of an IStatisticsLogger implementation.\n" +
                            "\t               Defaults to FileStatsLogger. \n" +
                            "\tlistenerFile= (Optional) Will be used as the IStatisticsLogger log file (FileStatsLogger only)\n" +
                            "\t               A pipe-delimited list should be provided if each distinct listener should\n" +
                            "\t               use a different log file.\n");
            return;
        }
        /* 1. Settings for the tournament */
        GameType gameToPlay = GameType.valueOf(getArg(args, "game", "Uno"));
        int nPlayersPerGame = getArg(args, "nPlayers", 2);
        int nGamesPerMatchUp = getArg(args, "gamesPerMatchup", 1);
        boolean selfPlay = getArg(args, "selfPlay", false);
        String mode = getArg(args, "mode", "exhaustive");
        int totalMatchups = getArg(args, "matchups", 1000);
        String playerDirectory = getArg(args, "players", "");
        String logFile = getArg(args, "logFile", "");
        String gameParams = getArg(args, "gameParams", "");


        List<String> listenerClasses = new ArrayList<>(Arrays.asList(getArg(args, "listener", "utilities.GameReportListener").split("\\|")));
        List<String> listenerFiles = new ArrayList<>(Arrays.asList(getArg(args, "listenerFile", "GameReport.txt").split("\\|")));

        if (listenerClasses.size() > 1 && listenerFiles.size() > 1 && listenerClasses.size() != listenerFiles.size())
            throw new IllegalArgumentException("Lists of log files and listeners must be the same length");


        LinkedList<AbstractPlayer> agents = new LinkedList<>();
        if (!playerDirectory.equals("")) {
            File dir = new File(playerDirectory);
            if (dir.exists() && dir.isDirectory()) {
                for (String fileName : dir.list()) {
                    if (!fileName.endsWith(".json"))
                        continue;
                    System.out.println(fileName);
                    AbstractPlayer player = PlayerFactory.createPlayer(dir.getAbsolutePath() + File.separator + fileName);
                    agents.add(player);
                    player.setName(fileName.substring(0, fileName.indexOf(".")));
                }
            } else {
                throw new AssertionError("Specified dir argument is not a directory : " + playerDirectory);
            }
        } else {
            /* 2. Set up players */
            agents.add(new MCTSPlayer());
            agents.add(new BasicMCTSPlayer());
            agents.add(new RandomPlayer());
            agents.add(new RMHCPlayer());
            agents.add(new OSLAPlayer());
        }

        AbstractParameters params = ParameterFactory.createFromFile(gameToPlay, gameParams);

        // Run!
        RoundRobinTournament tournament = mode.equals("exhaustive") ?
                new RoundRobinTournament(agents, gameToPlay, nPlayersPerGame, nGamesPerMatchUp, selfPlay, params) :
                new RandomRRTournament(agents, gameToPlay, nPlayersPerGame, nGamesPerMatchUp, selfPlay, totalMatchups,
                        System.currentTimeMillis(), params);
        tournament.listenerFiles = listenerFiles;
        tournament.listenerClasses = listenerClasses;
        tournament.dataLogger = logFile.equals("") ? null : new FileStatsLogger(logFile, "\t", true);
        tournament.runTournament();
    }

    /**
     * Create a round robin tournament, which plays all agents against all others.
     *
     * @param agents          - players for the tournament.
     * @param gameToPlay      - game to play in this tournament.
     * @param playersPerGame  - number of players per game.
     * @param gamesPerMatchUp - number of games for each combination of players.
     * @param selfPlay        - true if agents are allowed to play copies of themselves.
     */
    public RoundRobinTournament(LinkedList<AbstractPlayer> agents, GameType gameToPlay, int playersPerGame,
                                int gamesPerMatchUp, boolean selfPlay, AbstractParameters gameParams) {
        super(agents, gameToPlay, playersPerGame, gameParams);
        if (!selfPlay && playersPerGame > this.agents.size()) {
            throw new IllegalArgumentException("Not enough agents to fill a match without self-play." +
                    "Either add more agents, reduce the number of players per game, or allow self-play.");
        }

        this.agentIDs = new LinkedList<>();
        for (int i = 0; i < this.agents.size(); i++)
            this.agentIDs.add(i);

        this.gamesPerMatchUp = gamesPerMatchUp;
        this.selfPlay = selfPlay;
        this.pointsPerPlayer = new int[agents.size()];
    }

    /**
     * Runs the round robin tournament.
     */
    @Override
    public void runTournament() {
        for (int g = 0; g < games.size(); g++) {
            System.out.println("Playing " + games.get(g).getGameType().name());

            LinkedList<Integer> matchUp = new LinkedList<>();
            createAndRunMatchUp(matchUp, g);

            for (int i = 0; i < this.agents.size(); i++) {
                System.out.printf("%s got %d points %n", agents.get(i), pointsPerPlayer[i]);
                System.out.printf("%s won %.1f%% of the games %n", agents.get(i), 100.0 * pointsPerPlayer[i] / (gamesPerMatchUp * matchUpsRun));
            }
        }
        if (dataLogger != null)
            dataLogger.processDataAndFinish();
    }

    /**
     * Recursively creates one combination of players and evaluates it.
     *
     * @param matchUp - current combination of players, updated recursively.
     * @param gameIdx - index of game to play with this match-up.
     */
    public void createAndRunMatchUp(LinkedList<Integer> matchUp, int gameIdx) {
        if (matchUp.size() == playersPerGame.get(gameIdx)) {
            evaluateMatchUp(matchUp, gameIdx);
        } else {
            for (Integer agentID : this.agentIDs) {
                if (selfPlay || !matchUp.contains(agentID)) {
                    matchUp.add(agentID);
                    createAndRunMatchUp(matchUp, gameIdx);
                    matchUp.remove(agentID);
                }
            }
        }
    }

    /**
     * Evaluates one combination of players.
     *
     * @param agentIDs - IDs of agents participating in this run.
     * @param gameIdx  - index of game to play in this evaluation.
     */
    protected void evaluateMatchUp(List<Integer> agentIDs, int gameIdx) {
     //   System.out.println("Evaluate " + agentIDs.toString());
        LinkedList<AbstractPlayer> matchUpPlayers = new LinkedList<>();
        for (int agentID : agentIDs)
            matchUpPlayers.add(this.agents.get(agentID));

        List<IGameListener> gameTrackers = new ArrayList<>();
        for (int l = 0; l < listenerClasses.size(); l++) {
            String logFile = listenerFiles.size() == 1 ? listenerFiles.get(0) : listenerFiles.get(l);
            IStatisticLogger logger = new FileStatsLogger(logFile);
            String listenerClass = listenerClasses.size() == 1 ? listenerClasses.get(0) : listenerClasses.get(l);
            IGameListener gameTracker = IGameListener.createListener(listenerClass, logger);
            games.get(gameIdx).addListener(gameTracker);
            gameTrackers.add(gameTracker);
        }

        // Run the game N = gamesPerMatchUp times with these players
        for (int i = 0; i < this.gamesPerMatchUp; i++) {
            gameCounter++;
            games.get(gameIdx).reset(matchUpPlayers);


            games.get(gameIdx).run(null);  // Always running tournaments without visuals

            for (IGameListener gameTracker : gameTrackers) {
                gameTracker.allGamesFinished();
            }
            games.get(gameIdx).clearListeners();

            GameResult[] results = games.get(gameIdx).getGameState().getPlayerResults();
            for (int j = 0; j < matchUpPlayers.size(); j++) {
                pointsPerPlayer[agentIDs.get(j)] += results[j] == GameResult.WIN ? 1 : 0;
            }
            if (dataLogger != null) {
                Game g = games.get(gameIdx);
                for (int p = 0; p < g.getPlayers().size(); p++) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("GameId", gameCounter);
                    data.put("Game", g.getGameType().name());
                    data.put("PlayerNumber", p);
                    data.put("PlayerType", g.getPlayers().get(p).toString());
                    data.put("Score", g.getGameState().getGameScore(p));
                    data.put("Result", g.getGameState().getPlayerResults()[p].toString());
                    dataLogger.record(data);
                    dataLogger.flush();
                }
            }
        }
        matchUpsRun++;
    }
}
