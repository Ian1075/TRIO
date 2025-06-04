package game;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private GameState gameState;
    private List<Card> currentFlip;

    public GameController(GameState gameState) {
        this.gameState = gameState;
        this.currentFlip = new ArrayList<>();
    }

    // 1. From Table（ index）
    public Card flipTableCard(int index) {
        Card card = gameState.getTableCards().get(index);
        currentFlip.add(card);
        System.out.println("Flipped table card: " + card);
        return card;
    }

    // 2. Min
    public Card flipPlayerMinCard(int playerIndex) {
        Card card = gameState.getPlayers().get(playerIndex).getMinCard();
        currentFlip.add(card);
        System.out.println("Flipped " + gameState.getPlayers().get(playerIndex).getName() + "'s MIN card: " + card);
        return card;
    }

    // 3. Max
    public Card flipPlayerMaxCard(int playerIndex) {
        Card card = gameState.getPlayers().get(playerIndex).getMaxCard();
        currentFlip.add(card);
        System.out.println("Flipped " + gameState.getPlayers().get(playerIndex).getName() + "'s MAX card: " + card);
        return card;
    }

    public List<Card> getCurrentFlip() {
        return currentFlip;
    }

    public void resetFlip() {
        currentFlip.clear();
    }
}
