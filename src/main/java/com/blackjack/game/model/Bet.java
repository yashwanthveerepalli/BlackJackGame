package com.blackjack.game.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bets")
@Getter
@Setter
@NoArgsConstructor
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Link each bet to a user

    @Column(name = "bet_amount", nullable = false)
    private int betAmount; // Amount bet by the user

    @Column(name = "winnings", nullable = false)
    private int winnings; // Positive for a win, negative for a loss

    @Column(name = "game_result", nullable = false)
    private String gameResult; // E.g., "Player Wins!" or "Player Loses!"

    @Column(name = "created_at", nullable = false, updatable = false)
    private java.sql.Timestamp createdAt; // Bet creation timestamp
}
