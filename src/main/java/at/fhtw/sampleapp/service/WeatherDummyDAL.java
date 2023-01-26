package at.fhtw.sampleapp.service;

import at.fhtw.sampleapp.model.Weather;

import java.util.ArrayList;
import java.util.List;

public class WeatherDummyDAL {
    private final List<Weather> weatherData;

    public WeatherDummyDAL() {
        weatherData = new ArrayList<>();
        weatherData.add(new Weather(1, "Vienna", 9.f));
        weatherData.add(new Weather(2, "Berlin", 8.f));
        weatherData.add(new Weather(3, "Tokyo", 12.f));
    }

    // GET /weather/:id
    public Weather getWeather(Integer ID) {
        Weather foundWaether = weatherData.stream()
                .filter(waether -> ID == waether.getId())
                .findAny()
                .orElse(null);

        return foundWaether;
    }

    // GET /weather
    public List<Weather> getWeather() {
        return weatherData;
    }

    // POST /weather
    public void addWeather(Weather weather) {
        weatherData.add(weather);
    }
}
