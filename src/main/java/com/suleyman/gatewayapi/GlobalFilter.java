package com.suleyman.gatewayapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    @Value("auth-control.authUrl")
    private String authUrl;

    public GlobalFilter(){super(Config.class);}

    private String isAuthorizationValid(String authorizationHeader){
        boolean isValid=true;
        RestTemplate restTemplate= new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization",authorizationHeader);
        HttpEntity request = new HttpEntity<>(headers);
        restTemplate.setErrorHandler( new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError( HttpStatus statusCode)
            {return false;}
        });
        ResponseEntity<String> response=restTemplate.exchange(authUrl, HttpMethod.GET,request,String.class);
        if (response.getStatusCodeValue() ==401){
            isValid=false;
        }
        if (isValid){
            return response.getBody();
        }
        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange,String err){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }
    @Override
    public GatewayFilter apply(GlobalFilter.Config config) {

        return ((exchange, chain) -> {
            ServerHttpRequest request =exchange.getRequest();
            log.info("Path --> "+request.getPath().toString() + " IP: " +request.getRemoteAddress());
            if (request.getPath().toString().equals("/auth/login")
                    ||request.getPath().toString().equals("/auth/forgot-password")
                    ||request.getPath().toString().equals("/auth/register"))
            {
                ServerHttpRequest modifiedRequest=exchange.getRequest().mutate().build();
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            }else if (!request.getHeaders().containsKey("Authorization")) {
                return this.onError(exchange,"No Authorization header");
            }
            String authorizationHeader = request.getHeaders().get("Authorization").get(0);
            String username = isAuthorizationValid(authorizationHeader);
            if (username==null){
                return this.onError(exchange,"Invalid Authorization header");
            }
            ServerHttpRequest modifiedRequest =exchange.getRequest().mutate()
                    .header("User",username)
                    .build();
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        });
    }


    public static class Config{
        private String name;
    }
}
