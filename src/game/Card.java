package game;

public class Card {
    private final int number;   // 1~12
    private final int id;       // id 0~35

    public Card(int number, int id) {
        this.number = number;
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Card{" + "num=" + number + ", id=" + id + '}';
    }
}

