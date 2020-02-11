package telegrambot.apimodel;

import lombok.Data;

@Data
public class Update {
    Long update_id;
    Message message;
}
