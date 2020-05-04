package games.explodingkittens.actions;

import actions.IAction;
import core.AbstractGameState;
import observations.IPrintable;
import components.IPartialObservableDeck;
import games.explodingkittens.ExplodingKittensGamePhase;
import games.explodingkittens.ExplodingKittensGameState;
import games.explodingkittens.cards.ExplodingKittenCard;
import turnorder.TurnOrder;


public class ChooseSeeTheFutureOrder implements IAction, IPrintable {
    private ExplodingKittenCard card1;
    private ExplodingKittenCard card2;
    private ExplodingKittenCard card3;
    private IPartialObservableDeck<ExplodingKittenCard> drawPile;
    private int playerID;

    public ChooseSeeTheFutureOrder(IPartialObservableDeck<ExplodingKittenCard> drawPile,
                                   ExplodingKittenCard card1, ExplodingKittenCard card2, ExplodingKittenCard card3,
                                   int playerID){
        this.drawPile = drawPile;
        this.card1 = card1;
        this.card2 = card2;
        this.card3 = card3;
        this.playerID = playerID;
    }

    @Override
    public boolean Execute(AbstractGameState gs, TurnOrder turnOrder) {
        drawPile.remove(card1);
        drawPile.remove(card2);
        drawPile.remove(card3);

        drawPile.add(card3);
        drawPile.add(card2);
        drawPile.add(card1);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < gs.getNPlayers(); j++){
                drawPile.setVisibility(i, j, false);        // other players don't know the order anymore
            }
            drawPile.setVisibility(i, playerID, true);      // this player knows the first three cards
        }
        ((ExplodingKittensGameState)gs).gamePhase = ExplodingKittensGamePhase.PlayerMove;
        return false;
    }

    public String toString(){
        return "Chosen card order: " + card1.cardType + ", " + card2.cardType + ", " + card3.cardType;
    }

    @Override
    public void PrintToConsole() {
        System.out.println(toString());
    }

}