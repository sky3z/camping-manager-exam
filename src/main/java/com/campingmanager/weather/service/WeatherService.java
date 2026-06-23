package com.campingmanager.weather.service;

import com.campingmanager.weather.dto.WeatherResponse;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class WeatherService {

    @Value("${openweather.api.key:}")
    private String apiKey;

    // chiamo l'api di openweathermap e mappo solo i campi che mi servono
    public WeatherResponse getWeather(String city) {
        JsonNode root = RestClient.create()
                .get()
                .uri("https://api.openweathermap.org/data/2.5/weather?q={city}&appid={key}&units=metric&lang=it",
                        city, apiKey)
                .retrieve()
                .body(JsonNode.class);

        WeatherResponse response = new WeatherResponse();
        response.setCity(root.get("name").asText());
        response.setTemperature(root.get("main").get("temp").asDouble());
        response.setFeelsLike(root.get("main").get("feels_like").asDouble());
        response.setHumidity(root.get("main").get("humidity").asInt());
        response.setDescription(root.get("weather").get(0).get("description").asText());
        return response;
    }
}
