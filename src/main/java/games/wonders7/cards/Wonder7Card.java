package games.wonders7.cards;

import core.AbstractGameState;
import core.components.Card;
import games.wonders7.Wonders7Constants;
import games.wonders7.Wonders7GameState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Wonder7Card extends Card {

    public enum Wonder7CardType {
        RawMaterials,

        ManufacturedGoods,

        CivilianStructures,

        ScientificStructures,

        CommercialStructures,

        MilitaryStructures,

        Guilds,

    }


    public final Wonder7CardType type;  // Different type of cards, brown cards, grey cards...)
    public final String cardName; // Name of card
    public final HashMap<Wonders7Constants.resources, Integer> constructionCost; // The resources required to construct structure
    public final HashMap<Wonders7Constants.resources, Integer> manufacturedGoods; // Resources the card creates
    //public final HashMap<Wonder7Card, Integer> prerequisite; // THE STRUCTURES REQUIRED TO BUILD CARD FOR FREE
    public final String[] prerequisiteCards;

    // A normal card with construction cost, produces resources
    public Wonder7Card(String name, Wonder7CardType type, HashMap<Wonders7Constants.resources,Integer> constructionCost, HashMap<Wonders7Constants.resources,Integer> manufacturedGoods) {
        super(name);
        this.cardName = name;
        this.type = type;
        this.constructionCost = constructionCost;
        this.manufacturedGoods = manufacturedGoods;
        this.prerequisiteCards = new String[0];
    }

    // Card has prerequisite cards
    public Wonder7Card(String name, Wonder7CardType type, HashMap<Wonders7Constants.resources,Integer> constructionCost, HashMap<Wonders7Constants.resources,Integer> manufacturedGoods, String[] prerequisiteCards) {
        super(name);
        this.cardName = name;
        this.type = type;
        this.constructionCost = constructionCost;
        this.manufacturedGoods = manufacturedGoods;
        this.prerequisiteCards = prerequisiteCards;
    }

    // A free card (no construction cost)
    public Wonder7Card(String name, Wonder7CardType type, HashMap<Wonders7Constants.resources,Integer> manufacturedGoods){
        super(name);
        this.cardName = name;
        this.type = type;
        this.constructionCost = empty(); // Card costs nothing
        this.manufacturedGoods = manufacturedGoods;
        this.prerequisiteCards = new String[0];
    }

    protected Wonder7Card(String name, Wonder7CardType type, HashMap<Wonders7Constants.resources,Integer> constructionCost, HashMap<Wonders7Constants.resources,Integer> manufacturedGoods, String[] prerequisiteCards, int componentID){
        super(name, componentID);
        this.cardName = name;
        this.type = type;
        this.constructionCost = constructionCost;
        this.manufacturedGoods = manufacturedGoods;
        this.prerequisiteCards = prerequisiteCards;
    }



    @Override
    public String toString() {
        // ADD COSTS
        switch (type) {
            case RawMaterials:
                return  "raw materials:" + " " + cardName;
            case ManufacturedGoods:
                return  "manufactured goods:" + " " + cardName;
            case CivilianStructures:
                return  "civilian structure:" + " " + cardName ;
            case ScientificStructures:
                return "scientific structure:" + " " + cardName;
            case CommercialStructures:
                return "commercial structure:" + " " + cardName;
            case MilitaryStructures:
                return "military structure:" + " " + cardName;
            case Guilds:
                return "guild:" + " " + cardName;
        }
        return null;
    }

    @Override
    public Wonder7Card copy(){
        return new Wonder7Card(cardName, type, constructionCost, manufacturedGoods,prerequisiteCards, componentID);
    }

    // Checks if player can pay the cost of the card or if the player is allowed to build the structure
    public boolean isPlayable(AbstractGameState gameState) {
        Wonders7GameState wgs = (Wonders7GameState) gameState;
        // Checks if the player has an identical structure
        for (int i=0;i<wgs.getPlayedCards(wgs.getCurrentPlayer()).getSize();i++){
            if(wgs.getPlayedCards(wgs.getCurrentPlayer()).get(i).cardName == cardName){
                return false;
            }
        }

        // Checks if player can afford the cost of the card
        Set<Wonders7Constants.resources> key = constructionCost.keySet(); //Gets the resources of the player
        for (Wonders7Constants.resources resource : key) { // Goes through every resource the player has
            if ((wgs.getPlayerResources(wgs.getCurrentPlayer()).get(resource)) < constructionCost.get(resource)) { // Checks if players resource count is more or equal to card resource count (i.e. the player can afford the card)
                return false; // Player cant afford card
            }
        }
        return true;
    }

    public boolean isFree(AbstractGameState gameState){
        Wonders7GameState wgs = (Wonders7GameState) gameState;
        // Checks if the player has prerequisite cards
        for (int i=0;i<wgs.getPlayedCards(wgs.getCurrentPlayer()).getSize();i++){
            for (String prerequisite : wgs.getPlayedCards(wgs.getCurrentPlayer()).get(i).prerequisiteCards){
                if(wgs.getPlayedCards(wgs.getCurrentPlayer()).get(i).cardName == prerequisite){
                    return true;
                }
            }
        }
        return false;
    }


    public HashMap<Wonders7Constants.resources, Integer> empty(){
        HashMap<Wonders7Constants.resources, Integer> empty = new HashMap<>();
        return empty;
    }



}

