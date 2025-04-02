package com.blackjack.game.controller;

import com.blackjack.game.model.Bet;
import com.blackjack.game.model.BlackJackGame;
import com.blackjack.game.model.User;
import com.blackjack.game.service.BetService;
import com.blackjack.game.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {
    private final UserService userService;
    private final BetService betService;
    private final Map<String, BlackJackGame> games = new HashMap<>();

    public GameController(UserService userService, BetService betService) {
        this.userService = userService;
        this.betService = betService;
    }

    /**
     * Redirection to game start endpoint.
     */
    @GetMapping("/")
    public String redirectToStartGame() {
        return "redirect:/api/game/start";
    }

    /**
     * Start a new Blackjack game while deducting the user's bet amount.
     */
    @PostMapping("/start")
    public ResponseEntity<BlackJackGame> startGame(@RequestBody Map<String, Object> payload) {
        System.out.println("Received payload: " + payload);

        // Extract email and betAmount from request payload
        String email = (String) payload.get("email");
        Integer betAmount = (Integer) payload.get("betAmount");

        if (email == null || betAmount == null) {
            System.out.println("Error: Missing email or betAmount.");
            return ResponseEntity.badRequest().body(null); // Invalid payload
        }

        System.out.println("Email: " + email + ", Bet Amount: " + betAmount);

        // Validate the user
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {
            System.out.println("Error: User not found for email: " + email);
            return ResponseEntity.badRequest().body(null); // User not found
        }

        User user = optionalUser.get();
        System.out.println("User found: " + user);

        // Validate the bet amount
        if (betAmount <= 0 || betAmount > 30) {
            System.out.println("Error: Invalid bet amount: " + betAmount);
            return ResponseEntity.badRequest().body(null); // Invalid bet amount
        }

        // Validate sufficient balance
        if (user.getBalance() < betAmount) {
            System.out.println("Error: Insufficient balance. Current balance: " + user.getBalance());
            return ResponseEntity.badRequest().body(null); // Insufficient balance
        }

        // Deduct the bet amount from the user's balance
        System.out.println("Starting game with bet amount: " + betAmount);
        user.setBalance(user.getBalance() - betAmount);
        userService.save(user);

        // Create a new game instance for the user and store it in the map
        BlackJackGame game = new BlackJackGame();
        game.startGame();
        games.put(email, game); // Store the game instance for the specific user

        return ResponseEntity.ok(game);
    }

    /**
     * Process the "hit" action for the player.
     */
    @PostMapping("/hit")
    public ResponseEntity<BlackJackGame> playerHits(@RequestBody Map<String, String> payload) {
        // Extract email from payload
        String email = payload.get("email");

        // Check if game exists for this user
        if (!games.containsKey(email)) {
            return ResponseEntity.badRequest().body(null); // No game exists for this user
        }

        // Retrieve user's game instance and perform "hit" action
        BlackJackGame game = games.get(email);
        game.playerHits();

        return ResponseEntity.ok(game);
    }

    /**
     * Process the "stand" action for the dealer.
     */
    @PostMapping("/stand")
    public ResponseEntity<BlackJackGame> dealerHits(@RequestBody Map<String, String> payload) {
        // Extract email from payload
        String email = payload.get("email");

        // Check if game exists for this user
        if (!games.containsKey(email)) {
            return ResponseEntity.badRequest().body(null); // No game exists for this user
        }

        // Retrieve user's game instance and perform dealer's turn
        BlackJackGame game = games.get(email);
        game.dealerHits();

        return ResponseEntity.ok(game);
    }

    /**
     * Determine the winner, calculate winnings, and update balance.
     */
    /**
     * Determine the winner, calculate winnings, and update balance.
     */
    @GetMapping("/winner")
    public ResponseEntity<String> determineWinner(@RequestParam String email, @RequestParam int betAmount) {
        // Validate the user
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found.");
        }

        if (!games.containsKey(email)) {
            return ResponseEntity.badRequest().body("No active game found for this user.");
        }

        BlackJackGame game = games.get(email); // Get current game instance
        User user = optionalUser.get();
        String result = game.determineWinner();
        int winnings = 0;

        // Calculate winnings or losses based on game result
        if (result.contains("Player Wins!")) {
            if (game.calculateHandValue(game.getPlayerHand()) == 21) {
                // Blackjack: betAmount + 1.5x betAmount
                winnings = (int) (betAmount + (betAmount * 1.5));
            } else {
                // Regular win: betAmount x 2
                winnings = betAmount * 2;
            }

            // Add winnings (includes the original bet amount back to balance)
            user.setBalance(user.getBalance() + winnings);
        } else if (result.contains("Dealer Wins!") || result.contains("It's a Tie!")) {
            // Loss: The bet amount has already been deducted during the start of the game
            winnings = 0; // Nothing is added back for a loss
        } else if (result.contains("Tie")) {
            // In case of a Tie: Return the bet amount back to the user (no gain, no loss)
            user.setBalance(user.getBalance() + betAmount);
        }

        // Log the bet to the "bets" table
        Bet bet = new Bet();
        bet.setUser(user);
        bet.setBetAmount(betAmount);
        bet.setWinnings(winnings - betAmount); // Net winnings (positive or negative)
        bet.setGameResult(result);
        bet.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        betService.save(bet);

        // Save the updated user balance in the database
        userService.save(user);

        // Remove the game from the map (game is over)
        games.remove(email);

        String responseMessage = (result.contains("Player Wins!") || result.contains("Tie"))
                ? result + ". Your updated balance is $" + user.getBalance()
                : result + ". Sorry, you lost. Your updated balance is $" + user.getBalance();

        return ResponseEntity.ok(responseMessage);
    }

    /**
     * Retrieve the current balance of the user.
     */
    @GetMapping("/balance")
    public ResponseEntity<String> getBalance(@RequestParam String email) {
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found.");
        }

        User user = optionalUser.get();
        return ResponseEntity.ok("Your balance is: $" + user.getBalance());
    }

    /**
     * Retrieve betting history for the user.
     */
    @GetMapping("/history")
    public ResponseEntity<List<Bet>> getBetHistory(@RequestParam String email) {
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User user = optionalUser.get();
        List<Bet> bets = betService.findByUser(user);

        return ResponseEntity.ok(bets);
    }
}