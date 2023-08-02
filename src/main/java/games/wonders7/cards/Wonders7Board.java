package games.wonders7.cards;

import core.AbstractGameState;
import core.components.Card;
import games.wonders7.Wonders7Constants;
import games.wonders7.Wonders7GameState;

import java.util.*;

public class Wonders7Board extends Card {
    public enum wonder {
        colossus,

        lighthouse,

        temple,

        gardens,

        statue,

        mausoleum,

        pyramids
    }

    public final String wonderName;
    public final wonder type;
    public boolean effectUsed;
    public int wonderStage;
    public final Wonders7Constants.resources resourcesProduced; // Default wonder production

    // 2(n-1): Stage Costs types/Resource counts    2(n-1)+1: Stage production types/Resource counts
    public final Wonders7Constants.resources[] stageResourceTypes; // Each stage's resource types for cost and productions
    public final int[] stageResourceCounts; // Each stage's resource counts for cost and productions

    public Wonders7Board(wonder type, Wonders7Constants.resources[] stageResourceTypes, int[] stageResourceCounts) {
        super(type.toString());
        this.type = type;
        this.stageResourceTypes = stageResourceTypes;
        this.stageResourceCounts = stageResourceCounts;
        this.wonderStage = 1;
        this.effectUsed = true;

        switch (type){
            case colossus:
                this.wonderName = "The Colossus of Rhodes          ";
                this.resourcesProduced = Wonders7Constants.resources.ore;
                break;
            case lighthouse:
                this.wonderName = "The Lighthouse of Alexandria    ";
                this.resourcesProduced = Wonders7Constants.resources.glass;
                break;
            case temple:
                this.wonderName = "The Temple of Artemis in Ephesus";
                this.resourcesProduced = Wonders7Constants.resources.papyrus;
                break;
            case gardens:
                this.wonderName = "The Hanging Gardens of Babylon  ";
                this.resourcesProduced = Wonders7Constants.resources.clay;
                break;
            case statue:
                this.wonderName = "The Statue of Zeus in Olympia   ";
                this.resourcesProduced = Wonders7Constants.resources.wood;
                break;
            case mausoleum:
                this.wonderName = "The Mausoleum of Halicarnassus  ";
                this.resourcesProduced = Wonders7Constants.resources.textile;
                break;
            case pyramids:
                this.wonderName = "The Pyramids of Giza            ";
                this.resourcesProduced = Wonders7Constants.resources.stone;
                break;
            default: throw new IllegalArgumentException("Wonder does not have a type");
        }
    }

    public Wonders7Board(wonder type, Wonders7Constants.resources[] stageResourceTypes, int[] stageResourceCounts, int componentID) {
        super(type.toString(),componentID);
        this.type = type;
        this.stageResourceTypes = stageResourceTypes;
        this.stageResourceCounts = stageResourceCounts;
        this.wonderStage = 1;
        this.effectUsed = true;

        switch (type){
            case colossus:
                this.wonderName = "The Colossus of Rhodes          ";
                this.resourcesProduced = Wonders7Constants.resources.ore;
                break;
            case lighthouse:
                this.wonderName = "The Lighthouse of Alexandria    ";
                this.resourcesProduced = Wonders7Constants.resources.glass;
                break;
            case temple:
                this.wonderName = "The Temple of Artemis in Ephesus";
                this.resourcesProduced = Wonders7Constants.resources.papyrus;
                break;
            case gardens:
                this.wonderName = "The Hanging Gardens of Babylon  ";
                this.resourcesProduced = Wonders7Constants.resources.clay;
                break;
            case statue:
                this.wonderName = "The Statue of Zeus in Olympia   ";
                this.resourcesProduced = Wonders7Constants.resources.wood;
                break;
            case mausoleum:
                this.wonderName = "The Mausoleum of Halicarnassus  ";
                this.resourcesProduced = Wonders7Constants.resources.textile;
                break;
            case pyramids:
                this.wonderName = "The Pyramids of Giza            ";
                this.resourcesProduced = Wonders7Constants.resources.stone;
                break;
            default: throw new IllegalArgumentException("Wonder does not have a type");
        }
    }

//    @Override
//    public String toString() {
//        switch (type) {
//            case colossus:
 //           case temple:
 //           case lighthouse:
 //           case gardens:
 //           case statue:
 //           case mausoleum:
 //           case pyramids:
 //               return wonderName;
 //       }
//        return "null";
 //   }

    @Override
    public String toString() {
        String stages = "";
        for (int i = 0; i < 3; i++) {
            String cost = stageResourceTypes[2*i] + "="+ stageResourceCounts[2*i];
            String makes = stageResourceTypes[2*i+1] + "="+ stageResourceCounts[2*i+1];
            stages += "{" + (i+1) + ":" + (!cost.equals("") ? "cost=" + cost : "free")  + (!cost.equals("") && !makes.equals("")?"," : "") + (!makes.equals("")? "makes=" + makes : "") + "}  ";
            if (i != stageResourceCounts.length-1) stages += ", ";
        }
        return wonderName + (effectUsed ? "(used)" : "") + "[" + (wonderStage-1) + "]" +
                ",makes=" + (resourcesProduced) + " " + stages;
    }

    private String mapToStr(HashMap<Wonders7Constants.resources, Integer> m) {
        String s = "";
        for (Map.Entry<Wonders7Constants.resources, Integer> e: m.entrySet()) {
            if (e.getValue() > 0) s += e.getValue() + " " + e.getKey() + ",";
        }
        s += "]";
        if (s.equals("]")) return "";
        return s.replace(",]", "");
    }

    public boolean isPlayable(AbstractGameState gameState) {
        Wonders7GameState wgs = (Wonders7GameState) gameState;
        if (wonderStage == 4){return false;}
        // Checks if player can afford the cost of the card
        // Checks if players resource count is more or equal to stage resource count (i.e. the player can afford the card)
        return (wgs.getPlayerResources(wgs.getCurrentPlayer()).get(stageResourceTypes[2 * (wonderStage - 1)])) >= stageResourceCounts[2 * (wonderStage - 1)]; // Player cant afford card
    }

    public void changeStage(){
        wonderStage +=1;
    }
    public int getWonderStage() {return wonderStage;}

    @Override
    public Wonders7Board copy(){
        Wonders7Board board =  new Wonders7Board(type, stageResourceTypes, stageResourceCounts,componentID);
        board.wonderStage = wonderStage;
        board.effectUsed = effectUsed;
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Wonders7Board) {
            Wonders7Board card = (Wonders7Board) o;
            return card.wonderName.equals(wonderName) &&
                    card.wonderStage == wonderStage;
        }
        return false;
    }

    @Override
    public int hashCode(){return Objects.hash(super.hashCode(), wonderName); }

}
