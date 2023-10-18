package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {

    @Autowired
    public StatsClient(@Value("${server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> addEndpointHit(EndpointHitDto hitDto) {
        return post("/hit", new EndpointHitDto(
                hitDto.getApp(),
                hitDto.getUri(),
                hitDto.getIp(),
                hitDto.getTimestamp()
        ));
    }

    public ResponseEntity<Object> getStats(List<String> uris, boolean unique, String start, String end) {
        Map<String, Object> parameters = Map.of(
                "uris", uris,
                "unique", end,
                "start", start,
                "end", end
        );
        return get("/stats?start={strStart}&end={strEnd}&uris={uris}&unique={unique}", parameters);
    }
}
