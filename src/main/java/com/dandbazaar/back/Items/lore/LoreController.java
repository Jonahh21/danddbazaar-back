package com.dandbazaar.back.Items.lore;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dandbazaar.back.Items.Item;
import com.dandbazaar.back.Items.ItemRepository;
import com.dandbazaar.back.auth.entities.User;
import com.dandbazaar.back.auth.repositories.UserRepository;

@RestController
@RequestMapping("/api/lore")
public class LoreController {

    public User findUser(Authentication auth) throws NoSuchElementException {
        String username = auth.getName();
        User user = userRepo.findByUsername(username)
                .orElseThrow();

        return user;
    }

    @Autowired
    private ItemRepository itemRepo;

    @Autowired
    private LoreRepository loreRepo;

    @Autowired
    private UserRepository userRepo;
    
    @GetMapping
    public List<LoreRequest> all(){
        List<Lore> allLore = loreRepo.findAll();

        // sort by newest
        allLore.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return allLore.stream().map(lore -> lore.toSimple()).toList();
    }

    @GetMapping("/{itemId}")
    public List<LoreRequest> getFromItem(@PathVariable Long itemId, Authentication auth){
        User user = findUser(auth);
        Item item = findItem(itemId);

        return item.getLore().stream().map(l -> l.toSimple()).toList();
    }

    @PostMapping("/{itemId}")
    public LoreRequest createForItem(@PathVariable Long itemId, Authentication auth, @RequestBody LorePost post) throws Exception{
        User user = findUser(auth);
        Item item = findItem(itemId);

        if(!user.getGames().contains(item.getGame())){
            throw new Exception("Este no es vuestro item");
        }

        Lore lore = Lore.fromLorePost(post, item);

        item.setName(post.getName());
        item.setStats(post.getStats());
        item.setCurses(post.getCurses().orElse(null));
        item.setDescription(post.getDescription());
        item.setInGamePrice(item.getInGamePrice() + post.getPricechange());
        item.setStats(post.getStats());
        item.setImage(post.getImage());

        lore = loreRepo.saveAndFlush(lore);

        return lore.toSimple();
    }

    private Item findItem(Long itemId) {
        return findInRepo(itemRepo, itemId);
    }

    private Lore findLore(Long loreId) {
        return findInRepo(loreRepo, loreId);
    }

    private <T, I> T findInRepo(JpaRepository<T, I> repo, I id) {
        return repo.findById(id).orElseThrow();
    }
}
