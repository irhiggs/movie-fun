package org.superbiz.moviefun;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import static com.amazonaws.util.ImmutableMapParameter.of;


@Controller
public class RabbitMessageController {
    private final RabbitTemplate rabbitTemplate;
    private final String queue;

    public RabbitMessageController(RabbitTemplate rabbitTemplate, @Value("${rabbitmq.queue}") String queue) {
        this.rabbitTemplate = rabbitTemplate;
        this.queue = queue;
    }

    @PostMapping("/rabbit")
    public ResponseEntity publishMessage() {
        rabbitTemplate.convertAndSend(queue, "Start message");
        return ResponseEntity.ok(of("response", "This is an unrelated JSON response"));
    }
}
