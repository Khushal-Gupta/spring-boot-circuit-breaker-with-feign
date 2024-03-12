package com.example.upstream.controller;

import com.example.upstream.service.CoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/core")
@RequiredArgsConstructor
public class CoreController {

    private final CoreService coreService;

    @GetMapping
    public ResponseEntity<?> getDownstream() {
        return ResponseEntity.ok(coreService.getDownstreamResponse());
    }
}
