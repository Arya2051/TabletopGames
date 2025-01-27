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

public class BuildStage extends DrawCard {
    public String cardName;
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

        Wonders7Card card = findCard(wgs); // Card being selected

        // The second stage has been built, now the player can play their special action (if they have the wonder)
        if (wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).wonderStage == 2){
            Wonders7Board board = wgs.getPlayerWonderBoard(wgs.getCurrentPlayer());
            switch (board.type){
                case lighthouse:
                case mausoleum:
                case gardens:
                case statue:
                    wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).effectUsed = false;
                default:
                    break;
            }}


        // Gives player resources produced from stage
        if (wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).stageResourceTypes[2 * (wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).wonderStage - 1) + 1] != null) {
            int stageValue = wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).stageResourceCounts[2 * (wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).wonderStage - 1) + 1]; // Number of resource the stage provides
            int playerValue = wgs.getPlayerResources(wgs.getCurrentPlayer()).get(wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).stageResourceTypes[2 * (wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).wonderStage - 1) + 1]); // Number of resource the player owns
            wgs.getPlayerResources(wgs.getCurrentPlayer()).put(wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).stageResourceTypes[2 * (wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).wonderStage - 1) + 1], playerValue + stageValue); // Adds the resources provided by the stage to the players resource count
        }

        // remove the card from the players hand to the playedDeck
        wgs.getPlayerHand(wgs.getCurrentPlayer()).remove(card);
        wgs.getDiscardPile().add(card);

        wgs.getPlayerWonderBoard(wgs.getCurrentPlayer()).changeStage(); // Increases wonderstage value to the next stage
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

    @Override
    public String getString(AbstractGameState gameState) {return toString();}

    @Override
    public String toString() {return "Built stage " + wonderStage + " using " + cardName;}
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BuildStage)) return false;
        if (!super.equals(o)) return false;
        BuildStage buildStage = (BuildStage) o;
        return Objects.equals(cardName, buildStage.cardName) &&
                wonderStage == buildStage.wonderStage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), cardName);
    }

   @Override
    public AbstractAction copy() {return new BuildStage(cardName, wonderStage); }
}
