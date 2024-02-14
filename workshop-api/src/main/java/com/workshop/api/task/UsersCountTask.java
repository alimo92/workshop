/* (C) 2023-2024 */
package com.workshop.api.task;

import com.workshop.api.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UsersCountTask {

  @Autowired private UserService userService;

  // Ref for crone expressions: https://www.freeformatter.com/cron-expression-generator-quartz.html
  @Scheduled(cron = "0 */3 * * * *", zone = "UTC")
  private void printUsersCount() {
    log.info("Count={}", userService.getUsersCount("name"));
  }
}
