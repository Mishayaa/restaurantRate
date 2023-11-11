package com.example.restaurantestimate.pojo_tripadvisor;

import java.util.List;

public class RestaurantInfo {


    private int total;
    private int pages;
    private List<DocsItemRestaurantInfo> docs;
    private int limit;
    private int page;

    public int getTotal() {
        return total;
    }

    public int getPages() {
        return pages;
    }

    public List<DocsItemRestaurantInfo> getDocs() {
        return docs;
    }

    public int getLimit() {
        return limit;
    }

    public int getPage() {
        return page;
    }
}
