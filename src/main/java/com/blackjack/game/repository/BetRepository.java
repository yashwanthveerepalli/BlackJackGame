package com.blackjack.game.repository;

import com.blackjack.game.model.Bet;
import com.blackjack.game.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByUser(User user); // Fetch all bets made by a specific user
}
