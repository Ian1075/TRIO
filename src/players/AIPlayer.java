package players;

import game.Card;
import game.GameState;
import game.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AIPlayer extends Player{
    private GameState game;
    private Map<Integer, List<Card>> all;
    private List<Card> known;
    List<Card> currentFlip;
    public AIPlayer(String name) {
        super(name);
    }
    
    public int[] chooseCardToFlip() {
        int[] result = {0,0};
        all = new HashMap<>();
        all.put(0, new ArrayList<>(game.getPlayers().get(0).getHand()));
        all.put(1, new ArrayList<>(game.getPlayers().get(1).getHand()));
        all.put(2, new ArrayList<>(game.getPlayers().get(2).getHand()));
        all.put(3, new ArrayList<>(game.getPlayers().get(3).getHand()));
        all.put(-1, new ArrayList<>(game.getTableCards()));

        known = new ArrayList<>(game.getFlippedCards());
        
        for(Card c : hand) {
            if(!known.contains(c)) known.add(c);
        }
        //System.out.println(known);
        currentFlip = new ArrayList<>(game.getCurrentFlip());

        switch(currentFlip.size()) {
            case 0:
                List<Card> case2 = new ArrayList<>();
                for(Card c1 : getKnownAndFlipable()) {
                    updateMap(c1);
                    currentFlip.add(c1);

                    switch(flip1(c1)) {
                        case 3:
                        undoMap(c1);
                            result[0] = c1.getSource();
                            if(result[0] == -1) {
                                result[1] = all.get(-1).indexOf(c1);
                            } else {
                                if(all.get(result[0]).get(0) == c1) {
                                    result[1] = 1;
                                } else {
                                    result[1] = 2;
                                }
                            }
                
                            return result;
                        
                        case 2:
                            case2.add(c1);
                            break;

                        default:
                            break;
                    }
                    undoMap(c1);
                    currentFlip.remove(c1);
                }
                for(Card cCase2 : case2){
                    if(winable(cCase2)){
                        result = toResult(guessUnknownCard(cCase2.getNumber()>7));
                        return result;
                    } 
                }
                result = toResult(guessUnknownCard((int)Math.random()*2 == 1 ? true : false));
                return result;
            case 1:
                for(Card c2 : getKnownAndFlipable()) {
                    updateMap(c2);
                    currentFlip.add(c2);
                    switch(flip2(c2, currentFlip.get(0))) {
                        case 3:
                        undoMap(c2);
                            result[0] = c2.getSource();
                            if(result[0] == -1) {
                                result[1] = all.get(-1).indexOf(c2);
                            } else {
                                if(all.get(result[0]).get(0) == c2) {
                                    result[1] = 1;
                                } else {
                                    result[1] = 2;
                                }
                            }
                
                            return result;

                        case 2:
                            result = toResult( guessUnknownCard(c2.getNumber()>7));
                            return result;
                    }
                }
                
                result = toResult(getKnownAndFlipable().get((int)(Math.random() * getKnownAndFlipable().size())));
                return result;

            case 2:
                for(Card c3 : getKnownAndFlipable()) {
                    if(flip3(c3, currentFlip.get(1))) {
                        result = toResult(c3);
                        return result;
                    }
                }
                result = toResult(guessUnknownCard(currentFlip.get(1).getNumber()>7));
                return result;
        }
        result[0] = -1;
        result[1] = (int)Math.random() * game.getTableCards().size();
        return result;
    }

    private int flip1(Card c1) {
        boolean cCase2 = false;
        for(Card c2 : getKnownAndFlipable()) {
            currentFlip.add(c2);
            updateMap(c2);
            switch (flip2(c2, c1)) {
                case 3:
                    undoMap(c2);
                    return 3;

                case 2 :
                    cCase2 = true;
                    break;

                default:
                    break;
            }
            undoMap(c2);
            currentFlip.remove(c2);
        }
        return (cCase2 ? 2 : 1);
    }

    private int flip2(Card c2, Card c1) {
        if(c2.getNumber() == c1.getNumber()) {
            
            for(Card c3 : getKnownAndFlipable()) {
                updateMap(c3);

                if(flip3(c3, c2)) {
                    undoMap(c3);
                    return 3;
                }
                undoMap(c3);
            }
            return 2;
        } else {
            return 1;
        }
    }

    private boolean flip3(Card c3, Card c2) {
        return (currentFlip.get(0).getNumber() == c3.getNumber());
    }

    private List<Card> getFlipable() {
        List<Card> list = new ArrayList<>();
        for(int i=0; i<4;i++) {
            if(!all.get(i).isEmpty()){
                list.add(all.get(i).get(0));
                if(all.get(i).get(0) != all.get(i).get(all.get(i).size()-1)) {
                    list.add(all.get(i).get(all.get(i).size()-1));
                }
            }
        }
        list.addAll(all.get(-1));
        return list;
    }

    private List<Card> getKnownAndFlipable() {
        ArrayList<Card> intersection = new ArrayList<>();
        for (Card c : getFlipable()) {
            if (known.contains(c)) {
                intersection.add(c);
            }
        }
        return intersection;
    }

    private void updateMap(Card c) {
        all.get(c.getSource()).remove(c);
    }

    private void undoMap(Card c) {
        if(c.getSource() != -1) {
            all.get(c.getSource()).add(c);
            sortCards(all.get(c.getSource()));
        } else {
            all.get(-1).add(c);
        }
    }

    public void sortCards(List<Card> list) {
        Collections.sort(list, Comparator
        .comparingInt(Card::getNumber)
        .thenComparingInt(Card::getId));
    }

        private boolean winable(Card c) {
        List<Integer> list = new ArrayList<>(getTrio());
        list.add(c.getNumber());
        if((list.contains(1) && list.contains(6))
        || (list.contains(1) && list.contains(8))
        || (list.contains(2) && list.contains(5))
        || (list.contains(2) && list.contains(9))
        || (list.contains(5) && list.contains(12))
        || (list.contains(3) && list.contains(4))
        || (list.contains(3) && list.contains(10))
        || (list.contains(4) && list.contains(11))
        || (list.contains(7))) {
            return true;
        }else{
            return false;
        }
    }

    private Card guessUnknownCard(boolean preferMax) {
        List<Card> candidateCards = new ArrayList<>();

        for (int i=0; i<4; i++) {
            List<Card> hand = all.get(i);
            if (hand == null || hand.isEmpty()) continue;

            Card selected = preferMax ? 
                Collections.max(hand, Comparator.comparingInt(Card::getNumber)) :
                Collections.min(hand, Comparator.comparingInt(Card::getNumber));

            if (!known.contains(selected)) {
                candidateCards.add(selected);
            }
        }

        if (!candidateCards.isEmpty()) {
            return candidateCards.get((int)(Math.random() * candidateCards.size()));
        }

        List<Card> tableUnknowns = all.get(-1).stream()
            .filter(c -> !known.contains(c))
            .collect(Collectors.toList());

        if (!tableUnknowns.isEmpty()) {
            return tableUnknowns.get((int)(Math.random() * tableUnknowns.size()));
        }

        return getFlipable().get((int)(Math.random() * getFlipable().size()));
    }

    private int[] toResult(Card c) {
        int[] result = {0,0};
        result[0] = c.getSource();
        if(result[0] == -1) result[1] = all.get(-1).indexOf(c);
        else result[1] = c.getNumber()>7 ? 2 : 1;
        return result;
    }

    public void setGameState(GameState game) {
        this.game = game;
    }
}
