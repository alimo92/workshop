/* (C) 2024 */
package com.workshop.api.user;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {

  @Autowired private WebTestClient webTestClient;

  @Test
  void test() {
    UserRequestBody userRequest = new UserRequestBody("first", "last", 0);
    webTestClient
        .post()
        .uri("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(userRequest)
        .header(HttpHeaders.AUTHORIZATION, "Bearer test")
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(UserResponse.class)
        .consumeWith(
            result -> {
              UserResponse userResponse = result.getResponseBody();
              assertNotNull("userResponse not null", userResponse);
              assertEquals(
                  "firstName assertion", userRequest.firstName(), userResponse.firstName());
              assertEquals("lastName assertion", userRequest.lastName(), userResponse.lastName());
              assertEquals("balance assertion", userRequest.balance(), userResponse.balance());
            });
  }
}
