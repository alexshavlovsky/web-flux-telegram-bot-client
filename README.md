# Spring Boot WebFlux RxJava2 Thymeleaf WebSockets Telegram Bot API Client

This is a browser version of this console client: [RxJava Telegram Bot API Console Client](https://github.com/alexshavlovsky/rxjava-telegram-bot-api-client)

## Build and run instructions

```
Build and run the Spring Boot application. Open the URL in a browser:
For the first time:
localhost:8080/token/{TELEGRAM_BOT_API_TOKEN}
Next time just open:
localhost:8080
it will use the latest token and the most recent chat
```

## Technology Stack

Component                      | Technology
---                            | ---
Backend engine                 | Spring Boot WebFlux + Reactive WebSockets
Template engine                | Thymeleaf
Java Reactive extensions       | RxJava v2.2
Http client                    | Spring ProjectReactor Netty WebClient
Frontend engine                | Pure JS + WebSockets + Bootstrap
