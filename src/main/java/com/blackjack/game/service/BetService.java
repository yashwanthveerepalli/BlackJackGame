package com.blackjack.game.service;

import com.blackjack.game.model.Bet;
import com.blackjack.game.model.User;
import com.blackjack.game.repository.BetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BetService {

    private final BetRepository betRepository;

    public BetService(BetRepository betRepository) {
        this.betRepository = betRepository;
    }

    public Bet save(Bet bet) {
        return betRepository.save(bet); // Save a bet to the database
    }

    public List<Bet> findByUser(User user) {
        return betRepository.findByUser(user); // Find all bets for a user
    }
}
