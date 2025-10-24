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
import com.dandbazaar.back.Items.registry.PurchaseRegistry;
import com.dandbazaar.back.Items.registry.PurchaseRegistryRepository;
import com.dandbazaar.back.auth.entities.User;
import com.dandbazaar.back.auth.repositories.UserRepository;
import com.dandbazaar.back.common.reporegister.MasterRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
    private PurchaseRegistryRepository purchaseRepo;

    @Autowired
    EntityManager eman;

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
                .map(game -> {
                    return game.toGameRequest();
                })
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
    public GameRequest updateMoney(Authentication auth, @RequestBody CurrencyUpdatePost update, @PathVariable Long id)
            throws Unauthorized {
        User user = findUser(auth);
        Game game = gameRepo.findById(id).orElseThrow();

        if (user.getGames().contains(game)) {
            game.updateCurrency(update);
        }

        game = gameRepo.save(game);

        return game.toGameRequest();
    }

    @Transactional
    @DeleteMapping("/delete/{originId}/{destinationId}")
    public GameRequest deleteGameAndTransfer(Authentication auth,
            @PathVariable Long originId,
            @PathVariable Long destinationId) throws Exception {

        log.info("=== Iniciando deleteGameAndTransfer ===");

        User user = userRepo.findByUsername(auth.getName()).orElseThrow();
        log.info("Usuario encontrado: {}", user.getUsername());

        Game origin = gameRepo.findById(originId).orElseThrow();
        Game destination = gameRepo.findById(destinationId).orElseThrow();
        log.info("Juego origen encontrado: {}", origin.getName());
        log.info("Juego destino encontrado: {}", destination.getName());

        // 1️⃣ Transferir Items
        List<Item> items = new ArrayList<>(origin.getItems());
        log.info("Items en origin antes de transferir: {}", items.size());

        for (Item item : items) {
            item.setInGamePrice(item.toTargetCurrency(destination));
            item.setGame(destination);

            // mover de las listas de Hibernate
            origin.getItems().remove(item);
            destination.getItems().add(item);

            log.info("Item {} transferido al destino", item.getName());
        }

        itemRepo.saveAllAndFlush(items);
        log.info("Todos los items transferidos y flush ejecutado");

        // 2️⃣ Transferir / eliminar PurchaseRegistry del origin
        List<PurchaseRegistry> originPurchases = new ArrayList<>(origin.getBeingOrigin());
        for (PurchaseRegistry pr : originPurchases) {
            // Si querés transferirlos al destination:
            pr.setOrigin(destination);
            destination.getBeingOrigin().add(pr);
            origin.getBeingOrigin().remove(pr);
            log.info("PurchaseRegistry {} transferido (origin)", pr.getId());
        }

        List<PurchaseRegistry> destinationPurchases = new ArrayList<>(origin.getBeingDestination());
        for (PurchaseRegistry pr : destinationPurchases) {
            pr.setDestination(destination);
            destination.getBeingDestination().add(pr);
            origin.getBeingDestination().remove(pr);
            log.info("PurchaseRegistry {} transferido (destination)", pr.getId());
        }

        purchaseRepo.flush();
        log.info("Todos los PurchaseRegistry transferidos y flush ejecutado");

        // 3️⃣ Borrar Game origen
        gameRepo.delete(origin);
        gameRepo.flush();
        log.info("Juego origen '{}' eliminado", origin.getName());

        // 4️⃣ Devolver el game destino actualizado
        Game updatedDestination = gameRepo.findById(destinationId).orElseThrow();
        log.info("=== deleteGameAndTransfer completado ===");
        return updatedDestination.toGameRequest();
    }

}
