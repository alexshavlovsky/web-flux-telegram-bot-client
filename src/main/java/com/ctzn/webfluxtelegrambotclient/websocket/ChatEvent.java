package com.ctzn.webfluxtelegrambotclient.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.event.Level;
import org.slf4j.event.SubstituteLoggingEvent;
import telegrambot.apimodel.Message;
import telegrambot.apimodel.User;

import java.util.Date;

import static telegrambot.MessageFormatter.*;

@Data
@AllArgsConstructor
class ChatEvent {
    String type;
    String time;
    String message;
    String payload1;
    String payload2;
    String payload3;

    static ChatEvent fromLoggingEvent(SubstituteLoggingEvent e) {
        String time = formatTime(new Date(e.getTimeStamp()));
        String message = formatLogMessage(e);
        String level = e.getLevel().toString();
        return new ChatEvent("log", time, message, level, "", "");
    }

    static ChatEvent fromMessage(Message message, User botUser) {
        String name = formatName(message.getFrom());
        String dir = formatDirection(message.getFrom(), botUser);
        String time = formatTime(message.getDate());
        String msgText = messageToText(message);
        return new ChatEvent("msg", time, msgText, name, dir, "");
    }

    static ChatEvent fromError(Throwable e) {
        SubstituteLoggingEvent event = new SubstituteLoggingEvent();
        event.setLevel(Level.ERROR);
        event.setTimeStamp(new Date().getTime());
        event.setMessage(e.getMessage());
        return fromLoggingEvent(event);
    }

    private static final ObjectMapper json = new ObjectMapper();

    String asJson() {
        try {
            return json.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            try {
                return json.writeValueAsString(ChatEvent.fromError(e));
            } catch (JsonProcessingException ex) {
                return "{}";
            }
        }
    }
}
