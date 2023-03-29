package games.wonders7.cards;

import core.AbstractGameState;
import core.AbstractParameters;
import core.components.Card;
import games.wonders7.Wonders7GameState;

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
    // ENUM OF MATERIALS
    public enum resources { //Another enum for costs

        wood,

        stone,

        clay,

        ore,

        glass,

        papyrus,

        textile,

        cog,

        compass,

        tablet,

        shield,

        victory,

        coin,

    }

    public final Wonder7CardType type;  // Different type of cards, brown cards, grey cards...)
    public final int nPlayers; // The number of players this card can be in the game for.
    public final int age; // Which deck/age the card belongs to
    public final String cardName; // Name of card
    public final HashMap<resources, Integer> constructionCost; // The resources required to construct structure
    public final HashMap<resources, Integer> manufacturedGoods; // Resources the card creates
    //public final HashMap<Wonder7Card, Integer> prerequisite; // THE STRUCTURES REQUIRED TO BUILD CARD FOR FREE

    // A normal card with construction cost, produces resources and is a prerequisite to another card
    public Wonder7Card(String name, Wonder7CardType type,  int nPlayers, int age, HashMap<resources,Integer> constructionCost, HashMap<resources,Integer> manufacturedGoods) {
        super(name);
        this.cardName = name;
        this.type = type;
        this.nPlayers = nPlayers;
        this.age = age;
        this.constructionCost = constructionCost;
        this.manufacturedGoods = manufacturedGoods;
    }
    // A free card (no construction cost)
    public Wonder7Card(String name, Wonder7CardType type, int nPlayers, int age, HashMap<resources,Integer> manufacturedGoods){
        super(name);
        this.cardName = name;
        this.type = type;
        this.nPlayers = nPlayers;
        this.age = age;
        this.constructionCost = empty(); // Card costs nothing
        this.manufacturedGoods = manufacturedGoods;
    }

    protected Wonder7Card(String name, Wonder7CardType type,  int nPlayers, int age, HashMap<resources,Integer> constructionCost, HashMap<resources,Integer> manufacturedGoods, int componentID){
        super(name, componentID);
        this.cardName = name;
        this.type = type;
        this.nPlayers = nPlayers;
        this.age = age;
        this.constructionCost = constructionCost;
        this.manufacturedGoods = manufacturedGoods;
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
        return new Wonder7Card(cardName, type, nPlayers, age, constructionCost, manufacturedGoods,componentID);
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
        Set<resources> key = constructionCost.keySet(); //Gets the resources of the player
        for (resources resource : key) {// Goes through every resource the player has
            if (!((wgs.getPlayerResources(wgs.getCurrentPlayer()).get(resource)) >= constructionCost.get(resource))) { // Checks if players resource count is more or equal to card resource count (i.e. the player can afford the card)
                return false; // Player cant afford card
            }
        }
        return true;
    }

    public HashMap<resources, Integer> empty(){
        HashMap<resources, Integer> empty = new HashMap<>();
        for (Wonder7Card.resources type: Wonder7Card.resources.values()){empty.put(type, 0);}

        return empty;
    }



}

