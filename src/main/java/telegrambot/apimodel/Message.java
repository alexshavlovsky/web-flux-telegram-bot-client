package telegrambot.apimodel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Message implements Serializable {
    @EqualsAndHashCode.Include
    Long message_id;
    User from;
    @JsonDeserialize(using = UnixTimestampDeserializer.class)
    Date date;
    Chat chat;
    String text;
    String caption;
}
