package com.blackjack.game.model;

import java.util.ArrayList;
import java.util.List;

public class BlackJackGame {
    private Deck deck;
    private List<Card> playerHand;
    private List<Card> dealerHand;

    public BlackJackGame() {
        deck = new Deck();
        deck.shuffle();
        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();
    }

    public void startGame() {
        playerHand.add(deck.drawCard());
        playerHand.add(deck.drawCard());
        dealerHand.add(deck.drawCard());
        dealerHand.add(deck.drawCard());
    }

    public void playerHits() {
        playerHand.add(deck.drawCard());
    }

    public void dealerHits() {
        while (calculateHandValue(dealerHand) < 17) {
            dealerHand.add(deck.drawCard());
        }
    }

    public int calculateHandValue(List<Card> hand) {
        int value = 0;
        int aceCount = 0;

        for (Card card : hand) {
            value += card.getNumericValue();
            if (card.getValue().equals("A")) {
                aceCount++;
            }
        }

        while (value > 21 && aceCount > 0) {
            value -= 10; // Counting Ace as 1 instead of 11
            aceCount--;
        }

        return value;
    }

    public List<Card> getPlayerHand() {
        return playerHand;
    }

    public List<Card> getDealerHand() {
        return dealerHand;
    }

    public String determineWinner() {
        int playerValue = calculateHandValue(playerHand);
        int dealerValue = calculateHandValue(dealerHand);

        if (playerValue > 21) return "Player Busts! Dealer Wins!";
        if (dealerValue > 21) return "Dealer Busts! Player Wins!";
        if (playerValue > dealerValue) return "Player Wins!";
        if (playerValue < dealerValue) return "Dealer Wins!";
        return "It's a Tie!";
    }
}