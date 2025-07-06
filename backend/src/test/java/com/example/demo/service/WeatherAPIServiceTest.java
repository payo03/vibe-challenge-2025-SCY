package com.example.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WeatherAPIServiceTest {
    WeatherAPIService weatherAPIService;
    @Mock
    RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        weatherAPIService = new WeatherAPIService();
    }

    @Test
    void testGetLATLNGFromCity_empty() {
        Map<String, Double> result = weatherAPIService.getLATLNGFromCity("");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetLocationKeyByLATLNG_empty() {
        String key = weatherAPIService.getLocationKeyByLATLNG(null);
        assertNull(key);
    }

    @Test
    void testGetDayForecast_empty() {
        Map<String, Map<String, Object>> result = weatherAPIService.getDayForecast("");
        assertTrue(result.isEmpty());
    }

    // getWeatherString 등은 외부 API mock 필요, 통합테스트 권장
} 