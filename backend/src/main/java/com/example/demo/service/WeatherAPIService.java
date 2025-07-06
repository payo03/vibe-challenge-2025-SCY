package com.example.demo.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherAPIService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherAPIService.class);
    
    public static final Boolean isTrial = true; // 무료버전으로 인해 5일이 최대
    // 1. properties. accu.api.weather.url의 5days값을 동적으로 변경
    // 2. isTrial 구문 정리

    public static final String ENDPOINT_FORMAT_LATLNG = "%s?q=%s&key=%s&limit=1&language=en";
    public static final String ENDPOINT_FORMAT_LOCATION = "%s?q=%s&apikey=%s&language=en";
    public static final String ENDPOINT_FORMAT_WEATHER = "%s%s?apikey=%s&language=ko&metric=true";  // 섭씨온도 : ko, 화씨온도 : en

    @Value("${opencage.api.key}")
    private String openCageKey;
    @Value("${opencage.api.url}")
    private String openCageURL;

    @Value("${accu.api.key}")
    private String accuWeatherKey;
    @Value("${accu.api.location.url}")
    private String accuLocationURL;
    @Value("${accu.api.weather.url}")
    private String accuWeatherURL;
    
    @Autowired
    @Qualifier("defaultRestTemplate")
    RestTemplate restTemplate;

    // 도시-날짜를 통한 날씨 가져오기
    public String getWeatherString(Map<String, Object> infoMap) {
        String result = "";

        String cityName = infoMap.containsKey("cityName") 
            ? String.valueOf(infoMap.get("cityName")) 
            : null;
        String paramDate = infoMap.containsKey("paramDate") 
            ? String.valueOf(infoMap.get("paramDate")) 
            : null;

        // 1. 도시-날짜가 없으면 Return [1차 Validation]
        if (cityName == null || paramDate == null) return result;
        
        // 2. 위도-경도를 불러오지 못하면 Return [2차 Validation]
        Map<String, Double> latlngMap = getLATLNGFromCity(cityName);
        if(latlngMap.isEmpty()) return result;

        // 3. 위도-경도의 Key값 가져오지 못하면 Return [3차 Validation]
        String locationKey = getLocationKeyByLATLNG(latlngMap);
        if (locationKey == null || locationKey.isBlank()) return result;

        // 4. Key값 기준으로 날씨 GET
        Map<String, Map<String, Object>> weatherMap = getDayForecast(locationKey);

        // 5. 사용자의 여행날짜 기준으로 날씨 Return
        List<String> travelDateList = Arrays.asList(paramDate.split(","));
        LocalDate startDate = null;
        LocalDate endDate = null;
        if (!travelDateList.isEmpty()) {
            startDate = LocalDate.parse(travelDateList.get(0));
            endDate = LocalDate.parse(travelDateList.get(travelDateList.size() - 1));
        }
        
        // 6. weatherMap의 날짜가 여행 기간에 포함되는지 확인하여 weatherInfoList에 추가
        List<Map<String, Object>> weatherInfoList = new ArrayList<>();
        for(String date : weatherMap.keySet()) {
            LocalDate weatherDate = LocalDate.parse(date);
            
            if (!weatherDate.isBefore(startDate) && !weatherDate.isAfter(endDate)) {
                Map<String, Object> weatherInfo = weatherMap.get(date);

                weatherInfoList.add(weatherInfo);
            }
        }

        // 현재 무료버전으로인해 현재일 + 5일까지만 날짜 가져옴
        if(isTrial) {
            for(String date : weatherMap.keySet()) {
                weatherInfoList.add(weatherMap.get(date));
            }
        }

        // 7. 날짜정보 Return
        StringBuilder response = new StringBuilder();
        response.append("🌤️ **날씨 정보**\n\n");
        for (Map<String, Object> weatherInfo : weatherInfoList) {
            String date = (String) weatherInfo.get("date");
            String minTemp = String.valueOf(weatherInfo.get("min_temp"));
            String maxTemp = String.valueOf(weatherInfo.get("max_temp"));
            String dayPhrase = (String) weatherInfo.get("day_phrase");
            String nightPhrase = (String) weatherInfo.get("night_phrase");
            
            response.append("📅 **").append(date).append("**\n");
            response.append("🌡️ **온도**: ").append(minTemp).append("°C ~ ").append(maxTemp).append("°C\n");
            response.append("☀️ **낮**: ").append(dayPhrase).append("\n");
            response.append("🌙 **밤**: ").append(nightPhrase).append("\n\n");
        }
        response.append("즐거운 여행 되세요! ✈️");

        return response.toString();
    }
    
    /* ----------------------------------------------------------------------------------- */
    /* ------------------------------------- 내부함수 ------------------------------------- */
    /* ----------------------------------------------------------------------------------- */

    // 도시이름으로부터 위도-경도 GET [OpenCage]
    @SuppressWarnings({ "unchecked" })
    public Map<String, Double> getLATLNGFromCity(String cityName) {
        Map<String, Double> returnMap = new HashMap<String, Double>();
        if (cityName == null || cityName.isBlank()) return returnMap;

        try{
            String endpointURL = String.format(ENDPOINT_FORMAT_LATLNG, openCageURL, cityName, openCageKey);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                endpointURL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");

                if (results != null && !results.isEmpty()) {
                    Map<String, Object> geometry = (Map<String, Object>) results.get(0).get("geometry");
                    double lat = ((Number) geometry.get("lat")).doubleValue();
                    double lng = ((Number) geometry.get("lng")).doubleValue();

                    logger.info("도시 [{}]의 위도/경도 조회 성공 → lat={}, lng={}", cityName, lat, lng);

                    returnMap.put("lat", lat);
                    returnMap.put("lng", lng);
                }
            }

            logger.warn("도시 [{}]의 위도/경도 결과가 비어 있습니다.", cityName);
        } catch (Exception e) {
            logger.error("OpenCage API 호출 중 오류 발생 - 도시명: [{}]", cityName, e);
        }

        return returnMap;
    }

    // 위도-경도로부터 LocationKey발급 [AccuWeather]
    @SuppressWarnings({ "null" })
    public String getLocationKeyByLATLNG(Map<String, Double> infoMap) {
        String locationKey = null;
        if (infoMap == null || infoMap.isEmpty()) return locationKey;

        double lat = infoMap.get("lat");
        double lng = infoMap.get("lng");
        String latlng = String.format("%.6f,%.6f", lat, lng);
    
        String endpointURL = String.format(ENDPOINT_FORMAT_LOCATION, accuLocationURL, latlng, accuWeatherKey);
    
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                endpointURL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );
    
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                locationKey = String.valueOf(response.getBody().get("Key"));
                logger.info("LocationKey 조회 성공 → [{}] for lat={}, lng={}", locationKey, lat, lng);
            }
        } catch (Exception e) {
            logger.error("AccuWeather LocationKey API 호출 실패", e);
        }

        return locationKey;
    }

    // LocationKey값 기준으로 Weather 가져오기 [AccuWeather]
    /* ################################################# */
    /* ####### 07-06. 무료버전으로 인해 5일이 최대임 ####### */
    /* ################################################# */
    @SuppressWarnings("unchecked")
    public Map<String, Map<String, Object>> getDayForecast(String locationKey) {
    Map<String, Map<String, Object>> forecastMap = new HashMap<>();
        if (locationKey == null || locationKey.isBlank()) return forecastMap;

        String endpointURL = String.format(ENDPOINT_FORMAT_WEATHER, accuWeatherURL, locationKey, accuWeatherKey);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                endpointURL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> dailyForecasts = (List<Map<String, Object>>) body.get("DailyForecasts");

                for (Map<String, Object> day : dailyForecasts) {
                    Map<String, Object> infoMap = new HashMap<>();
                    String isoDate = String.valueOf(day.get("Date"));     // ex. 2025-07-08T07:00:00+09:00
                    String date = isoDate.split("T")[0];                // → "2025-07-08"
                    infoMap.put("date", date);

                    Map<String, Object> temp = (Map<String, Object>) day.get("Temperature");
                    infoMap.put("min_temp", ((Map<String, Object>) temp.get("Minimum")).get("Value"));
                    infoMap.put("max_temp", ((Map<String, Object>) temp.get("Maximum")).get("Value"));

                    Map<String, Object> dayWeather = (Map<String, Object>) day.get("Day");
                    Map<String, Object> nightWeather = (Map<String, Object>) day.get("Night");

                    infoMap.put("day_phrase", dayWeather.get("IconPhrase"));
                    infoMap.put("night_phrase", nightWeather.get("IconPhrase"));

                    forecastMap.put(date, infoMap);
                }

                logger.info("AccuWeather 5일 예보 조회 성공: {}건", forecastMap.keySet().size());
            } else {
                logger.warn("AccuWeather 5일 예보 조회 실패: 응답 없음");
            }

        } catch (Exception e) {
            logger.error("AccuWeather 5일 예보 API 호출 실패", e);
        }

        return forecastMap;
    }
}
