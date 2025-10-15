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

    private Game findGame(Long gameId) {
        return gamerepo.findById(gameId).orElseThrow();
    }
}
