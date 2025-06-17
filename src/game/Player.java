package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class Player {
    private final String name;
    protected  List<Card> hand;
    private final List<Integer> completedTrios;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.completedTrios = new ArrayList<>();
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
        Collections.sort(hand, Comparator
        .comparingInt(Card::getNumber)
        .thenComparingInt(Card::getId));
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

    public void collectTrio(List<Card> cards) {
        if (cards == null || cards.size() != 3) return;
        completedTrios.add(cards.get(0).getNumber());
    }

    public List<Integer> getTrio() {
        return completedTrios;
    }

    public abstract int[] chooseCardToFlip();

    public abstract void setGameState(GameState game);
}
