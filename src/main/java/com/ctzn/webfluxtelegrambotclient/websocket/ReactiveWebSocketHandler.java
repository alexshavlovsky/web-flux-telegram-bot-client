package com.ctzn.webfluxtelegrambotclient.websocket;

import io.reactivex.BackpressureStrategy;
import io.reactivex.subjects.PublishSubject;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import telegrambot.BotException;
import telegrambot.TelegramBot;

@Component
public class ReactiveWebSocketHandler implements WebSocketHandler {

    private TelegramBot bot = new TelegramBot(null);
    private PublishSubject<String> subject = PublishSubject.create();

    public ReactiveWebSocketHandler() throws BotException {
    }

    private Flux<String> intervalFlux = Flux.from(
            bot.logMessageObservable().mergeWith(bot.messageObservable(subject))
                    .toFlowable(BackpressureStrategy.BUFFER)
    );

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        return webSocketSession.send(intervalFlux
                .map(webSocketSession::textMessage))
                .and(webSocketSession.receive()
                        .map(WebSocketMessage::getPayloadAsText).doOnNext(subject::onNext).log());
    }
}

//        WebSocketClient client = new ReactorNettyWebSocketClient();
//        client.execute(
//          URI.create("ws://localhost:8080/event-emitter"),
//          session -> session.send(
//            Mono.just(session.textMessage("event-spring-reactive-client-websocket")))
//            .thenMany(session.receive()
//              .map(WebSocketMessage::getPayloadAsText)
//              .log())
//            .then())
//            .block(Duration.ofSeconds(10L));