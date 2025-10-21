package com.dandbazaar.back.Items;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dandbazaar.back.Items.exceptions.NotEnoughMoneyException;
import com.dandbazaar.back.Items.registry.PurchaseRegistry;
import com.dandbazaar.back.Items.registry.PurchaseRegistryRepository;
import com.dandbazaar.back.common.pagination.Paginate;
import com.dandbazaar.back.common.pagination.PaginationBuilder;
import com.dandbazaar.back.games.Game;
import com.dandbazaar.back.games.GameRepository;

@RestController
@RequestMapping("/api/games")
public class ItemController {

    @Autowired
    GameRepository gamerepo;

    @Autowired
    ItemRepository itemRepo;

    @Autowired
    PurchaseRegistryRepository prRepo;

    @Autowired
    PaginationBuilder pBuilder;
    
    @GetMapping("/{gameId}/inventory")
    public List<ItemSimple> inventory(@PathVariable Long gameId) {
        Game game = findGame(gameId);

        return game.getItems().stream()
            .map(item -> item.toItemSimple(game))
            .toList();
    }

    @GetMapping("/{gameId}/store")
    public Paginate<ItemSimple> all(@PathVariable Long gameId, @RequestParam(defaultValue = "1") Integer page) {
        Game game = findGame(gameId);

        List<ItemSimple> allItems = itemRepo.findAll().stream()
            .filter(item -> !item.getHidden())
            .map(item -> item.toItemSimple(game))
            .toList();
        return pBuilder.paginate(allItems, page);
    }

    @GetMapping("/{gameId}/store/{itemId}")
    public ItemDetailed one(@PathVariable Long gameId, @PathVariable Long itemId) {
        Game game = findGame(gameId);
        Item item = findItem(itemId);

        return item.toItemDetailed(game);
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
    public ItemDetailed buy(@PathVariable Long gameId, @PathVariable Long itemId) throws NotEnoughMoneyException, Exception {
        Game destination = findGame(gameId);
        Item purchase = findItem(itemId);
        Game origin = purchase.getGame();
        Double destinationPrice = purchase.toTargetCurrency(destination);
        Double originPrice = purchase.getInGamePrice();

        if(destination.equals(origin)) {
            throw new Exception("No puedes comprar tu propio Item");
        }
        // Si el destino no tiene dinero
        if (destination.getPartyCurrency() < destinationPrice) {
            throw new NotEnoughMoneyException();
        }

        // Si el destino tiene dinero
        destination.setPartyCurrency(destination.getPartyCurrency() - destinationPrice);
        origin.setPartyCurrency(origin.getPartyCurrency() + originPrice);
        purchase.setGame(destination);
        purchase.setInGamePrice(destinationPrice);


        // Guarda el dinero

        gamerepo.save(destination);
        gamerepo.save(origin);

        
        // Actualiza la propiedad del item
        itemRepo.save(purchase);

        // Crea un registro
        PurchaseRegistry newRegistry = new PurchaseRegistry();
        newRegistry.setOrigin(origin);
        newRegistry.setDestination(destination);
        newRegistry.setItem(purchase);
        newRegistry.setPurchasedat(new java.sql.Date(System.currentTimeMillis()));

        prRepo.saveAndFlush(newRegistry);
        
        return purchase.toItemDetailed(destination);
    }

    private Game findGame(Long gameId) {
        return findInRepo(gamerepo, gameId);
    }

    private Item findItem(Long itemId) {
        return findInRepo(itemRepo, itemId);
    }

    private <T, I> T findInRepo(JpaRepository<T, I> repo, I id) {
        return repo.findById(id).orElseThrow();
    }
}
