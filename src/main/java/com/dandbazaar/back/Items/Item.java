package com.dandbazaar.back.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.dandbazaar.back.common.SwordPriceService;
import com.dandbazaar.back.games.Game;
import com.dandbazaar.back.Items.lore.Lore;
import com.dandbazaar.back.Items.lore.LoreRequest;
import com.dandbazaar.back.Items.registry.PurchaseRegistry;
import com.dandbazaar.back.Items.registry.PurchaseRegistrySimple;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double inGamePrice;
    @Nullable
    private String image;
    private String description;
    private String stats;
    @Nullable
    private String curses;
    private Double quantity;
    private Boolean hidden;

    @ManyToOne
    private Game game;

    @OneToMany(mappedBy = "item")
    private List<PurchaseRegistry> purchaseHistorial;
    private List<Lore> lore;

    @OneToMany(mappedBy = "loreditem")

    /**
     * Convierte el precio de este ítem desde la economía de su juego origen a la
     * economía de
     * {@code otherGame} utilizando los "ratios" basados en el patrón (espada).
     *
     * ratio = PRECIO_ESPADA_REAL / precioInGame
     *
     * Para mantener la equivalencia del valor real, la fórmula correcta es:
     * precioDestino = precioOrigen * (ratioOrigen / ratioDestino)
     *
     * Ejemplo:
     * Juego A: espada = 20 -> ratioA = 20 / 20 = 1.0
     * Juego B: espada = 299.99 -> ratioB = 20 / 299.99 ≈ 0.0667
     * Si un objeto vale 119.95 en Juego A, su valor en Juego B será:
     * 119.95 * (1.0 / 0.0667) ≈ 1798.95
     *
     * Esta implementación aplica exactamente esa fórmula.
     */
    public Double toTargetCurrency(Game otherGame) {
        Double originRatio = this.game.getSwordPatternRatio();
        Double targetRatio = otherGame.getSwordPatternRatio();

        if (originRatio == null || targetRatio == null || originRatio == 0 || targetRatio == 0) {
            // Si falta ratio o es 0, devolvemos null para indicar que la conversión no es
            // posible
            return null;
        }

        return inGamePrice * (originRatio / targetRatio);
    }

    public ItemSimple toItemSimple(Game otherGame) {
        ItemSimple.ItemSimpleBuilder builder = ItemSimple.builder();

        builder
                .id(id)
                .name(name)
                .image(Optional.ofNullable(image))
                .price(toTargetCurrency(otherGame));

        return builder.build();
    }

    public ItemDetailed toItemDetailed(Game otherGame) {
        ItemDetailed.ItemDetailedBuilder builder = ItemDetailed.builder();

        List<PurchaseRegistrySimple> historial = purchaseHistorial.stream()
            .map(reg -> reg.toSimple())
            .toList();

        List<LoreRequest> loreHistory = lore.stream().map(l -> l.toSimple()).toList();

        builder
            .id(id)
            .name(name)
            .image(Optional.ofNullable(image))
            .price(toTargetCurrency(otherGame))
            .description(description)
            .stats(stats)
            .hidden(hidden)
            .fromGame(game.getName())
            .curses(Optional.ofNullable(curses))
            .quantity(quantity)
            .purchasehistory(historial)
            .lore(loreHistory);

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
        newItem.purchaseHistorial = new ArrayList<PurchaseRegistry>();
        newItem.lore = new ArrayList<Lore>();

        return newItem;
    }
}
