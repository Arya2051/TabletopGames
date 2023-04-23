package games.wonders7.actions;

import core.AbstractGameState;
import core.actions.DrawCard;
import games.wonders7.Wonders7Constants;
import games.wonders7.Wonders7GameState;
import games.wonders7.cards.Wonder7Board;
import games.wonders7.cards.Wonder7Card;

import java.util.Objects;
import java.util.Set;

public class SpecialEffect extends DrawCard {

    private String cardName;

    // Player chooses card to play
    public SpecialEffect(String cardName){
        super();
        this.cardName = cardName;

    }


    @Override
    public boolean execute(AbstractGameState gameState) {
        super.execute(gameState);
        Wonders7GameState wgs = (Wonders7GameState) gameState;

        // Finds the played card
        int index=0; // The index of the card in hand
        for (int i=0; i<wgs.getPlayerHand(wgs.getCurrentPlayer()).getSize(); i++){ // Goes through each card in the playerHand
            if (cardName.equals(wgs.getPlayerHand(wgs.getCurrentPlayer()).get(i).cardName)){ // If cardName is the one searching for (being played)
                index = i;
            }
        }
        Wonder7Card card = wgs.getPlayerHand(wgs.getCurrentPlayer()).get(index); // Card being selected

        Wonder7Board board = wgs.getPlayerWonderBoard(wgs.getCurrentPlayer());
        switch (board.type){
            case lighthouse:
            case mausoleum:
            case gardens:
                wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).effectUsed = true;
            case statue:
                // Gives player resources produced from card
                Set<Wonders7Constants.resources> keys = card.manufacturedGoods.keySet(); // Gets all the resources the card provides
                for (Wonders7Constants.resources resource: keys){  // Goes through all keys for each resource
                    int cardValue = card.manufacturedGoods.get(resource); // Number of resource the card provides
                    int playerValue = wgs.getPlayerResources(wgs.getCurrentPlayer()).get(resource); // Number of resource the player owns
                    wgs.getPlayerResources(wgs.getCurrentPlayer()).put(resource, playerValue + cardValue); // Adds the resources provided by the card to the players resource count
                }

                // remove the card from the players hand to the playedDeck
                wgs.getPlayerHand(wgs.getCurrentPlayer()).remove(card);
                wgs.getPlayedCards(wgs.getCurrentPlayer()).add(card);
                wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).effectUsed = true;
                return true;
            default:
                break;
        }
        return true;
    }

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
        if (!(o instanceof Wonder7Card)) return false;
        if (!super.equals(o)) return false;
        SpecialEffect specialEffect = (SpecialEffect) o;
        return Objects.equals(cardName, specialEffect.cardName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), cardName);
    }
}