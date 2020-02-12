package com.ctzn.webfluxtelegrambotclient.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Event {
    String type;
    String time;
    String message;
    String payload1;
    String payload2;
    String payload3;
}
