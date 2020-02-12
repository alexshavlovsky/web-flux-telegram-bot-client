package com.ctzn.webfluxtelegrambotclient.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.BackpressureStrategy;
import io.reactivex.subjects.PublishSubject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import telegrambot.BotException;
import telegrambot.TelegramBot;

import java.util.Map;

import static telegrambot.MessageFormatter.*;

@Component
@Scope("prototype")
public class MessageHandler implements WebSocketHandler {

    private static final ObjectMapper json = new ObjectMapper();

    private static String toJson(Event event) {
        try {
            return json.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            try {
                return json.writeValueAsString(fromError(e));
            } catch (JsonProcessingException ex) {
                return "{}";
            }
        }
    }

    private static String getPathParam(WebSocketSession webSocketSession, String uriTemplate, String key) {
        String path = webSocketSession.getHandshakeInfo().getUri().getPath();
        UriTemplate template = new UriTemplate(uriTemplate);
        Map<String, String> parameters = template.match(path);
        return parameters.get(key);
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        String token = getPathParam(webSocketSession, "/messages/{token}", "token");
        try {
            PublishSubject<String> subject = PublishSubject.create();
            TelegramBot bot = new TelegramBot(token);
            Flux<String> messages = Flux.from(
                    bot.logMessageObservable().map(e -> toJson(fromLoggingEvent(e)))
                            .mergeWith(bot.messageObservable(subject).map(m -> toJson(fromMessage(m, bot.botUser))))
                            .toFlowable(BackpressureStrategy.BUFFER)
            );
            return webSocketSession.send(messages
                    .map(webSocketSession::textMessage))
                    .and(webSocketSession.receive().map(WebSocketMessage::getPayloadAsText).doOnNext(subject::onNext));
        } catch (BotException e) {
            return webSocketSession.send(Mono.just(toJson(fromError(e))).concatWith(Flux.never())
                    .map(webSocketSession::textMessage));
        }
    }
}
