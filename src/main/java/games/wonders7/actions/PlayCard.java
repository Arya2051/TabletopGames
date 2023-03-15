package games.wonders7.actions;

import core.AbstractGameState;
import core.actions.DrawCard;
import games.wonders7.Wonders7GameState;
import games.wonders7.cards.Wonder7Card;

import java.util.Set;

public class PlayCard extends DrawCard {

    private String cardName;

    // Player chooses card to play
    public PlayCard(String cardName){
        super();
        this.cardName = cardName;

    }


    @Override
    public boolean execute(AbstractGameState gameState) {
        super.execute(gameState);
        Wonders7GameState wgs = (Wonders7GameState) gameState;

        // Finds the played card
        int index=0; // The index of the played card
        for (int i=0; i<wgs.getAgeDeck().getSize(); i++){ // Goes through each card in the drawDeck/AgeDeck
            if (cardName.equals(wgs.getAgeDeck().get(i).cardName)){ // If cardName is the one searching for (being played)
                index = i;
            }
        }
        Wonder7Card card = wgs.getAgeDeck().get(index);

        // Adds the resources the played card provides to player's resources
        Set<Wonder7Card.resources> keys = card.manufacturedGoods.keySet(); // Gets all the resources the card provides
        for (Wonder7Card.resources resource: keys){  // Goes through all keys for each resource
            int cardValue = card.manufacturedGoods.get(resource); // Number of resource the card provides
            int playerValue = wgs.getPlayerResources(wgs.getCurrentPlayer()).get(resource); // Number of resource the player owns
            wgs.getPlayerResources(wgs.getCurrentPlayer()).put(resource, cardValue + playerValue); // Adds the resources provided by the card to the players resource count
        }

        // remove the card from the players hand to the playedDeck
        wgs.getPlayerHand(gameState.getCurrentPlayer()).remove(card);
        wgs.getPlayedCards(gameState.getCurrentPlayer()).add(card);

        return true;
    }

}
