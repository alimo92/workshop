/* (C) 2023 */
package com.workshop.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Ping {
  @GetMapping("/ping")
  public String hello() {
    return "Hello world";
  }
}
