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
}
