package com.exmaple.sender.controller;

import com.exmaple.sender.service.KafkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("replay")
@RequiredArgsConstructor
public class ReplayController {
    final KafkaService kafkaService;

    @GetMapping
    ResponseEntity<String> replay() throws Exception {
        final StringBuffer message = new StringBuffer();
        message.append("You: Current Date?</br>");
        message.append(kafkaService.kafkaRequestReply("Current Date?").toString());
        return ResponseEntity.ok(message.toString());
    }
}
