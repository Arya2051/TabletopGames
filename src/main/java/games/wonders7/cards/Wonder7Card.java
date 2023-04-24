package games.wonders7.cards;

import core.AbstractGameState;
import core.components.Card;
import games.wonders7.Wonders7Constants;
import games.wonders7.Wonders7GameState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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



 //   @Override
 //   public String toString() {
  //      // ADD COSTS
  //      switch (type) {
  //          case RawMaterials:
   //             return  "raw materials:" + " " + cardName;
   //         case ManufacturedGoods:
   //             return  "manufactured goods:" + " " + cardName;
   //         case CivilianStructures:
   //             return  "civilian structure:" + " " + cardName ;
   //         case ScientificStructures:
   //             return "scientific structure:" + " " + cardName;
   //         case CommercialStructures:
    //            return "commercial structure:" + " " + cardName;
    //        case MilitaryStructures:
    //            return "military structure:" + " " + cardName;
    //        case Guilds:
    //            return "guild:" + " " + cardName;
   //     }
   //     return null;
   // }

    @Override
    public String toString() {
        String cost = mapToStr(constructionCost);
        String makes = mapToStr(manufacturedGoods);
        return "{" + cardName +
                "(" + type + ")" +
                (!cost.equals("") ? ":cost=" + cost : ",free") +
                (!makes.equals("") ? ",makes=" + makes : "") + "}  ";
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

    // Checks if the card is free
    public boolean isFree(AbstractGameState gameState){
        Wonders7GameState wgs = (Wonders7GameState) gameState;

        // Checks if the player has an identical structure
        for (int i=0;i<wgs.getPlayedCards(wgs.getCurrentPlayer()).getSize();i++){
            if(wgs.getPlayedCards(wgs.getCurrentPlayer()).get(i).cardName == cardName){
                return false;
            }
        }

        // Checks if the player has prerequisite cards
        for (String prerequisite : prerequisiteCards){
            for (int i=0;i<wgs.getPlayedCards(wgs.getCurrentPlayer()).getSize();i++){
                if (prerequisite.equals(wgs.getPlayedCards(wgs.getCurrentPlayer()).get(i).cardName)){
                    return true;
                }
            }
        }
        return false;
    }

    // Checks if neighbour on the right can provide resources to build the structure
    public boolean isPayableR(AbstractGameState gameState){
        Wonders7GameState wgs = (Wonders7GameState) gameState;

        // Checks if the player has an identical structure
        for (int i=0;i<wgs.getPlayedCards(wgs.getCurrentPlayer()).getSize();i++){
            if(wgs.getPlayedCards(wgs.getCurrentPlayer()).get(i).cardName == cardName){
                return false;
            }
        }

        // Collects the resources player does not have
        Set<Wonders7Constants.resources> key = constructionCost.keySet();
        HashMap<Wonders7Constants.resources, Integer> neededResources = new HashMap<>();
        for (Wonders7Constants.resources resource : key) { // Goes through every resource the player needs
            if ((wgs.getPlayerResources(wgs.getCurrentPlayer()).get(resource)) < constructionCost.get(resource)) { // If the player does not have resource count, added to needed resources
                neededResources.put(resource, constructionCost.get(resource)-wgs.getPlayerResources(wgs.getCurrentPlayer()).get(resource));
            }
        }
        // Calculates the cost of resources
        int coinCost=0;
        key = neededResources.keySet();
        for (Wonders7Constants.resources resource : key)
            coinCost += 2*neededResources.get(resource); // For each unit of the resource needed
        if (coinCost>wgs.getPlayerResources(wgs.getCurrentPlayer()).get(Wonders7Constants.resources.coin)){return false;} // If player can pay the neighbour for the resources


        HashMap<Wonders7Constants.resources, Integer> neighbourResources = new HashMap<>(); // Resources offered by the neighbour
        // Resources provided by neighbour's wonder
        key = wgs.getPlayerWonderBoard(((wgs.getCurrentPlayer()+1)%wgs.getNPlayers())).manufacturedGoods.keySet();
        for (Wonders7Constants.resources resource : key) {
            neighbourResources.put(resource, wgs.getPlayerWonderBoard(((wgs.getCurrentPlayer()+1)%wgs.getNPlayers())).manufacturedGoods.get(resource));
        }
        // Resources provided by neighbour's raw materials
        for (int i=0;i<wgs.getPlayedCards((wgs.getCurrentPlayer()+1)%wgs.getNPlayers()).getSize();i++){
            if ((wgs.getPlayedCards((wgs.getCurrentPlayer()+1)%wgs.getNPlayers()).get(i).type==Wonder7CardType.RawMaterials)){
                key = wgs.getPlayedCards((wgs.getCurrentPlayer()+1)%wgs.getNPlayers()).get(i).manufacturedGoods.keySet();
                for (Wonders7Constants.resources resource : key) {
                    neighbourResources.put(resource, wgs.getPlayedCards((wgs.getCurrentPlayer()+1)%wgs.getNPlayers()).get(i).manufacturedGoods.get(resource));
                }
            }
        }
        // Resources provided by neighbour's manufactured goods
        for (int i=0;i<wgs.getPlayedCards((wgs.getCurrentPlayer()+1)%wgs.getNPlayers()).getSize();i++){
            if ((wgs.getPlayedCards((wgs.getCurrentPlayer()+1)%wgs.getNPlayers()).get(i).type==Wonder7CardType.ManufacturedGoods)){
                key = wgs.getPlayedCards((wgs.getCurrentPlayer()+1)%wgs.getNPlayers()).get(i).manufacturedGoods.keySet();
                for (Wonders7Constants.resources resource : key) {
                    neighbourResources.put(resource, wgs.getPlayedCards((wgs.getCurrentPlayer()+1)%wgs.getNPlayers()).get(i).manufacturedGoods.get(resource));
                }
            }
        }

        // Calculates combined resources of neighbour and player
        HashMap<Wonders7Constants.resources, Integer> combinedResources = new HashMap<>();
        key = neighbourResources.keySet();
        for (Wonders7Constants.resources resource : key) { // Goes through every resource provided by the neighbour
            combinedResources.put(resource, wgs.getPlayerResources(wgs.getCurrentPlayer()).get(resource)+neighbourResources.get(resource)); // Adds player and neighbour values into combined resources hashmap
        }

        // Checks if the combinedResources can pay the cost of the card
        key = constructionCost.keySet();
        for (Wonders7Constants.resources resource : key) {
            if (combinedResources.get(resource)== null){return false;}
            if ((combinedResources.get(resource)) < constructionCost.get(resource)) { // Checks whether player's resource (after 'buying' resources) count can now afford the card
                return false; // Player can't afford card with bought resources
            }
        }
        return true;
    }

    // Checks if neighbour on the left can provide resources to build the structure
    public boolean isPayableL(AbstractGameState gameState){
        Wonders7GameState wgs = (Wonders7GameState) gameState;

        // Checks if the player has an identical structure
        for (int i=0;i<wgs.getPlayedCards(wgs.getCurrentPlayer()).getSize();i++){
            if(wgs.getPlayedCards(wgs.getCurrentPlayer()).get(i).cardName == cardName){
                return false;
            }
        }

        // Collects the resources player does not have
        Set<Wonders7Constants.resources> key = constructionCost.keySet();
        HashMap<Wonders7Constants.resources, Integer> neededResources = new HashMap<>();
        for (Wonders7Constants.resources resource : key) { // Goes through every resource the player needs
            if ((wgs.getPlayerResources(wgs.getCurrentPlayer()).get(resource)) < constructionCost.get(resource)) { // If the player does not have resource count, added to needed resources
                neededResources.put(resource, constructionCost.get(resource)-wgs.getPlayerResources(wgs.getCurrentPlayer()).get(resource));
            }
        }
        // Calculates the cost of resources
        int coinCost=0;
        key = neededResources.keySet();
        for (Wonders7Constants.resources resource : key)
            coinCost += 2*neededResources.get(resource); // For each unit of the resource needed
        if (coinCost>wgs.getPlayerResources(wgs.getCurrentPlayer()).get(Wonders7Constants.resources.coin)){return false;} // If player can pay the neighbour for the resources


        HashMap<Wonders7Constants.resources, Integer> neighbourResources = new HashMap<>(); // Resources offered by the neighbour
        // Resources provided by neighbour's wonder
        key = wgs.getPlayerWonderBoard(Math.floorMod(wgs.getCurrentPlayer()-1, wgs.getNPlayers())).manufacturedGoods.keySet();
        for (Wonders7Constants.resources resource : key) {
            neighbourResources.put(resource, wgs.getPlayerWonderBoard(Math.floorMod(wgs.getCurrentPlayer()-1, wgs.getNPlayers())).manufacturedGoods.get(resource));
        }
        // Resources provided by neighbour's raw materials
        for (int i=0;i<wgs.getPlayedCards(Math.floorMod(wgs.getCurrentPlayer()-1, wgs.getNPlayers())).getSize();i++){
            if ((wgs.getPlayedCards(Math.floorMod(wgs.getCurrentPlayer()-1, wgs.getNPlayers())).get(i).type==Wonder7CardType.RawMaterials)){
                key = wgs.getPlayedCards(Math.floorMod(wgs.getCurrentPlayer()-1, wgs.getNPlayers())).get(i).manufacturedGoods.keySet();
                for (Wonders7Constants.resources resource : key) {
                    neighbourResources.put(resource, wgs.getPlayedCards(Math.floorMod(wgs.getCurrentPlayer()-1, wgs.getNPlayers())).get(i).manufacturedGoods.get(resource));
                }
            }
        }
        // Resources provided by neighbour's manufactured goods
        for (int i=0;i<wgs.getPlayedCards(Math.floorMod(wgs.getCurrentPlayer()-1, wgs.getNPlayers())).getSize();i++){
            if ((wgs.getPlayedCards(Math.floorMod(wgs.getCurrentPlayer()-1, wgs.getNPlayers())).get(i).type==Wonder7CardType.ManufacturedGoods)){
                key = wgs.getPlayedCards(Math.floorMod(wgs.getCurrentPlayer()-1, wgs.getNPlayers())).get(i).manufacturedGoods.keySet();
                for (Wonders7Constants.resources resource : key) {
                    neighbourResources.put(resource, wgs.getPlayedCards(Math.floorMod(wgs.getCurrentPlayer()-1, wgs.getNPlayers())).get(i).manufacturedGoods.get(resource));
                }
            }
        }

        // Calculates combined resources of neighbour and player
        HashMap<Wonders7Constants.resources, Integer> combinedResources = new HashMap<>();
        key = neighbourResources.keySet();
        for (Wonders7Constants.resources resource : key) { // Goes through every resource provided by the neighbour
            combinedResources.put(resource, wgs.getPlayerResources(wgs.getCurrentPlayer()).get(resource)+neighbourResources.get(resource)); // Adds player and neighbour values into combined resources hashmap
        }

        // Checks if the combinedResources can pay the cost of the card
        key = constructionCost.keySet();
        for (Wonders7Constants.resources resource : key) {
            if (combinedResources.get(resource)== null){return false;}
            if ((combinedResources.get(resource)) < constructionCost.get(resource)) { // Checks whether player's resource (after 'buying' resources) count can now afford the card
                return false; // Player can't afford card with bought resources
            }
        }
        return true;
    }

    public HashMap<Wonders7Constants.resources, Integer> empty(){
        HashMap<Wonders7Constants.resources, Integer> empty = new HashMap<>();
        return empty;
    }



}

