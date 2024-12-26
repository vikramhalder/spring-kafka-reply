package com.exmaple.sender.controller;

import com.exmaple.sender.service.KafkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ReplayController {

    final KafkaService kafkaService;

    @GetMapping(value = {"", "/replay"})
    public ResponseEntity<String> replay() throws Exception {
        final String replayMessage = kafkaService.kafkaRequestReply("Current Date?").toString();
        return ResponseEntity.ok("You: Current Date?</br>".concat(replayMessage));
    }
}
