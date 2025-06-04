package game;

import java.util.ArrayList;
import java.util.List;

// GameState.java
public class GameState {
    private List<Player> players;
    private List<Card> tableCards;
    private int currentPlayerIndex;
    private Deck deck;

    public GameState() {
        deck = new Deck();
        deck.shuffle();

        // Initialize players
        players = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Player p = new Player("Player " + (i + 1));
            for (int j = 0; j < 7; j++) {
                p.drawCard(deck);
            }
            p.sortHand(); // 之前你有做手牌排序
            players.add(p);
        }

        // Initialize table cards (8 facedown)
        tableCards = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            tableCards.add(deck.draw());
        }

        // Start from Player 1
        currentPlayerIndex = 0;
    }

    // Getters
    public List<Player> getPlayers() { return players; }
    public List<Card> getTableCards() { return tableCards; }
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }
    
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }
}

