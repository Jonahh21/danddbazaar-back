package com.dandbazaar.back.Items;

import java.util.Optional;

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

    private Double quantity;
}
