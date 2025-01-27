package games.wonders7.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.actions.DrawCard;
import games.wonders7.Wonders7Constants;
import games.wonders7.Wonders7GameState;
import games.wonders7.cards.Wonders7Board;
import games.wonders7.cards.Wonders7Card;

import java.util.Objects;
import java.util.Set;

public class SpecialEffect extends DrawCard {

    public String cardName;

    // Player chooses card to play
    public SpecialEffect(String cardName){
        super();
        this.cardName = cardName;

    }


    @Override
    public boolean execute(AbstractGameState gameState) {
        super.execute(gameState);
        Wonders7GameState wgs = (Wonders7GameState) gameState;


        Wonders7Card card = findCard(wgs); // Card being selected for statue
        Wonders7Card discardedCard = findCardDiscPile(wgs); // Card being selected for mausoleum

        Wonders7Board board = wgs.getPlayerWonderBoard(wgs.getCurrentPlayer());
        switch (board.type){
            case gardens:
                // Player chooses between choice of 3 scientific materials
                if (cardName.equals("Tablet")) {
                    int playerValue = wgs.getPlayerResources(wgs.getCurrentPlayer()).get(Wonders7Constants.resources.tablet); // Number of resource the player owns
                    wgs.getPlayerResources(wgs.getCurrentPlayer()).put(Wonders7Constants.resources.tablet, playerValue + 1);
                }
                if (cardName.equals("Compass")) {
                    int playerValue = wgs.getPlayerResources(wgs.getCurrentPlayer()).get(Wonders7Constants.resources.compass); // Number of resource the player owns
                    wgs.getPlayerResources(wgs.getCurrentPlayer()).put(Wonders7Constants.resources.compass, playerValue + 1);
                }
                if (cardName.equals("Cog")) {
                    int playerValue = wgs.getPlayerResources(wgs.getCurrentPlayer()).get(Wonders7Constants.resources.cog); // Number of resource the player owns
                    wgs.getPlayerResources(wgs.getCurrentPlayer()).put(Wonders7Constants.resources.cog, playerValue + 1);
                }

                wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).effectUsed = true;
                return true;
            case mausoleum:
                // Gives player resources produced from card
                Set<Wonders7Constants.resources> keys1 = discardedCard.resourcesProduced.keySet(); // Gets all the resources the card provides
                for (Wonders7Constants.resources resource: keys1){  // Goes through all keys for each resource
                    int cardValue = discardedCard.resourcesProduced.get(resource); // Number of resource the card provides
                    int playerValue = wgs.getPlayerResources(wgs.getCurrentPlayer()).get(resource); // Number of resource the player owns
                    wgs.getPlayerResources(wgs.getCurrentPlayer()).put(resource, playerValue + cardValue); // Adds the resources provided by the card to the players resource count
                }

                // remove the card from the discardPile to the player's playedDeck
                wgs.getDiscardPile().remove(card);
                wgs.getPlayedCards(wgs.getCurrentPlayer()).add(card);
                wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).effectUsed = true;
                return true;
            case statue:
                // Gives player resources produced from card
                Set<Wonders7Constants.resources> keys = card.resourcesProduced.keySet(); // Gets all the resources the card provides
                for (Wonders7Constants.resources resource: keys){  // Goes through all keys for each resource
                    int cardValue = card.resourcesProduced.get(resource); // Number of resource the card provides
                    int playerValue = wgs.getPlayerResources(wgs.getCurrentPlayer()).get(resource); // Number of resource the player owns
                    wgs.getPlayerResources(wgs.getCurrentPlayer()).put(resource, playerValue + cardValue); // Adds the resources provided by the card to the players resource count
                }

                // remove the card from the players hand to the player's playedDeck
                wgs.getPlayerHand(wgs.getCurrentPlayer()).remove(card);
                wgs.getPlayedCards(wgs.getCurrentPlayer()).add(card);
                wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).effectUsed = true;
                return true;
            default:
                break;
        }
        return true;
    }

    public Wonders7Card findCard(Wonders7GameState wgs){
        // Finds the played card
        int index=0; // The index of the card in hand
        for (int i=0; i<wgs.getPlayerHand(wgs.getCurrentPlayer()).getSize(); i++){ // Goes through each card in the playerHand
            if (cardName.equals(wgs.getPlayerHand(wgs.getCurrentPlayer()).get(i).cardName)){ // If cardName is the one searching for (being played)
                index = i;
            }
        }
        return wgs.getPlayerHand(wgs.getCurrentPlayer()).get(index); // Card being selected
    }
    public Wonders7Card findCardDiscPile(Wonders7GameState wgs){
        // Finds the discarded card
        int index=0; // The index of the card in discard pile
        for (int i=0; i<wgs.getDiscardPile().getSize(); i++){ // Goes through each card in the discardPile
            if (cardName.equals(wgs.getDiscardPile().get(i).cardName)){ // If cardName is the one searching for (being played)
                index = i;
            }
        }
        return wgs.getDiscardPile().get(index); // Card being selected
    }
    @Override
    public String toString() {
        return "Special Effect " + cardName;
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpecialEffect)) return false;
        if (!super.equals(o)) return false;
        SpecialEffect specialEffect = (SpecialEffect) o;
        return Objects.equals(cardName, specialEffect.cardName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), cardName);
    }

    @Override
    public AbstractAction copy(){return new SpecialEffect(cardName);}
}
