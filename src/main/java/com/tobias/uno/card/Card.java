package com.tobias.uno.card;

public class Card {
    private CardType cardType;
    private CardColor cardColor;
    private int value;

    Card(CardColor cardColor, CardType cardType) {
        this.cardColor = cardColor;
        this.cardType = cardType;
        // Set value to the value of the CardType enum.
        this.value = cardType.getValue();
    }
    // If card is not a special card, CardType will be set to NORMAL as default.
    Card(CardColor cardColor, int value) {
        this.cardColor = cardColor;
        this.cardType = CardType.NORMAL;
        this.value = value;
    }

    public CardColor getCardColor() {
        return cardColor;
    }


    public int getValue() {
        return value;
    }
    public CardType getCardType(){
        return cardType;
    }


    @Override
    public String toString() {
        return "["+  cardType + "," + cardColor + "," + value + "]";
    }
}
