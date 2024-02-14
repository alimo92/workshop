/* (C) 2023-2024 */
package com.workshop.api.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@EnableScheduling
@Slf4j
public class AppConfig {
  private static final int DEFAULT_TIMEOUT_MS = 5_000;

  @Bean(name = "jwk")
  WebClient jwkClient(@Value("${client.jwks.base.url}") String baseUrl) {
    HttpClient httpClient =
        HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, DEFAULT_TIMEOUT_MS)
            .responseTimeout(Duration.ofMillis(DEFAULT_TIMEOUT_MS))
            .doOnConnected(
                conn ->
                    conn.addHandlerLast(
                            new ReadTimeoutHandler(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS))
                        .addHandlerLast(
                            new WriteTimeoutHandler(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS)));

    return WebClient.builder()
        .baseUrl(baseUrl)
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();
  }
}
