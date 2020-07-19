package com.suleyman.gatewayapi.configuraiton;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class CorsConfiguration {
    private static final String ALLOWED_HEADERS = "x-requested-with, authorization, Content-Type, Authorization, credential, X-XSRF-TOKEN";
    private static final String ALLOWED_METHODS ="GET, PUT, POST, DELETE, OPTIONS";
    private static final String ALLOWED_ORIGIN="*";
    private static final String MAX_AGE="3600";

    @Bean
    public WebFilter corsFilter(){
        return (serverWebExchange, webFilterChain) -> {
            ServerHttpRequest request = serverWebExchange.getRequest();
            if (CorsUtils.isCorsRequest(request)){
                ServerHttpResponse response =serverWebExchange.getResponse();
                HttpHeaders headers = request.getHeaders();
                headers.add("Access-Control-Allow-Methods",ALLOWED_METHODS);
                headers.add("Access-Control-Max-Age",MAX_AGE);
                headers.add("Access-Control-Allow-Headers",ALLOWED_HEADERS);
                headers.add("Access-Control-Allow-Origin",ALLOWED_ORIGIN);
                if (request.getMethod() == HttpMethod.OPTIONS) {
                    response.setStatusCode(HttpStatus.OK);
                    return Mono.empty();
                }
            }
            return webFilterChain.filter(serverWebExchange);
        };
    }
}
