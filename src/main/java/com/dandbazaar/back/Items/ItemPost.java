package com.dandbazaar.back.Items;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class ItemPost {
    private String name;
    private Double price;
    private Optional<String> image;
    private String description;
    private String stats;
    private Optional<String> curses;
    private Double quantity;
    private Boolean hidden;
}
