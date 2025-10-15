package com.dandbazaar.back.games;

import java.util.Optional;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GameRequest {
    private Long id;
    private String name;
    private String currencysymbol;
    private String currencynamesingle;
    private String currencynamemultiple;
    private Optional<String> image;
    
    private Double partycurrency;
}
