package games.uno.actions;


import core.AbstractGameState;
import core.actions.IAction;
import core.components.Card;
import core.components.Deck;
import core.observations.IPrintable;
import games.uno.UnoGameState;
import games.uno.cards.UnoCard;

public class NoCards implements IAction, IPrintable {
    private final Deck<UnoCard> drawDeck;
    private final Deck<UnoCard> discardDeck;
    private final Deck<UnoCard> playerDeck;

    public NoCards(Deck<UnoCard> drawDeck, Deck<UnoCard> discardDeck, Deck<UnoCard> playerDeck){
        this.drawDeck    = drawDeck;
        this.discardDeck = discardDeck;
        this.playerDeck  = playerDeck;
    }

    // If the card drawn is playable, then play it
    @Override
    public boolean execute(AbstractGameState gs) {
        if (drawDeck.getSize() == 0)
            ((UnoGameState) gs).drawDeckEmpty();

        UnoCard card = drawDeck.draw();

        if (card.isPlayable((UnoGameState) gs)) {
            discardDeck.add(card);
            System.out.println("It can be played. " + card.toString());
        }
        else
            playerDeck.add(card);
        return true;
    }

    @Override
    public Card getCard() {
        return null;
    }

    @Override
    public void printToConsole() {
        System.out.println("No playable cards. You must draw a card.");
    }
}