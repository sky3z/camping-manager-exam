package com.campingmanager.weather.controller;

import com.campingmanager.weather.dto.WeatherResponse;
import com.campingmanager.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService service;

    @GetMapping
    public ResponseEntity<WeatherResponse> getWeather(@RequestParam(defaultValue = "Castello Tesino") String city) {
        return ResponseEntity.ok(service.getWeather(city));
    }
}
