package core.components;

import java.util.HashMap;

/**
 * An Area is a collection of core.components as Decks, Token, Dices, Cards and Boards.
 */
public class Area {
    protected HashMap<Integer, Component> components;
    protected int owner;   // -1 for main game area

    public Area(int owner) {
        this.components = new HashMap<>();
        this.owner = owner;
    }

    public Area copy() {
        Area new_area = new Area(owner);
        new_area.components = new HashMap<>();
        new_area.components.putAll(this.components);

        return new_area;
    }

    public int getOwner()           { return this.owner;  }
    public void setOwner(int owner) { this.owner = owner; }

    public HashMap<Integer, Component> getComponents()                                { return this.components;              }
    public Component                   getComponent(Integer key)                      { return this.components.get(key);     }
    public void                        addComponent(Integer key, Component component) { this.components.put(key, component); }

    public boolean setComponent(int hash, Component c) {
        Component old = components.get(hash);
        if (old.getType().equals(c.getType())) {
            components.put(hash, c);
            return true;
        }
        return false;
    }
}