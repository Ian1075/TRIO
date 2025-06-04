package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameController {
    private GameState game;
    private List<Card> currentFlip;

    public GameController() {
        this.game = new GameState();
        this.currentFlip = new ArrayList<>();
    }

    public void gameStart() {

        // Show each player's hand
        for (int i = 0; i < 4; i++) {
            System.out.println("Player " + (i + 1) + "'s hand:");
            for (Card c : game.getPlayers().get(i).getHand()) {
                System.out.println(c);
            }
            System.out.println();
        }

        // Show table cards
        System.out.println("Table cards (facedown):");
        for (int i = 0; i < game.getTableCards().size(); i++) {
            System.out.println("Position " + i + ": Card{}");
        }

        Scanner sc = new Scanner(System.in);

        while (true) {
            Player current = game.getCurrentPlayer();
            System.out.println("\n=== " + current.getName() + "'s turn ===");

            int flipCount = 0;
            while (flipCount < 3) {
                
                System.out.println("Flip from: 1.Table  2.Player Min  3.Player Max");
                int choice = sc.nextInt();

                if (choice == 1) {
                    System.out.print("Which table card (0-7)? ");
                    int index = sc.nextInt();
                    flipTableCard(index);
                } else if (choice == 2) {
                    System.out.print("Which player (0-3)? ");
                    int playerIndex = sc.nextInt();
                    flipPlayerMinCard(playerIndex);
                } else if (choice == 3) {
                    System.out.print("Which player (0-3)? ");
                    int playerIndex = sc.nextInt();
                    flipPlayerMaxCard(playerIndex);
                }

                flipCount++;
                if (tryCheckTrio()) {
                    playerCollectTrio(current, getCurrentFlip());
                    break;
                }
            }

            resetFlip();

            game.nextPlayer();
        }
    }

    // 1. From Table（ index）
    public Card flipTableCard(int index) {
        Card card = game.getTableCards().get(index);
        currentFlip.add(card);
        System.out.println("Flipped table card: " + card);
        return card;
    }

    // 2. Min
    public Card flipPlayerMinCard(int playerIndex) {
        Card card = game.getPlayers().get(playerIndex).getMinCard();
        currentFlip.add(card);
        System.out.println("Flipped " + game.getPlayers().get(playerIndex).getName() + "'s MIN card: " + card);
        return card;
    }

    // 3. Max
    public Card flipPlayerMaxCard(int playerIndex) {
        Card card = game.getPlayers().get(playerIndex).getMaxCard();
        currentFlip.add(card);
        System.out.println("Flipped " + game.getPlayers().get(playerIndex).getName() + "'s MAX card: " + card);
        return card;
    }

    public List<Card> getCurrentFlip() {
        return currentFlip;
    }

    public void resetFlip() {
        currentFlip.clear();
    }

    public boolean tryCheckTrio() {
        if (currentFlip.size() == 3) {
            if (TrioChecker.isBasicTrio(currentFlip)) {
                System.out.println("Basic TRIO formed!");
                currentFlip.clear();
                return true;
            } else {
                System.out.println("Not a trio. Turn ends.");
                resetFlip();
                return false;
            }
        }
        return false;
    }

    private void playerCollectTrio(Player collector, List<Card> trioCards) {
        for (Card card : trioCards) {
            game.removeTableCard(card.getId());
            for (Player p : game.getPlayers()) {
                p.removeHandCard(card.getId());
            }
        }
        collector.collectTrio(trioCards);
    }

}
