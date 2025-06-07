package game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrioChecker {

    private static final Map<Integer, List<Integer>> CHAINS = new HashMap<>();
    static {
        CHAINS.put(1, List.of(6, 8));
        CHAINS.put(2, List.of(5, 9));
        CHAINS.put(3, List.of(4, 10));
        CHAINS.put(4, List.of(3, 11));
        CHAINS.put(5, List.of(2, 12));
        CHAINS.put(6, List.of(1));
        CHAINS.put(7, List.of()); // No chain
        CHAINS.put(8, List.of(1));
        CHAINS.put(9, List.of(2));
        CHAINS.put(10, List.of(3));
        CHAINS.put(11, List.of(4));
        CHAINS.put(12, List.of(5));
    }

    public static boolean isBasicTrio(List<Card> cards) {
        if (cards.size() != 3) return false;
        int num = cards.get(0).getNumber();
        return cards.get(1).getNumber() == num && cards.get(2).getNumber() == num;
    }

    public static boolean hasWinningCondition(List<Integer> completedTrios) {

        if (completedTrios.contains(7)) return true;

        int chainCount = 0;
        for (int number : completedTrios) {
            List<Integer> chains = CHAINS.getOrDefault(number, List.of());
            for (int chainNum : chains) {
                if (completedTrios.contains(chainNum)) {
                    chainCount++;
                    if (chainCount >= 2) return true;
                }
            }
        }
        return false;
    }
}