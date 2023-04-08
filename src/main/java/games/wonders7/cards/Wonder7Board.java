package games.wonders7.cards;

import core.AbstractGameState;
import core.components.Card;
import games.wonders7.Wonders7Constants;
import games.wonders7.Wonders7GameState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Wonder7Board extends Card {
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
    public final HashMap<Wonders7Constants.resources, Integer> manufacturedGoods; // Resources the card creates
    public ArrayList<HashMap<Wonders7Constants.resources, Integer>> constructionCosts;
    public ArrayList<HashMap<Wonders7Constants.resources, Integer>> stageProduce;

    public Wonder7Board(wonder type, ArrayList<HashMap<Wonders7Constants.resources, Integer>> constructionCosts, ArrayList<HashMap<Wonders7Constants.resources, Integer>> stageProduce) {
        this.type = type;
        this.constructionCosts = new ArrayList<>();
        this.stageProduce = new ArrayList<>();
        for (HashMap<Wonders7Constants.resources, Integer> cost : constructionCosts){this.constructionCosts.add(cost);}
        for (HashMap<Wonders7Constants.resources, Integer> produce : stageProduce){this.stageProduce.add(produce);}
        this.wonderStage = 1;
        this.effectUsed = false;

        switch (type){
            case colossus:
                this.wonderName = "The Colossus of Rhodes          ";
                this.manufacturedGoods = new HashMap<>();
                this.manufacturedGoods.put(Wonders7Constants.resources.ore, 1);
                break;
            case lighthouse:
                this.wonderName = "The Lighthouse of Alexandria    ";
                this.manufacturedGoods = new HashMap<>();
                this.manufacturedGoods.put(Wonders7Constants.resources.glass, 1);
                break;
            case temple:
                this.wonderName = "The Temple of Artemis in Ephesus";
                this.manufacturedGoods = new HashMap<>();
                this.manufacturedGoods.put(Wonders7Constants.resources.papyrus, 1);
                break;
            case gardens:
                this.wonderName = "The Hanging Gardens of Babylon  ";
                this.manufacturedGoods = new HashMap<>();
                this.manufacturedGoods.put(Wonders7Constants.resources.clay, 1);
                break;
            case statue:
                this.wonderName = "The Statue of Zeus in Olympia   ";
                this.manufacturedGoods = new HashMap<>();
                this.manufacturedGoods.put(Wonders7Constants.resources.wood, 1);
                break;
            case mausoleum:
                this.wonderName = "The Mausoleum of Halicarnassus  ";
                this.manufacturedGoods = new HashMap<>();
                this.manufacturedGoods.put(Wonders7Constants.resources.textile, 1);
                break;
            case pyramids:
                this.wonderName = "The Pyramids of Giza            ";
                this.manufacturedGoods = new HashMap<>();
                this.manufacturedGoods.put(Wonders7Constants.resources.stone, 1);
                break;
            default: this.wonderName = ""; this.manufacturedGoods = new HashMap<>();
            break;
        }
    }

    @Override
    public String toString() {
        switch (type) {
            case colossus:
            case temple:
            case lighthouse:
            case gardens:
            case statue:
            case mausoleum:
            case pyramids:
                return wonderName;
        }
        return "null";
    }



    public boolean isPlayable(AbstractGameState gameState) {
        Wonders7GameState wgs = (Wonders7GameState) gameState;
        if (wonderStage == 4){return false;}
        // Checks if player can afford the cost of the card
        Set<Wonders7Constants.resources> key = constructionCosts.get(wonderStage-1).keySet(); //Gets the resources of the player
        for (Wonders7Constants.resources resource : key) {// Goes through every resource the player has
            if (!((wgs.getPlayerResources(wgs.getCurrentPlayer()).get(resource)) >= constructionCosts.get(wonderStage-1).get(resource))) { // Checks if players resource count is more or equal to card resource count (i.e. the player can afford the card)
                return false; // Player cant afford card
            }
        }
        return true;
    }

    public void changeStage(){
        wonderStage +=1;
    }

}