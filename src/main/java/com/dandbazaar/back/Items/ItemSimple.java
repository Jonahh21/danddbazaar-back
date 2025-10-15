package com.dandbazaar.back.Items;

import java.util.Optional;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ItemSimple {
    private Long id;
    private String name;
    private Optional<String> image;
    private Double price;
}
