// GameState.java
import game.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GameState game = new GameState();

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
        GameController controller = new GameController(game);

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
                    controller.flipTableCard(index);
                } else if (choice == 2) {
                    System.out.print("Which player (0-3)? ");
                    int playerIndex = sc.nextInt();
                    controller.flipPlayerMinCard(playerIndex);
                } else if (choice == 3) {
                    System.out.print("Which player (0-3)? ");
                    int playerIndex = sc.nextInt();
                    controller.flipPlayerMaxCard(playerIndex);
                }

                flipCount++;
                if (controller.tryCheckTrio()) {
                    controller.playerCollectTrio(current, controller.getCurrentFlip());
                    break;
                }
            }

            controller.resetFlip();

            game.nextPlayer();
        }
    }
}