package com.dandbazaar.back.Items;

import java.util.List;
import java.util.Optional;

import com.dandbazaar.back.Items.registry.PurchaseRegistrySimple;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ItemDetailed {
    private Long id;
    private String name;
    private Double price;
    private Optional<String> image;
    private String description;
    private String stats;
    private Optional<String> curses;
    private String fromGame;
    private Boolean hidden;

    private List<PurchaseRegistrySimple> purchasehistory;

    private Double quantity;
}
