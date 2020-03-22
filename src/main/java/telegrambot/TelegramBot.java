package telegrambot;

import io.reactivex.Observable;
import io.reactivex.subjects.ReplaySubject;
import org.slf4j.event.SubstituteLoggingEvent;
import telegrambot.apimodel.Chat;
import telegrambot.apimodel.Message;
import telegrambot.apimodel.User;
import telegrambot.httpclient.NettyAsyncHttpClient;
import telegrambot.io.BotRepository;
import telegrambot.io.TokensRepository;
import telegrambot.pollingclient.LongPollingClient;
import telegrambot.pollingclient.PollingClient;

import java.util.concurrent.TimeUnit;

public class TelegramBot {
    private final RxLogger logger = RxLogger.newInstance();

    private final BotRepository botRepository;
    public final User botUser;

    private final PollingClient pollingClient;

    private final ReplaySubject<Chat> latestChat$ = ReplaySubject.create();

    public TelegramBot(String token) throws BotException {
        TokensRepository tokensRepository = new TokensRepository();

        if (token == null || token.isEmpty()) {
            logger.info("Try to load a token from the file system...");
            token = tokensRepository.getMostRecentToken();
            if (token == null) throw new BotException(
                    "Can't find any saved token.\nPlease provide an API token via URL path: http://localhost:8080/token/{telegram_bot_api_token}\nYou can get one from BotFather.");
        }

        pollingClient = new LongPollingClient(token, new NettyAsyncHttpClient(), logger);

        if (tokensRepository.containsToken(token)) botUser = tokensRepository.getUserForToken(token);
        else {
            logger.info("Validate a new token against Telegram API...");
            try {
                botUser = pollingClient.getMe().timeout(5, TimeUnit.SECONDS).blockingGet();
            } catch (Exception e) {
                throw new BotException("Unable to validate token against Telegram API: " + e.getMessage(), e);
            }
            tokensRepository.saveToken(token, botUser);
        }
        botRepository = new BotRepository(token);
        botRepository.saveUser(botUser);

        logger.info("Current bot name: {}", MessageFormatter.formatName(botUser));

        botRepository.getLatestChatOptional().ifPresent(latestChat$::onNext);
    }

    public Observable<SubstituteLoggingEvent> logMessageObservable() {
        return logger.loggingEventObservable();
    }

    public Observable<Message> messageObservable(Observable<String> outgoingTextMessages) {

        Observable<Chat> latestChatObservable = latestChat$.distinctUntilChanged().takeUntil(outgoingTextMessages.lastElement().toObservable());

        return botRepository.messageHistoryOrderedObservable()

                .concatWith(
                        pollingClient
                                .connect(outgoingTextMessages, latestChatObservable,
                                        lostMessage -> logger.info("A current chat is not assigned. Please send a message to this bot first!"))
                                .doOnNext(botRepository::saveMessage)
                                .doOnNext(message -> latestChat$.onNext(message.getChat()))
                )

                .mergeWith(
                        latestChatObservable.map(MessageFormatter::formatChat)
                                .doOnNext(chat -> logger.info("Current chat is set to: {}", chat))
                                .ignoreElements()
                );
    }

}
