package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Player {
    private String name;
    private List<Card> hand;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    // Draw a card from the deck to player's hand
    public void drawCard(Deck deck) {
        Card card = deck.draw();
        if (card != null) {
            hand.add(card);
        }
    }

    // Get max card number in hand
    public Card getMaxCard() {
        if (hand.isEmpty()) return null;
        return Collections.max(hand, (a, b) -> Integer.compare(a.getNumber(), b.getNumber()));
    }

    // Get min card number in hand
    public Card getMinCard() {
        if (hand.isEmpty()) return null;
        return Collections.min(hand, (a, b) -> Integer.compare(a.getNumber(), b.getNumber()));
    }

    // Sort the player's hand in ascending order by card number.
    public void sortHand() {
        Collections.sort(hand, Comparator.comparingInt(Card::getNumber));
        System.out.println("Player's hand sorted:");
        for (Card c : hand) {
            System.out.println(c);
        }
    }

    // Print player's hand cards
    public void printHand() {
        System.out.println(name + "'s hand:");
        for (Card c : hand) {
            System.out.println(c);
        }
    }

    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return hand;
    }
}
