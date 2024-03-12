package com.example.downstream.controller;

import com.example.downstream.service.CoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/core")
@RequiredArgsConstructor
public class CoreController {

    private final CoreService coreService;

    @GetMapping
    public ResponseEntity<?> getEntity() {
        return ResponseEntity.ok(coreService.getResponse());
    }

    @PutMapping("/response-weight")
    public ResponseEntity<?> putEntity(@RequestBody Map<String, Double> requestBody) {
        return ResponseEntity.ok(coreService.setResponseWeight(requestBody));
    }

    @PutMapping("/response-time")
    public ResponseEntity<?> putResponseTime(@RequestBody Map<String, Integer> requestBody) {
        return ResponseEntity.ok(coreService.setSlowResponseTime(requestBody));
    }

}
