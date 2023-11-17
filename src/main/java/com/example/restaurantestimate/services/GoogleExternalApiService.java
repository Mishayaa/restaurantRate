package com.example.restaurantestimate.services;

import com.example.restaurantestimate.dto.google.Candidate;
import com.example.restaurantestimate.dto.google.GoogleRestaurantInfo;
import com.example.restaurantestimate.dto.restaurant.RestaurantDto;
import com.example.restaurantestimate.dto.restaurant.RestaurantPages;
import com.example.restaurantestimate.mappers.GooglePlacesApiSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;


import org.springframework.http.ResponseEntity;


@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleExternalApiService {
    @Value("AIzaSyDzGpFuJK0dhf72YFj-wrhNnpXkIZ5chLM")
    private String apiKey;
    private final GooglePlacesApiSerializer googlePlacesApiSerializer;

    public RestaurantPages findRestaurantByName(String name, Integer page, Integer limit) throws Exception {
        URI url = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("maps.googleapis.com")
                .path("/maps/api/place/findplacefromtext/json")
                .queryParam("input", name + "restaurant")
                .queryParam("inputtype", "textquery")
                .queryParam("fields", "photos,photo,formatted_address,name,rating,opening_hours,geometry" +
                        ",business_status")
                .queryParam("key", apiKey)
                // .queryParam("type", "italian_restaurant")
                .build()
                .toUri();


        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<GoogleRestaurantInfo> responseEntity =
                restTemplate.getForEntity(url, GoogleRestaurantInfo.class);

        List<Candidate> foundRestaurants = responseEntity.getBody().getCandidates();
        if (foundRestaurants == null || foundRestaurants.isEmpty()) {
            throw new EntityNotFoundException("Рестораны для данного названия на Google Places не найдены!");
        }
        for (Candidate foundRestaurant : foundRestaurants) {
            foundRestaurant.setPlaceId(getRestaurantFromGooglePlaces(getPlaceIdFromGooglePlaces(name)).getPlaceId());
            foundRestaurant.setPhotoUrl(getRestaurantFromGooglePlaces(foundRestaurant.getPlaceId()).getPhotoUrl());
            System.out.println(foundRestaurant.getName() + " - " + foundRestaurant.getFormatted_address());
        }

        List<RestaurantDto> restaurants = foundRestaurants.stream()
                .map(googlePlacesApiSerializer).toList();
        RestaurantPages restaurantPages = new RestaurantPages();
        restaurantPages.setRestaurants(restaurants);
        restaurantPages.setTotal((long) foundRestaurants.size());


        return restaurantPages;

    }


    public Candidate getRestaurantFromGooglePlaces(String placeId) throws Exception {
        String urlString = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeId + "&key=" + apiKey;

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        String line, outputString = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while ((line = reader.readLine()) != null) {
            outputString += line;
        }

        JSONObject jsonObject = new JSONObject(outputString);
        JSONObject result = jsonObject.getJSONObject("result");
        String name = result.getString("name");
        String address = result.getString("formatted_address");
        String photoReference = result.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
        String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoReference + "&key=" + apiKey;
        String description = result.has("description") ? result.getString("description") : "No description available";
        JSONArray types = result.getJSONArray("types");
        StringBuilder cuisines = new StringBuilder();
        for (int i = 0; i < types.length(); i++) {
            cuisines.append(types.getString(i));
            if (i < types.length() - 1) {
                cuisines.append(", ");
            }
        }

        Candidate restaurant = new Candidate();
        restaurant.setPlaceId(placeId);
        restaurant.setName(name);
        restaurant.setPhotoUrl(photoUrl);

        return restaurant;
    }

    public String getPlaceIdFromGooglePlaces(String restaurantName) throws Exception {
        String urlString = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input="
                + URLEncoder.encode(restaurantName, "UTF-8") + "&inputtype=textquery&fields=place_id&key=" + apiKey;

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        String line, outputString = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while ((line = reader.readLine()) != null) {
            outputString += line;
        }

        JSONObject jsonObject = new JSONObject(outputString);
        String placeId = jsonObject.getJSONArray("candidates").getJSONObject(0).getString("place_id");

        return placeId;
    }

}



