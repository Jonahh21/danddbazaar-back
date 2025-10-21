package com.dandbazaar.back.common.pagination;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class PaginationBuilder {

    private int limit = 10;

    @Autowired
    HttpServletRequest request;

    public <T> Paginate<T> paginate(List<T> data, Integer page) {

    Integer count = data.size();
    Integer allPages = count == 0 ? 0 : (int) Math.ceil((double) count / (double) limit);

        String url = request.getRequestURI();

        

        Optional<String> prevUrl = (page <= 1 || allPages == 0) ? Optional.empty() : Optional.of(url + "?page=" + (page - 1));
        Optional<String> nextUrl = (allPages == 0 || page >= allPages) ? Optional.empty() : Optional.of(url + "?page=" + (page + 1));

        // page is 1-based. start inclusive, end exclusive for subList
        int startI = (page - 1) * limit;
        int endI = Math.min(startI + limit, count);

        List<T> pageData;
        if (count == 0 || page < 1 || startI >= count) {
            // empty page
            pageData = List.of();
        } else {
            pageData = data.subList(startI, endI);
        }

        return new Paginate<T>(page, count, allPages, prevUrl, nextUrl, pageData);
    }
}
