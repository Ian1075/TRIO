import game.*;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Initialize deck and players
        Deck deck = new Deck();
        List<Player> players = new ArrayList<>();
        players.add(new Player("Player A"));
        players.add(new Player("Player B"));
        players.add(new Player("Player C"));
        players.add(new Player("Player D"));

        // Deal 7 cards to each player
        for (Player player : players) {
            player.receiveCards(deck.draw(7));
        }

        // Place 8 face-down cards on the table
        List<Card> table = deck.draw(8);

        // Setup game state
        GameState game = new GameState(players, table);
        GameController controller = new GameController(game);

        // Display initial hands
        System.out.println("=== Initial Hands ===");
        for (Player player : players) {
            System.out.println(player.getName() + ": " + player.getHand());
        }

        // Display table cards (face down)
        System.out.println("\n=== Table Cards (face-down) ===");
        for (int i = 0; i < table.size(); i++) {
            System.out.println("Position " + i + ": [Hidden]");
        }

        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            Player current = game.getCurrentPlayer();
            System.out.println("\n=== " + current.getName() + "'s turn ===");

            // Flip up to 3 cards
            controller.resetFlip();
            for (int i = 0; i < 3; i++) {
                System.out.println("Choose a flip source:");
                System.out.println("1. Table card");
                System.out.println("2. Other player's MIN card");
                System.out.println("3. Other player's MAX card");
                System.out.print("Your choice (or 0 to stop flipping): ");
                int choice = sc.nextInt();

                if (choice == 0) break;

                if (choice == 1) {
                    System.out.print("Enter table index (0–" + (table.size() - 1) + "): ");
                    int index = sc.nextInt();
                    if (index >= 0 && index < table.size()) {
                        controller.flipTableCard(index);
                    } else {
                        System.out.println("Invalid index.");
                        i--; // retry
                    }
                } else if (choice == 2 || choice == 3) {
                    System.out.print("Enter player index (0–3, excluding yourself=" + game.getCurrentPlayerIndex() + "): ");
                    int target = sc.nextInt();
                    if (target == game.getCurrentPlayerIndex() || target < 0 || target >= players.size()) {
                        System.out.println("Invalid target.");
                        i--; // retry
                        continue;
                    }

                    if (choice == 2) {
                        controller.flipPlayerMinCard(target);
                    } else {
                        controller.flipPlayerMaxCard(target);
                    }
                } else {
                    System.out.println("Invalid choice.");
                    i--; // retry
                }
            }

            // Show flipped cards
            System.out.println("Flipped cards this turn:");
            for (Card c : controller.getCurrentFlip()) {
                System.out.println(c);
            }

            // TODO: Check for Trio / Related Trio here

            // Move to next player
            game.nextPlayer();
        }

        sc.close();
    }
}