package com.example.restaurantestimate.services;

import com.example.restaurantestimate.dto.restaurant.RestaurantDto;
import com.example.restaurantestimate.dto.restaurant.RestaurantPages;
import com.example.restaurantestimate.mappers.ExternalAPISerializer;
import com.example.restaurantestimate.pojo_tripadvisor.DocsItemRestaurantInfo;
import com.example.restaurantestimate.pojo_tripadvisor.RestaurantInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalApiService {
    @Value("h3lrZ6nKjg2Bo-g5KFHBsaRyc-BFZQz8wwIdOyDweNUo6rZv6rm7bTcu0vn9t4rE7w3zgTUJ9WbiyYBin3BQR04l6MpSxxY42B7dYbNjaCRCPFonXcS2EMyOHalPZXYx")
    private String apiKey;

    private final ExternalAPISerializer externalAPISerializer;
    public RestaurantPages findRestaurantByName(String name, Integer page, Integer limit) {
        URI url = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("api.yelp.com")
                .path("/v3/businesses/search")
                .queryParam("term", name)
                .queryParam("limit", limit)
                .queryParam("offset", page * limit)
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<RestaurantInfo> responseEntity =
                restTemplate.exchange(url, HttpMethod.GET, entity, RestaurantInfo.class);

        List<DocsItemRestaurantInfo> foundRestaurant = responseEntity.getBody().getDocs();
        log.debug("ИЩУ НА ТРИПАДВАЙЗЕРЕ В ТРИПАДВАЙЗЕР СЕРВИС");
        if (responseEntity.getBody().getDocs().isEmpty()) {
            throw new EntityNotFoundException("Рестораны для данного названия на TripAdvisor не найдены!");
        }

        List<RestaurantDto> restaurants = foundRestaurant.stream()
                .map(externalAPISerializer).toList();

        RestaurantPages restaurantPages = new RestaurantPages(restaurants);
        restaurantPages.setPage(responseEntity.getBody().getPage());
        restaurantPages.setPages(responseEntity.getBody().getPages());
        restaurantPages.setTotal((long) responseEntity.getBody().getTotal());
        restaurantPages.setLimit(responseEntity.getBody().getLimit());

        return restaurantPages;
    }

    public RestaurantPages findRestaurantById(Long id) {
        String url = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("api.tripadvisor.com")
                .path("v2/restaurant")
                .queryParam("selectFields", "id", "name", "rating",
                        "image_url", "cuisines", "description")
                .queryParam("id", id.intValue())
                .build()
                .toUriString();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<RestaurantInfo> responseEntity =
                restTemplate.exchange(url, HttpMethod.GET, entity, RestaurantInfo.class);

        List<DocsItemRestaurantInfo> foundRestaurant = responseEntity.getBody().getDocs();
        if (responseEntity.getBody().getDocs().isEmpty()) {
            throw new EntityNotFoundException("Рестораны с данным ID на TripAdvisor не найден!");
        }

        List<RestaurantDto> restaurants = foundRestaurant.stream()
                .map(externalAPISerializer).toList();

        RestaurantPages restaurantPages = new RestaurantPages(restaurants);
        restaurantPages.setPage(responseEntity.getBody().getPage());
        restaurantPages.setPages(responseEntity.getBody().getPages());
        restaurantPages.setTotal((long) responseEntity.getBody().getTotal());
        restaurantPages.setLimit(responseEntity.getBody().getLimit());

        return restaurantPages;
    }
}