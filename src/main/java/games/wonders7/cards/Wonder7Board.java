package games.wonders7.cards;

import core.components.Card;

import java.util.HashMap;

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

    public final String wonderName;
    public final wonder type;
    //public final HashMap<Wonder7Board.resources, Integer> constructionCost1; // 1st stage of wonder
    //public final HashMap<Wonder7Board.resources, Integer> constructionCost2; // 2nd
    //public final HashMap<Wonder7Board.resources, Integer> constructionCost3; // 3rd
    public final HashMap<Wonder7Board.resources, Integer> manufacturedGoods; // Resources the card creates

    public Wonder7Board(wonder type) {
        this.type = type;
        switch (type){
            case colossus:
                this.wonderName = "The Colossus of Rhodes";
                this.manufacturedGoods = new HashMap<>();
                this.manufacturedGoods.put(resources.ore, 1);
                break;
            case lighthouse:
                this.wonderName = "The Lighthouse of Alexandria";
                this.manufacturedGoods = new HashMap<>();
                this.manufacturedGoods.put(resources.glass, 1);
                break;
            case temple:
                this.wonderName = "The Temple of Artemis in Ephesus" ;
                this.manufacturedGoods = new HashMap<>();
                this.manufacturedGoods.put(resources.papyrus, 1);
                break;
            case gardens:
                this.wonderName = "The Hanging Gardens of Babylon" ;
                this.manufacturedGoods = new HashMap<>();
                this.manufacturedGoods.put(resources.clay, 1);
                break;
            case statue:
                this.wonderName = "The Statue of Zeus in Olympia" ;
                this.manufacturedGoods = new HashMap<>();
                this.manufacturedGoods.put(resources.wood, 1);
                break;
            case mausoleum:
                this.wonderName = "The Mausoleum of Halicarnassus" ;
                this.manufacturedGoods = new HashMap<>();
                this.manufacturedGoods.put(resources.textile, 1);
                break;
            case pyramids:
                this.wonderName = "The Pyramids of Giza" ;
                this.manufacturedGoods = new HashMap<>();
                this.manufacturedGoods.put(resources.stone, 1);
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

    public void createBoard(){
        switch (type){
            case colossus:
                ; break;
            case lighthouse:
                ; break;
            case temple:
                ; break;
            case gardens:
                ; break;
            case statue:
                ; break;
            case mausoleum:
                ; break;
            case pyramids:
                ; break;
            default: ; break;
        }
    }



}
