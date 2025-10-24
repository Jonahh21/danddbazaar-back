package com.dandbazaar.back.games;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import org.springframework.web.servlet.function.EntityResponse;

import com.dandbazaar.back.Items.Item;
import com.dandbazaar.back.Items.ItemRepository;
import com.dandbazaar.back.auth.entities.User;
import com.dandbazaar.back.auth.repositories.UserRepository;
import com.dandbazaar.back.common.reporegister.MasterRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/games")
@Slf4j
public class GameController {

    
    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ItemRepository itemRepo;

    public User findUser(Authentication auth) throws NoSuchElementException {
        log.info("Obteniendo nombre del usuario...");
        log.info(auth.getName());
        User guestUser = new User();
        guestUser.setEmail("guest@none.es");
        guestUser.setId(0L);
        guestUser.setUsername("Guest");
        guestUser.setGames(new ArrayList<Game>());

        String username = auth.getName();
        User user = userRepo.findByUsername(username)
                .orElse(guestUser);

        return user;
    }

    @GetMapping()
    public List<GameRequest> byuser(Authentication auth) {

        log.info("Buscando al usuario: " + auth.getName());
        
        User user = findUser(auth);

        if (user.getId() == 0) {
            log.error("No se encontró el usuario");
        } else {
            log.info("Usuario encontrado: " + user.toString());
        }

        List<Game> games = user.getGames();

        log.info("La cantidad de juegos que el usuario tiene es: " + games.size());

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

    @DeleteMapping("/delete/{originId}/{destinationId}")
    public GameRequest deleteGameAndTransfer(Authentication auth, @PathVariable Long originId, @PathVariable Long destinationId) throws Exception {
        User user = userRepo.findByUsername(auth.getName())
            .orElseThrow();

        Game origin = gameRepo.findById(originId)
            .orElseThrow();
        Game destination = gameRepo.findById(destinationId)
            .orElseThrow();

        List<Item> items = origin.getItems();

        if (!user.getGames().contains(origin) || !user.getGames().contains(destination)) {
            throw new Exception("No tienes los juegos");
        }

        items = items.stream().map(item -> {
            item.setInGamePrice(item.toTargetCurrency(destination));
            item.setGame(destination);

            return item;
        }).toList();

        itemRepo.saveAllAndFlush(items);
        Game updatedRepo = gameRepo.findById(destinationId)
            .orElseThrow();

        gameRepo.delete(origin);


        return updatedRepo.toGameRequest();
    }
}
