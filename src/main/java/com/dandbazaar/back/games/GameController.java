package com.dandbazaar.back.games;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

import com.dandbazaar.back.auth.entities.User;
import com.dandbazaar.back.auth.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.websocket.server.PathParam;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameRepository gameRepo;
    private final UserRepository userRepo;

    public User findUser(Authentication auth) throws NoSuchElementException {
        String username = auth.getName();
        User user = userRepo.findByUsername(username)
                .orElseThrow();

        return user;
    }

    public GameController(GameRepository gameRepo, UserRepository userRepo) {
        this.gameRepo = gameRepo;
        this.userRepo = userRepo;
    }

    @GetMapping()
    public List<GameRequest> byuser(Authentication auth) {
        
        User user = findUser(auth);

        List<Game> games = gameRepo.findByOwnerUser(user)
                .orElse(new ArrayList<Game>());

        return games.stream()
                .map(game -> { return game.toGameRequest(); })
                .toList();
    }

    @GetMapping("/{id}")
    public GameRequest one(@PathVariable Long id, Authentication auth) throws EntityNotFoundException {
        User user = findUser(auth);

        Game game = gameRepo.findById(id).orElseThrow();

        if (user.getGames().contains(game)) {
            return game.toGameRequest();
        }

        throw new EntityNotFoundException("No puedes ver este juego");
    }

    @PostMapping()
    public GameRequest create(Authentication auth, @RequestBody GamePost post) {
        Game game = Game.fromGamePost(post);
        User creator = findUser(auth);

        game.setOwnerUser(creator);

        Game savedGame = gameRepo.saveAndFlush(game);

        return savedGame.toGameRequest();        
    }

    @PutMapping("/{id}")
    public GameRequest updateMoney(Authentication auth, @RequestBody CurrencyUpdatePost update, @PathVariable Long id) throws Unauthorized{
        User user = findUser(auth);
        Game game = gameRepo.findById(id).orElseThrow();

        if (user.getGames().contains(game)) {
            game.updateCurrency(update);
        }
        
        game = gameRepo.save(game);

        return game.toGameRequest();
    }
}
