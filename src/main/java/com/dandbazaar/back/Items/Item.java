package com.dandbazaar.back.Items;

import java.util.Optional;

import com.dandbazaar.back.common.SwordPriceService;
import com.dandbazaar.back.games.Game;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double inGamePrice;
    @Nullable private String image;
    private String description;
    private String stats;
    @Nullable private String curses;
    private Double quantity;
    private Boolean hidden;

    @ManyToOne
    private Game game;
    
    public ItemSimple toItemSimple(Game otherGame) {
        ItemSimple.ItemSimpleBuilder builder = ItemSimple.builder();

        Double realPrice = this.inGamePrice * game.getSwordPatternRatio();

        builder
            .id(id)
            .name(name)
            .image(Optional.ofNullable(image))
            .price(SwordPriceService.getItemPrice(otherGame.getSwordPatternRatio(), realPrice));

        return builder.build();
    }

    public ItemDetailed toItemDetailed(Game otherGame) {
        ItemDetailed.ItemDetailedBuilder builder = ItemDetailed.builder();

        Double realPrice = this.inGamePrice;
        builder
            .id(id)
            .name(name)
            .image(Optional.ofNullable(image))
            .price(SwordPriceService.getItemPrice(otherGame.getSwordPatternRatio(), realPrice))
            .description(description)
            .stats(stats)
            .hidden(hidden)
            .fromGame(game.getName())
            .curses(Optional.ofNullable(curses))
            .quantity(quantity);

        return builder.build();
    }

    public static Item fromItemPost(ItemPost post) {
        Item newItem = new Item();
        newItem.name = post.getName();
        newItem.inGamePrice = post.getPrice();
        newItem.image = post.getImage().orElse(null);
        newItem.description = post.getDescription();
        newItem.stats = post.getStats();
        newItem.curses = post.getCurses().orElse(null);
        newItem.quantity = post.getQuantity();
        newItem.hidden = post.getHidden();

        return newItem;
    }
}
