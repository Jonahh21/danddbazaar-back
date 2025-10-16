package com.dandbazaar.back.Items.lore;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.Format;

import com.dandbazaar.back.Items.Item;
import com.dandbazaar.back.games.Game;

import io.micrometer.common.lang.Nullable;
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
@Table(name = "lores")
public class Lore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Game originGame;
    @ManyToOne
    private Item loredItem;
    private String text;
    private Date createdAt;

    private String thenName;
    private String thenDescription;
    private String thenStats;
    @Nullable private String thenCurses;

    private Double priceChange;
    private Double thenPrice;



    public static Lore fromLorePost(LorePost post, Item item) {
        Lore newLore = new Lore();

        newLore.loredItem = item;
        newLore.originGame = item.getGame();
        newLore.text = post.getText();

        newLore.thenName = post.getName();
        newLore.thenDescription = post.getDescription();
        newLore.thenStats = post.getStats();
        newLore.thenCurses = post.getCurses().orElse(null);

        newLore.priceChange = post.getPricechange();

        return newLore;
    }

    public LoreRequest toSimple() {
        LoreRequest.LoreRequestBuilder builder = LoreRequest.builder();

        builder
            .id(id)
            .game(originGame.toGameRequest())
            .text(text)
            .item(loredItem.toItemSimple(originGame))
            .createdAt(createdAt)
            .thenName(thenName)
            .thenDescription(thenDescription)
            .priceChange(priceChange)
            .thenPriceChange(getThenPriceChange())
            .thenPrice(getThenPrice());

        return builder.build();
    }

    private String getThenPriceChange() {

        DecimalFormat df = new DecimalFormat("#.00");

        String prefix = "";

        if (priceChange < 0) {
            prefix = "-";
        } else {
            prefix = "+";
        }


        return prefix + df.format(priceChange) + " " + originGame.getCurrencysymbol();
    }

    private String getThenPrice() {
        DecimalFormat df = new DecimalFormat("#.00");

        String prefix = "";

        if (priceChange < 0) {
            prefix = "-";
        } else {
            prefix = "+";
        }

        return prefix + df.format(thenPrice) + " " + originGame.getCurrencysymbol();
    }
}
