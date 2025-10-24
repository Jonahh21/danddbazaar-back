package com.dandbazaar.back.Items.registry;

import java.sql.Date;

import com.dandbazaar.back.Items.Item;
import com.dandbazaar.back.games.Game;

import jakarta.persistence.CascadeType;
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
@Table(name = "purchases")
public class PurchaseRegistry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Game origin;
    
    @ManyToOne
    private Game destination;

    @ManyToOne
    private Item item;

    private Date purchasedat;

    public PurchaseRegistrySimple toSimple() {
        PurchaseRegistrySimple.PurchaseRegistrySimpleBuilder builder = PurchaseRegistrySimple.builder();

        builder
            .id(id)
            .origin(origin.toGameRequest())
            .destination(destination.toGameRequest())
            .purchasedat(purchasedat);

        return builder.build();
    }
}
