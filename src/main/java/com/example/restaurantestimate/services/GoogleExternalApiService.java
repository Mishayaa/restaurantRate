package com.example.restaurantestimate.services;

import com.example.restaurantestimate.dto.google.Candidate;
import com.example.restaurantestimate.dto.google.GoogleRestaurantInfo;
import com.example.restaurantestimate.dto.google.OpeningHours;
import com.example.restaurantestimate.dto.restaurant.RestaurantDto;
import com.example.restaurantestimate.dto.restaurant.RestaurantPages;
import com.example.restaurantestimate.entities.Restaurant;
import com.example.restaurantestimate.mappers.GooglePlacesApiSerializer;
import com.example.restaurantestimate.repositories.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;


import org.springframework.http.ResponseEntity;


@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleExternalApiService {
    @Value("AIzaSyDzGpFuJK0dhf72YFj-wrhNnpXkIZ5chLM")
    private String apiKey;
    private final GooglePlacesApiSerializer googlePlacesApiSerializer;
    private final RestaurantRepository restaurantRepository;

    public RestaurantPages findRestaurantByName(String name, Integer page, Integer limit) throws Exception {
        URI url = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("maps.googleapis.com")
                .path("/maps/api/place/findplacefromtext/json")
                .queryParam("input", name + "restaurant")
                .queryParam("inputtype", "textquery")
                .queryParam("fields", "photos,photo,formatted_address,name,rating,opening_hours,geometry")
                .queryParam("key", apiKey)

                // .queryParam("type", "italian_restaurant")
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<GoogleRestaurantInfo> responseEntity =
                restTemplate.getForEntity(url, GoogleRestaurantInfo.class);

        Set<Candidate> foundRestaurants = responseEntity.getBody().getCandidates();
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
        //return null;
    }


    public Candidate getRestaurantFromGooglePlaces(String placeId) throws Exception {
        String urlString = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeId + "&key=" + apiKey;
        String line, outputString = "";
        URL url = new URL(urlString);

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                outputString += line;
            }
        } catch (Exception e) {
            e.getMessage();
        }


        JSONObject jsonObject = new JSONObject(outputString);
        JSONObject result = jsonObject.getJSONObject("result");
        String name = result.getString("name");
        String address = result.getString("formatted_address");
        String photoReference = result.has("photos") ? result.getJSONArray("photos").getJSONObject(0).getString("photo_reference") : "";
        String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoReference + "&key=" + apiKey;
        Candidate restaurant = new Candidate();
        double rating = result.has("rating") ? result.getDouble("rating") : 0.0;
        boolean servesBeer = false;
        boolean servesWine = false;
        boolean open_now = result.has("open_now") ? result.getBoolean("open_now") : false;

        JSONArray types = result.getJSONArray("types");
        for (int i = 0; i < types.length(); i++) {
            String type = types.getString(i);
            if (type.equals("bar") || type.equals("liquor_store") || type.equals("night_club")) {
                servesBeer = true;
                servesWine = true;
                break;
            } else if (type.equals("cafe") || type.equals("restaurant") || type.equals("food")) {
                servesBeer = true;
                break;
            }
        }


        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient client = new OkHttpClient();
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setPlaceId(placeId);
        restaurant1.setRating(rating);
        restaurant1.setName(name);
        restaurant1.setPosterUrl(photoUrl);
        restaurant1.setFormatted_address(address);
        restaurant1.setServesBeer(servesBeer);

        restaurantRepository.save(restaurant1);
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            restaurant.setPlaceId(placeId);
            restaurant.setRating(rating);
            restaurant.setId(restaurantRepository.findById(restaurant1.getId()).get().getId());
            restaurant.setName(name);
            restaurant.setOpenNow(open_now);
            restaurant.setServesWine(servesWine);
            restaurant.setServesBeer(servesBeer);
            restaurant.setPhotoUrl(photoUrl);
            restaurant.setFormatted_address(address);
            return restaurant;
        }
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
        return jsonObject.getJSONArray("candidates").getJSONObject(0).getString("place_id");
    }


    public RestaurantPages test2(String name) throws Exception {
        List<Candidate> restaurants = new ArrayList<>();
        String urlString = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + name.replace(" ", "") + "restaurant" + "&key=" + apiKey;

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        String line, outputString = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while ((line = reader.readLine()) != null) {
            outputString += line;
        }

        JSONObject jsonObject = new JSONObject(outputString);
        JSONArray results = jsonObject.getJSONArray("results");

        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);
            String placeId = result.getString("place_id");
            Candidate restaurant = getRestaurantFromGooglePlaces(placeId);
            restaurants.add(restaurant);
        }
        for (Candidate c : restaurants
        ) {
            System.out.println(c.getName() + " " + c.getFormatted_address());
        }


        List<RestaurantDto> rest = restaurants.stream()
                .map(googlePlacesApiSerializer).toList();
        RestaurantPages restaurantPages = new RestaurantPages();
        restaurantPages.setRestaurants(rest);
        restaurantPages.setTotal((long) restaurants.size());


        return restaurantPages;
    }
}




