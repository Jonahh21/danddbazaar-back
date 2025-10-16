package com.dandbazaar.back.Items;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dandbazaar.back.Items.exceptions.NotEnoughMoneyException;
import com.dandbazaar.back.games.Game;
import com.dandbazaar.back.games.GameRepository;

@RestController
@RequestMapping("/api/games")
public class ItemController {

    @Autowired
    GameRepository gamerepo;

    @Autowired
    ItemRepository itemRepo;
    
    @GetMapping("/{gameId}/inventory")
    public List<ItemSimple> inventory(@PathVariable Long gameId) {
        Game game = findGame(gameId);

        return game.getItems().stream()
            .map(item -> item.toItemSimple(game))
            .toList();
    }

    @GetMapping("/{gameId}/store")
    public List<ItemSimple> all(@PathVariable Long gameId) {
        Game game = findGame(gameId);

        return itemRepo.findAll().stream()
            .filter(item -> !item.getHidden())
            .map(item -> item.toItemSimple(game))
            .toList();
    }

    @PostMapping("/{gameId}/create")
    public ItemDetailed create(@PathVariable Long gameId, @RequestBody ItemPost post) {
        Game game = findGame(gameId);
        
        Item item = Item.fromItemPost(post);
        item.setGame(game);

        itemRepo.saveAndFlush(item);

        return item.toItemDetailed(game);
    }

    @PostMapping("/{gameId}/buy/{itemId}")
    public ItemDetailed buy(@PathVariable Long gameId, @PathVariable Long itemId) throws NotEnoughMoneyException {
        Game destination = gamerepo.findById(gameId).orElseThrow();
        Item purchase = itemRepo.findById(itemId).orElseThrow();
        Game origin = purchase.getGame();
        Double destinationPrice = purchase.toTargetCurrency(destination);
        Double originPrice = purchase.getInGamePrice();

        // Si el destino no tiene dinero
        if (destination.getPartyCurrency() < destinationPrice) {
            throw new NotEnoughMoneyException();
        }

        // Si el destino tiene dinero
        destination.setPartyCurrency(destination.getPartyCurrency() - destinationPrice);
        origin.setPartyCurrency(origin.getPartyCurrency() + originPrice);
        purchase.setGame(destination);


        // Guarda el dinero

        gamerepo.save(destination);
        gamerepo.save(origin);

        // Actualiza la propiedad del item
        purchase = itemRepo.saveAndFlush(purchase);

        return purchase.toItemDetailed(destination);
    }

    private Game findGame(Long gameId) {
        return gamerepo.findById(gameId).orElseThrow();
    }
}
