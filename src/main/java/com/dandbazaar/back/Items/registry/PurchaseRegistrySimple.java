package com.dandbazaar.back.Items.registry;

import java.sql.Date;

import com.dandbazaar.back.Items.ItemDetailed;
import com.dandbazaar.back.games.GameRequest;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PurchaseRegistrySimple {
    private Long id;
    private GameRequest origin;
    private GameRequest destination;
    
    private Date purchasedat;
}
