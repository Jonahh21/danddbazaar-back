package com.dandbazaar.back.common.pagination;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter 
@AllArgsConstructor
public class Paginate<T> {
    private Integer page;
    private Integer count;
    private Integer allPages;

    private Optional<String> prevUrl;
    private Optional<String> nextUrl;

    private List<T> data;
}