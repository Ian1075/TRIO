package game;
import java.util.*;

public class Deck {
    private final List<Card> cards = new ArrayList<>();
    private final Random random = new Random();

    public Deck() {
        int id = 0;
        for (int num = 1; num <= 12; num++) {
            for (int i = 0; i < 3; i++) {
                cards.add(new Card(num, id++));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards, random);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public Card draw() {
        if (cards.isEmpty()) return null;
        return cards.remove(0);
    }

    public int size() {
        return cards.size();
    }

    public void printDeck() {
        for (Card c : cards) {
            System.out.println(c);
        }
    }
}

