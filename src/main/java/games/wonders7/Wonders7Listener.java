package games.wonders7;

import core.AbstractGameState;
import core.CoreConstants;
import core.Game;
import core.actions.AbstractAction;
import core.interfaces.IGameAttribute;
import core.interfaces.IGameListener;
import core.interfaces.IStatisticLogger;
import utilities.ActionSimpleAttributes;
import utilities.Pair;
import utilities.TAGStatSummary;
import utilities.TAGSummariser;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static core.CoreConstants.GameEvents.*;

public class Wonders7Listener implements IGameListener {

    IStatisticLogger logger;
    IGameAttribute[] attributesToRecord;

    @Override
    public void allGamesFinished() {
        logger.processDataAndFinish();
    }

    public Wonders7Listener(IStatisticLogger logger) {
        this(logger, Wonders7Attributes.values());
    }

    public Wonders7Listener(IStatisticLogger logger, IGameAttribute... attributes) {
        this.logger = logger;
        this.attributesToRecord = attributes;
    }

    @Override
    public void onGameEvent(CoreConstants.GameEvents type, Game game) {
        // Here we do nothing, as we are only interested in Action events
        if (type == GAME_OVER) {
            Wonders7GameState state = (Wonders7GameState) game.getGameState();
            Map<String, Object> data = Arrays.stream(attributesToRecord)
                    .collect(Collectors.toMap(IGameAttribute::name, attr -> attr.get(state, null)));
            logger.record(data);
        }
    }

    @Override
    public void onEvent(CoreConstants.GameEvents type, AbstractGameState state, AbstractAction action) {

    }
}