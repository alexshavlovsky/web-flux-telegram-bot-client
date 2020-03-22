package com.ctzn.webfluxtelegrambotclient.websocket;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import telegrambot.TelegramBot;

import static com.ctzn.webfluxtelegrambotclient.websocket.ChatEvent.fromMessage;

// outgoing message flow:
// text input -> raw text string -> websocket stream -> frontend events subject -> telegram bot client -> telegram api
//                                                     \                       /
//                                                      |    this connector   |
//                                /--------------------/        boundary       \------------------------------------\
// incoming message flow:        /                                                                                   \
// telegram api -> telegram bot -> bot message observable + log event observable -> chat event adapter -> json string ->
// -> websocket stream -> js dispatcher -> html adapter -> chat console appender

class BotEventsWebSocketConnectorAdapter {
    static Mono<Void> connect(TelegramBot bot, WebSocketSession session) {
        PublishSubject<String> frontendEvents = PublishSubject.create();
        Observable<ChatEvent> botEvents = Observable.merge(
                bot.logMessageObservable().map(ChatEvent::fromLoggingEvent),
                bot.messageObservable(frontendEvents).map(m -> fromMessage(m, bot.botUser))
        );
        Flux<String> jsonEvents = Flux.from(botEvents.map(ChatEvent::asJson).toFlowable(BackpressureStrategy.BUFFER));
        return session.send(jsonEvents.map(session::textMessage))
                .and(session.receive().map(WebSocketMessage::getPayloadAsText).doOnNext(frontendEvents::onNext));
    }
}
