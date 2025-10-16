package com.dandbazaar.back.Items.lore;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LorePost {
    private String text;

    private String name;
    private String description;
    private Double pricechange;

    private String stats;
    private Optional<String> curses;
}
