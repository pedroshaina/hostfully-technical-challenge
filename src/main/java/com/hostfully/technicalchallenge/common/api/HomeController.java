package com.hostfully.technicalchallenge.common.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
  @GetMapping(value = "/")
  public String index() {
    return "redirect:swagger-ui.html";
  }
}
