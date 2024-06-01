package com.vts.vaccation_tracking_system;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PruebaController {

    @GetMapping("/prueba")
    public String prueba() {
        return "Es solo un punto final de prueba ver si funciona";
    }
}
