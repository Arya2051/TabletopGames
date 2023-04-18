package games.wonders7.actions;

import core.AbstractGameState;
import core.actions.DrawCard;
import games.wonders7.Wonders7Constants;
import games.wonders7.Wonders7GameState;
import games.wonders7.cards.Wonder7Board;
import games.wonders7.cards.Wonder7Card;

import java.util.Objects;
import java.util.Set;

public class BuildStage extends DrawCard {
    private String cardName;
    private int wonderStage;

    public BuildStage(String cardName, int wonderStage){
        super();
        this.cardName = cardName;
        this.wonderStage = wonderStage;
    }

    @Override
    public boolean execute(AbstractGameState gameState){
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

        // Removes the resource paid for stage
        Set<Wonders7Constants.resources> keys = wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).constructionCosts.get(wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).wonderStage-1).keySet(); // Gets all the resources the stage provides
        for (Wonders7Constants.resources resource: keys){  // Goes through all keys for each resource
            int stageValue =  wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).constructionCosts.get(wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).wonderStage-1).get(resource); // Number of resource the stage provides
            int playerValue = wgs.getPlayerResources(wgs.getCurrentPlayer()).get(resource); // Number of resource the player owns
            wgs.getPlayerResources(wgs.getCurrentPlayer()).put(resource, playerValue - stageValue); // Subtracts the resources cost of the stage from the players resource count
        }

        // FOR SECOND STAGE UNIQUE EFFECTS
        if (!wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).effectUsed){
            Wonder7Board board = wgs.getPlayerWonderBoard(wgs.getCurrentPlayer());
            switch (board.type){
                case lighthouse:
                case mausoleum:
                case gardens:
                    wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).effectUsed = true;
                    wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).changeStage(); // stage is no longer buildable
                    return true;
                case statue:
                    // Gives player resources produced from card
                    keys = card.manufacturedGoods.keySet(); // Gets all the resources the card provides
                    for (Wonders7Constants.resources resource: keys){  // Goes through all keys for each resource
                        int cardValue = card.manufacturedGoods.get(resource); // Number of resource the card provides
                        int playerValue = wgs.getPlayerResources(wgs.getCurrentPlayer()).get(resource); // Number of resource the player owns
                        wgs.getPlayerResources(wgs.getCurrentPlayer()).put(resource, playerValue + cardValue); // Adds the resources provided by the card to the players resource count
                    }

                    // remove the card from the players hand to the playedDeck
                    wgs.getPlayerHand(wgs.getCurrentPlayer()).remove(card);
                    wgs.getPlayedCards(wgs.getCurrentPlayer()).add(card);
                    wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).effectUsed = true;
                    wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).changeStage(); // stage is no longer buildable
                    return true;
                default:
                    break;
            }}


        // Gives player resources produced from stage
        keys = wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).stageProduce.get(wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).wonderStage-1).keySet(); // Gets all the resources the stage provides
        for (Wonders7Constants.resources resource: keys){  // Goes through all keys for each resource
            int stageValue =  wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).stageProduce.get(wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).wonderStage-1).get(resource); // Number of resource the stage provides
            int playerValue = wgs.getPlayerResources(wgs.getCurrentPlayer()).get(resource); // Number of resource the player owns
            wgs.getPlayerResources(wgs.getCurrentPlayer()).put(resource, playerValue + stageValue); // Adds the resources provided by the stage to the players resource count
        }

        // remove the card from the players hand to the playedDeck
        wgs.getPlayerHand(wgs.getCurrentPlayer()).remove(card);
        wgs.getDiscardPile().add(card);

        wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).changeStage(); // stage is no longer buildable
        return true;
    }

    public String toString() {
        return "Built stage " + wonderStage + " using " + cardName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Wonder7Card)) return false;
        if (!super.equals(o)) return false;
        BuildStage buildStage = (BuildStage) o;
        return Objects.equals(cardName, buildStage.cardName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), cardName);
    }
}
