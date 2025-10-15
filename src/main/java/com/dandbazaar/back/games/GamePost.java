package com.dandbazaar.back.games;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class GamePost {
    private String name;
    private String currencysymbol;
    private String currencynamesingle;
    private String currencynamemultiple;
    private Double swordpriceincurrency;
    private Optional<String> image;

    private Double partycurrency;
}
