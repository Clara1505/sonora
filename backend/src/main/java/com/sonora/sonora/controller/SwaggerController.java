package com.sonora.sonora.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Redirects to the Swagger UI entry point. */
@Controller
public class SwaggerController {

    /**
     * Redirects /swagger to the Springdoc Swagger UI.
     *
     * @return redirect target
     */
    @GetMapping("/swagger")
    public String swagger() {
        return "redirect:/swagger-ui/index.html";
    }
}
