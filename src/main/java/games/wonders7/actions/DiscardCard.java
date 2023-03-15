package games.wonders7.actions;

import core.actions.DrawCard;
import games.wonders7.Wonders7GameState;
import games.wonders7.cards.Wonder7Card;

import java.util.Set;

public class DiscardCard extends DrawCard {
    private String cardName;

    public DiscardCard(String cardName){
        this.cardName = cardName;
    }

    public boolean execute(Wonders7GameState wgs){
        // Finds card being removed in player Hand
        int index=0; // The index of the card in hand
        for (int i=0; i<wgs.getPlayerHand(wgs.getCurrentPlayer()).getSize(); i++){ // Goes through each card in the playerHand
            if (cardName.equals(wgs.getPlayerHand(wgs.getCurrentPlayer()).get(i).cardName)){ // If cardName is the one searching for (being played)
                index = i;
            }
        }
        Wonder7Card card = wgs.getAgeDeck().get(index); // Card being removed

        // Player gets 3 coins from discarding card
        int playerValue = wgs.getPlayerResources(wgs.getCurrentPlayer()).get(Wonder7Card.resources.coin); // No. Coins player has
        wgs.getPlayerResources(wgs.getCurrentPlayer()).put(Wonder7Card.resources.coin,  playerValue+3); // Adds 3 coins to player coin count
        // Removes card from hand and adds to discarded cards deck
        wgs.getPlayerHand(wgs.getCurrentPlayer()).remove(card); // remove
        wgs.getDiscardedCards().add(card); // add

        return true;
    }


}
