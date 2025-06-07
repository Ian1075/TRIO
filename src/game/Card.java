package game;

import java.util.Objects;

public class Card {
    private final int number;   // 1~12
    private final int id;       // id 0~35
    private int source;   //-1 Table/ 0~3 Player

    public Card(int number, int id) {
        this.number = number;
        this.id = id;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getSource() {
        return source;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Card card = (Card) obj;
        return number == card.number && id == card.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, id);
    }
}

