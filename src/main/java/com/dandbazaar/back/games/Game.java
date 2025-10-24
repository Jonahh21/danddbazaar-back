package com.dandbazaar.back.games;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.dandbazaar.back.Items.Item;
import com.dandbazaar.back.Items.lore.Lore;
import com.dandbazaar.back.Items.registry.PurchaseRegistry;
import com.dandbazaar.back.auth.entities.User;
import com.dandbazaar.back.common.SwordPriceService;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a game entity with currency and ownership details.
 * <p>
 * This class is mapped to the "games" table in the database and contains information
 * about the game's name, currency, image, party currency, sword pattern ratio, and owner.
 * </p>
 *
 * <p>
 * Provides utility methods to convert to and from request/DTO objects, as well as to update
 * currency-related fields.
 * </p>
 *
 * @author jonahh21
 */
@Entity
@Getter @Setter @NoArgsConstructor @ToString @AllArgsConstructor
@Table(name = "games")

public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String currencysymbol;
    private String currencyNameSingle;
    private String currencyNameMultiple;

    private String image;

    private Double partyCurrency;
    private Double swordPatternRatio;

    @ManyToOne
    @ToString.Exclude
    private User ownerUser;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Item> items;

    @OneToMany(mappedBy = "origin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseRegistry> beingOrigin;

    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseRegistry> beingDestination;

    @OneToMany(mappedBy = "originGame", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lore> lores;

    public GameRequest toGameRequest() {
        GameRequest.GameRequestBuilder builder = GameRequest.builder();

        builder
            .id(id)
            .name(name)
            .currencysymbol(currencysymbol)
            .currencynamesingle(currencyNameSingle)
            .currencynamemultiple(currencyNameMultiple)
            .image(Optional.ofNullable(image))
            .partycurrency(partyCurrency);

        return builder.build();

    }

    public static Game fromGamePost(GamePost post) {
        Game newGame = new Game();

        newGame.name = post.getName();
        newGame.currencysymbol = post.getCurrencysymbol();
        newGame.currencyNameSingle = post.getCurrencynamesingle();
        newGame.currencyNameMultiple = post.getCurrencynamemultiple();
        newGame.image = post.getImage().orElse(null);
        newGame.partyCurrency = post.getPartycurrency();

        newGame.items = new ArrayList<Item>();
        newGame.beingOrigin = new ArrayList<PurchaseRegistry>();
        newGame.beingDestination = new ArrayList<PurchaseRegistry>();

        newGame.swordPatternRatio = SwordPriceService.getRatio(post.getSwordpriceincurrency());

        return newGame;
    }

    public Game updateCurrency(CurrencyUpdatePost post) {
        if (post.getPartycurrency().isPresent()) {
            this.partyCurrency = post.getPartycurrency().get();
        }
        if (post.getSwordpriceincurrency().isPresent()) {
            this.swordPatternRatio = SwordPriceService.getRatio(post.getSwordpriceincurrency().get());
        }

        return this;
    }
}
