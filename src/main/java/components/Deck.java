package components;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import content.Property;
import content.PropertyString;
import content.PropertyStringArray;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utilities.Hash;
import utilities.Utils.ComponentType;

public class Deck extends Component implements IDeck {

    protected int capacity;

    protected ArrayList<Card> cards;

    protected Random rnd;

    public Deck()
    {
        super.type = ComponentType.DECK;
        cards = new ArrayList<>();
        rnd = new Random();
        properties = new HashMap<>();
    }

    protected Deck(Random rnd, int capacity)
    {
        super.type = ComponentType.DECK;
        cards = new ArrayList<>();
        this.rnd = rnd;
        this.capacity = capacity;
        properties = new HashMap<>();
    }

    protected void setCards(ArrayList<Card> cards) {this.cards = cards;}

    @Override
    public int getCapacity() {
        return capacity;
    }

    public void shuffle() {
        Collections.shuffle(cards, rnd);
    }

    public Card draw() {
        return pick(0);
    }

    public Card pick() {
        return pick(0);
    }

    public Card pick(int idx) {
        if(cards.size() > 0 && idx < cards.size()) {
            Card c = cards.get(idx);
            cards.remove(idx);
            return c;
        }
        return null;
    }

    public Card pickLast() {
        return pick(cards.size()-1);
    }

    @Override
    public Card peek() {
        return peek(0);
    }

    @Override
    public Card[] peek(int idx, int amount) {
        ArrayList<Card> cards = new ArrayList<>();
        for(int i = idx; i < idx+amount; ++i)
        {
            Card c = peek(i);
            if(c != null)
                cards.add(c);
        }
        return (Card[]) cards.toArray();
    }

    private Card peek(int idx)
    {
        if(cards.size() > 0 && idx < cards.size()) {
            Card c = cards.get(idx);
            return c;
        }
        return null;
    }

    public boolean add(Card c) {
        return add(c, 0);
    }

    public boolean add(Card c, int index) {
        if (cards.size() < capacity) {
            cards.add(index, c);
            return true;
        }
        return false;
    }


    @Override
    public IDeck copy()
    {
        Deck dp = new Deck();
        this.copyTo(dp);
        return dp;
    }

    public void copyTo(IDeck target)
    {
        Deck dp = (Deck) target;
        ArrayList<Card> newCards = new ArrayList<>();
        for (Card c : dp.cards)
        {
            newCards.add(c.copy());
        }
        dp.setCards(newCards);
        dp.capacity = capacity;
        dp.rnd = rnd;
    }

    // TODO: check for visibility?
    public ArrayList<Card> getCards() {
        return cards;
    }

    /**
     * Loads cards for a deck from a JSON file.
     * @param deck - deck to load in JSON format
     */
    public void loadDeck(JSONObject deck) {

        String name = (String) ( (JSONArray) deck.get("name")).get(1);
        properties.put(Hash.GetInstance().hash("name"), new PropertyString(name));

        JSONArray deckCards = (JSONArray) deck.get("cards");

        for(Object o : deckCards)
        {
            // Add nodes to board nodes
            JSONObject jsonCard = (JSONObject) o;
            Card newCard = (Card) parseComponent(new Card(), jsonCard);
            cards.add(newCard);
        }
    }

    public static List<Component> loadDecks(String filename)
    {
        JSONParser jsonParser = new JSONParser();
        ArrayList<Component> decks = new ArrayList<>();

        try (FileReader reader = new FileReader(filename)) {

            JSONArray data = (JSONArray) jsonParser.parse(reader);
            for(Object o : data) {
                Deck newDeck = new Deck();
                newDeck.loadDeck((JSONObject) o);
                decks.add(newDeck);
            }

        }catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return decks;
    }

}
