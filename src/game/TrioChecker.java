package game;

import java.util.List;

public class TrioChecker {

    public static boolean isBasicTrio(List<Card> cards) {
        if (cards.size() != 3) return false;
        int num = cards.get(0).getNumber();
        return cards.get(1).getNumber() == num && cards.get(2).getNumber() == num;
    }
}