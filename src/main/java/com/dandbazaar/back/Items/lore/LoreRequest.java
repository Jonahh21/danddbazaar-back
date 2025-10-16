package com.dandbazaar.back.Items.lore;

import java.sql.Date;

import com.dandbazaar.back.Items.ItemSimple;
import com.dandbazaar.back.games.GameRequest;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoreRequest {
    private Long id;
    private GameRequest game;
    private String text;
    private ItemSimple item;
    private Date createdAt;

    private String thenName;
    private String thenDescription;

    private Double priceChange;

    private String thenPriceChange;
    private String thenPrice;


    private String thenStats;
    private String thenCurses;
}
