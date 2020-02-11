package telegrambot.apimodel;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Chat implements Serializable {
    @EqualsAndHashCode.Include
    Long id;
    String type;
    String title;
    String username;
    String first_name;
    String last_name;
}
