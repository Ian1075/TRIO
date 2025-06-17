package game;
import java.util.ArrayList;
import java.util.List;
import players.AIPlayer;
import players.User;


// GameState.java
public class GameState {
    private List<Player> players;
    private List<Card> tableCards;
    private int currentPlayerIndex;
    private Deck deck;
    private List<Card> currentFlip;
    public Player winner = null;
     public String lastMoveDescription = "Game Started"; 

    private List<Card> flippedCards = new ArrayList<>();

    public GameState() {
        deck = new Deck();
        deck.shuffle();

        // Initialize players
        players = new ArrayList<>();
        // User
        User user = new User("You");
        for (int j = 0; j < 7; j++) {
            Card card = deck.draw();
            card.setSource(0);  // user = 0 
            user.getHand().add(card);
        }
        user.sortHand();
        players.add(user);
        // AI
        for (int i = 1; i < 4; i++) {
            AIPlayer ai = new AIPlayer("AI " + i);
            for (int j = 0; j < 7; j++) {
                Card card = deck.draw();
                card.setSource(i);  // source: player index
                ai.getHand().add(card);
            }
            ai.sortHand();
            players.add(ai);
        }

        // Initialize table cards (8 facedown)
        tableCards = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            Card card = deck.draw();
            card.setSource(-1);
            tableCards.add(card);
        }

        // Start from Player 1
        currentPlayerIndex = 0;
        this.currentFlip = new ArrayList<>();
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

    public List<Card> getFlippedCards() {
        return flippedCards;
    }

    public void addFlippedCard(Card card) {
        if (!flippedCards.contains(card)) {
            flippedCards.add(card);
        }
    }

    public void removeFlippedCards(List<Card> cards) {
        flippedCards.removeAll(cards);
    }

    public boolean isCardFlipped(Card card) {
        return flippedCards.contains(card);
    }

    public List<Card> getCurrentFlip() {
        return currentFlip;
    }

    public void setTableCards(List<Card> list) {
        tableCards = list;
    }
}

