package com.kkansal.gatekeeper.smart_gateway_service.client;

import com.kkansal.gatekeeper.smart_gateway_service.data.RouteEntry;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Set;

@Component
public class UpstreamClient {

    // Hop-by-hop headers describe a single connection and must not be forwarded (RFC 9110 §7.6.1).
    // Host and Content-Length are recomputed by the client for the upstream call.
    private static final Set<String> EXCLUDED_HEADERS = Set.of(
            "connection", "keep-alive", "proxy-authenticate", "proxy-authorization",
            "te", "trailer", "transfer-encoding", "upgrade", "host", "content-length");

    private final RestClient restClient;

    public UpstreamClient(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    public ResponseEntity<byte[]> forward(RouteEntry routeEntry, HttpServletRequest request) throws IOException {

        URI uri = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(routeEntry.host())
                .port(routeEntry.port())
                .path(routeEntry.targetPath())
                .query(request.getQueryString())
                .build(true)
                .toUri();

        byte[] body = request.getInputStream().readAllBytes();

        RestClient.RequestBodySpec spec = restClient.method(HttpMethod.valueOf(request.getMethod()))
                .uri(uri)
                .headers(headers -> copyRequestHeaders(request, headers));

        if (body.length > 0) {
            spec.body(body);
        }

        try {
            // exchange() skips RestClient's default 4xx/5xx exceptions, so upstream errors pass through to the caller as-is
            return spec.exchange((clientRequest, clientResponse) -> ResponseEntity
                    .status(clientResponse.getStatusCode())
                    .headers(headers -> copyResponseHeaders(clientResponse.getHeaders(), headers))
                    .body(clientResponse.getBody().readAllBytes()));
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Upstream unreachable", e);
        }
    }

    private void copyRequestHeaders(HttpServletRequest request, HttpHeaders target) {
        for (String name : Collections.list(request.getHeaderNames())) {
            if (!EXCLUDED_HEADERS.contains(name.toLowerCase())) {
                target.addAll(name, Collections.list(request.getHeaders(name)));
            }
        }
    }

    private void copyResponseHeaders(HttpHeaders source, HttpHeaders target) {
        source.forEach((name, values) -> {
            if (!EXCLUDED_HEADERS.contains(name.toLowerCase())) {
                target.addAll(name, values);
            }
        });
    }
}
