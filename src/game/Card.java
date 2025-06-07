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
    public boolean equals(Object o) {
        // 1. 檢查是否為同一個記憶體物件
        if (this == o) return true;
        // 2. 檢查傳入的物件是否為 null 或不同類別
        if (o == null || getClass() != o.getClass()) return false;
        // 3. 將物件轉型為 Card
        Card card = (Card) o;
        // 4. 比較核心屬性：只要 num 和 id 都相同，我們就認為這兩張卡是相等的
        return number == card.number && id == card.id;
    }

    @Override
    public int hashCode() {
        // 根據用於 equals 比較的屬性來產生一個雜湊碼
        return Objects.hash(number, id);
    }
}

