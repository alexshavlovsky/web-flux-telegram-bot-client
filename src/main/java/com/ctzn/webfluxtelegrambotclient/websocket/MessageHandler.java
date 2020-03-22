package com.ctzn.webfluxtelegrambotclient.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import telegrambot.BotException;
import telegrambot.TelegramBot;

import java.util.Map;

import static com.ctzn.webfluxtelegrambotclient.websocket.BotEventsWebSocketConnectorAdapter.connect;
import static com.ctzn.webfluxtelegrambotclient.websocket.ChatEvent.fromError;

@Component
public class MessageHandler implements WebSocketHandler {

    private static String getPathParam(WebSocketSession webSocketSession, String uriTemplate, String key) {
        String path = webSocketSession.getHandshakeInfo().getUri().getPath();
        UriTemplate template = new UriTemplate(uriTemplate);
        Map<String, String> parameters = template.match(path);
        return parameters.get(key);
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        try {
            String token = getPathParam(webSocketSession, "/messages/{token}", "token");
            TelegramBot bot = new TelegramBot(token);
            return connect(bot, webSocketSession);
        } catch (BotException e) {
            return webSocketSession.send(Mono.just(fromError(e).asJson()).concatWith(Flux.never())
                    .map(webSocketSession::textMessage));
        }
    }
}
