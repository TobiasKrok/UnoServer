package com.tobias.game.card;

public class Card {
    private CardType cardType;
    private CardColor cardColor;
    private int value;

    public Card(CardColor cardColor, CardType cardType) {
        this.cardColor = cardColor;
        this.cardType = cardType;
        // Return zero instead of null if card is a special card. Special cards does not have a value.
        this.value = 0;
    }
    // If card is not a special card, CardType will be set to NORMAL as default.
    public Card(CardColor cardColor, int value) {
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
        return "cardColor: " + cardColor + " value: " + value + " cardType: " + cardType;
    }
}
