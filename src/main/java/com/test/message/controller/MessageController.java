package com.test.message.controller;

import com.test.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testMessage")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/sendToUser")
    public Integer sendToUser() {
        return messageService.sendToUser();
    }
}
