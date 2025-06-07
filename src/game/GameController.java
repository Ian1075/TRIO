package game;

import java.util.List;
import java.util.Scanner;

public class GameController {
    private GameState game;

    public GameController() {
        this.game = new GameState();
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
            System.out.println("Position " + i + ": Card{" + game.getTableCards().get(i).getNumber() + "}");
        }

        Scanner sc = new Scanner(System.in);

        while (true) {
            Player current = game.getCurrentPlayer();
            //if(game.getPlayers().indexOf(game.getCurrentPlayer()) != 0) 
            System.out.println("\n=== " + current.getName() + "'s turn ===");
            //System.err.println("\n" + game.getFlippedCards());
            int flipCount = 0;
            while (flipCount < 3) {
                current.setGameState(game);
                
                int choice[] = current.chooseCardToFlip();

                int type = choice[0];
                int target = choice[1];

                if (type == -1) {
                    flipTableCard(target);
                } else if (target == 1) {
                    flipPlayerMinCard(type);
                } else if (target == 2) {
                    flipPlayerMaxCard(type);
                }
                
                flipCount++;

                if (flipCount == 2) {
                    int num1 = game.getCurrentFlip().get(0).getNumber();
                    int num2 = game.getCurrentFlip().get(1).getNumber();
                    if (num1 != num2) {
                        System.out.println("First two cards have different numbers. Turn ends.");
                        break;
                    }
                }
                if (tryCheckTrio()) {
                    playerCollectTrio(current, game.getCurrentFlip());
                    break;
                }
            }

            for (Card c : game.getCurrentFlip()) {
                if (c.getSource() >= 0) {
                   game.getPlayers().get(c.getSource()).getHand().add(c);
                }
            }
            if(TrioChecker.hasWinningCondition(current.getTrio())) {
                System.out.println(current.getName() + " Wins");
                break;
            }
            resetFlip();

            game.nextPlayer();
        }
    }

    // 1. From Table（ index）
    public Card flipTableCard(int index) {
        Card card = game.getTableCards().get(index);
        game.getCurrentFlip().add(card);
        game.addFlippedCard(card);
        System.out.println("Flipped table card: " + card);
        return card;
    }

    // 2. Min
    public Card flipPlayerMinCard(int playerIndex) {
    Player player = game.getPlayers().get(playerIndex);
    Card card = player.getMinCard();
    if (card != null) {
        player.getHand().remove(card);
        game.getCurrentFlip().add(card);
        game.addFlippedCard(card);
        System.out.println("Flipped " + player.getName() + "'s MIN card: " + card);
    }
    return card;
}

    // 3. Max
    public Card flipPlayerMaxCard(int playerIndex) {
    Player player = game.getPlayers().get(playerIndex);
    Card card = player.getMaxCard();
    if (card != null) {
        player.getHand().remove(card);
        game.getCurrentFlip().add(card);
        game.addFlippedCard(card);
        System.out.println("Flipped " + player.getName() + "'s MAX card: " + card);
    }
    return card;
}

    public void resetFlip() {
        game.getCurrentFlip().clear();
    }

    public boolean tryCheckTrio() {
        if (game.getCurrentFlip().size() == 3) {
            if (TrioChecker.isBasicTrio(game.getCurrentFlip())) {
                System.out.println("Basic TRIO formed!");
                return true;
            } else {
                System.out.println("Not a trio. Turn ends.");
                return false;
            }
        }
        return false;
    }

    public void playerCollectTrio(Player collector, List<Card> trioCards) {
        for (Card card : trioCards) {
            game.getTableCards().remove(card);
            for(Player p : game.getPlayers()) {
                p.getHand().remove(card);
            }
        }
        game.removeFlippedCards(trioCards);
        collector.collectTrio(trioCards);
        resetFlip();
    }
}
