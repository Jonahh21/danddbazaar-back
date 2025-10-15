package com.dandbazaar.back.games;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CurrencyUpdatePost {
    private Optional<Double> partycurrency;
    private Optional<Double> swordpriceincurrency;
}
