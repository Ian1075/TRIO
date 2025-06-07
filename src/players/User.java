package players;

import game.GameState;
import game.Player;
import java.util.Scanner;

public class User extends Player {

    public User(String name) {
        super(name);
    }
    
    @Override
    public int[] chooseCardToFlip() {
        int type;
        int target;
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("Flip from: -1.Table  0~3. Player");
            type = sc.nextInt();
            if (type == -1) {
                System.out.print("Which table card (0-7)?");
                target = sc.nextInt();
            } else {
                System.out.print("1. Min 2. Max");
                target = sc.nextInt();
            }
        }
        return new int[] {type, target};
    }

    @Override
    protected void setGameState(GameState game) {
    }
}
