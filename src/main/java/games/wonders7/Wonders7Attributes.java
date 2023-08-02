package games.wonders7;

import core.interfaces.IGameAttribute;
import core.AbstractGameState;
import core.actions.AbstractAction;
import games.wonders7.actions.BuildStage;
import games.wonders7.cards.Wonders7Card;

import java.util.function.*;



public enum Wonders7Attributes implements IGameAttribute {


    RAW((s, a) -> (s).nCardsOfType(Wonders7Card.Wonder7CardType.RawMaterials, s.getCurrentPlayer())),
    MANUFACTURED((s, a) -> (s).nCardsOfType(Wonders7Card.Wonder7CardType.ManufacturedGoods, s.getCurrentPlayer())),
    CIVILLIAN((s, a) -> (s).nCardsOfType(Wonders7Card.Wonder7CardType.CivilianStructures, s.getCurrentPlayer())),
    SCIENTIFIC((s, a) -> (s).nCardsOfType(Wonders7Card.Wonder7CardType.ScientificStructures, s.getCurrentPlayer())),
    COMMERCIAL((s, a) -> (s).nCardsOfType(Wonders7Card.Wonder7CardType.CommercialStructures, s.getCurrentPlayer())),
    MILITARY((s, a) -> (s).nCardsOfType(Wonders7Card.Wonder7CardType.MilitaryStructures, s.getCurrentPlayer())),
    WONDERSTAGES((s,a) -> s.countBuiltWonderStages(s.getCurrentPlayer()))

    ;


    private final BiFunction<Wonders7GameState, AbstractAction, Object> lambda;

    Wonders7Attributes(BiFunction<Wonders7GameState, AbstractAction, Object> lambda) {
        this.lambda = lambda;
    }

    @Override
    public Object get(AbstractGameState state, AbstractAction action) {
        return lambda.apply((Wonders7GameState) state, action);
    }

}



