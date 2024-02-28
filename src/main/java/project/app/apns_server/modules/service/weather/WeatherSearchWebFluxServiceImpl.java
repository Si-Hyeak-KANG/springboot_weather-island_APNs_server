package project.app.apns_server.modules.service.weather;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import project.app.apns_server.modules.dto.WeatherApiResponseDto;

import java.net.URI;

@Slf4j
@Profile({"dev", "prod"})
@Service
@RequiredArgsConstructor
public class WeatherSearchWebFluxServiceImpl implements WeatherSearchService {

    private final WebClient.Builder webClientBuilder;
    private final OpenWeatherUriBuilderService openWeatherUriBuilderService;

    public WeatherApiResponseDto requestCurrWeatherByLocation(double lat, double lon) {
        URI uri = openWeatherUriBuilderService.buildUriByLocation(lat, lon);
        return webClientBuilder.build()
                .method(HttpMethod.GET)
                .uri(uri)
                .retrieve()
                .bodyToMono(WeatherApiResponseDto.class)
                .doOnNext(this::convertTemperatureUnit)
                .doOnNext(response -> log.info("[WeatherSearchWebFluxServiceImpl requestCurrWeatherByLocation] 성공적으로 날씨를 조회하였습니다."))
                .doOnNext(response -> log.debug("[WeatherSearchWebFluxServiceImpl requestCurrWeatherByLocation] 날씨 조회 결과, 온도 = {}", response.getMainDto().getTemp()))
                .doOnError(response -> log.error("날씨 조회를 실패하였습니다.(message={})", response.getMessage()))
                .block();
    }
}
