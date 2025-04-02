package com.blackjack.game.model;

public class Card {
    private String suit;
    private String value;

    public Card(String suit, String value) {
        this.suit = suit;
        this.value = value;
    }

    public String getSuit() {
        return suit;
    }

    public void setSuit(String suit) {
        this.suit = suit;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getNumericValue() {
        return switch (value) {
            case "2", "3", "4", "5", "6", "7", "8", "9", "10" -> Integer.parseInt(value);
            case "J", "Q", "K" -> 10;
            case "A" -> 11; // Initially, Ace is counted as 11
            default -> 0;
        };
    }

    @Override
    public String toString() {
        return value + " of " + suit;
    }
}