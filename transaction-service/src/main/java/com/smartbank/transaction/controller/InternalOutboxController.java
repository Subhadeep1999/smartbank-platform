package com.smartbank.transaction.controller;

import com.smartbank.transaction.service.OutboxService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/internal/outbox")
public class InternalOutboxController {

    private final OutboxService outboxService;

    public InternalOutboxController(
            OutboxService outboxService
    ) {
        this.outboxService = outboxService;
    }

    @PostMapping("/{eventId}/replay")
    public ResponseEntity<Void> replay(
            @PathVariable UUID eventId
    ) {

        outboxService.replay(eventId);

        return ResponseEntity.accepted().build();
    }
}