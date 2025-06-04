import game.Card;
import game.Deck;
import game.Player;

public class Main {
    public static void main(String[] args) {
        Deck deck = new Deck();
        deck.shuffle();

        Player player = new Player("User");

        // Draw 7 cards to player's hand
        for (int i = 0; i < 7; i++) {
            player.drawCard(deck);
        }

        player.printHand();

        Card maxCard = player.getMaxCard();
        Card minCard = player.getMinCard();

        System.out.println("Max card in hand: " + maxCard);
        System.out.println("Min card in hand: " + minCard);

        player.sortHand();
        player.printHand();
    }
}

