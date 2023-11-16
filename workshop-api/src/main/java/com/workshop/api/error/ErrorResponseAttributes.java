/* (C) 2023 */
package com.workshop.api.error;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Component
@Slf4j
public class ErrorResponseAttributes extends DefaultErrorAttributes {

  @Override
  public Map<String, Object> getErrorAttributes(
      WebRequest webRequest, ErrorAttributeOptions options) {

    Map<String, Object> defaultErrorAttributes = super.getErrorAttributes(webRequest, options);

    Map<String, Object> attributes = new HashMap<>();

    attributes.put("message", defaultErrorAttributes.get("message"));
    attributes.put("error", defaultErrorAttributes.get("error"));

    return attributes;
  }
}
