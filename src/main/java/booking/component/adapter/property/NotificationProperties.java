package booking.component.adapter.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "services.notification")
@Component
@Getter
@Setter
public class NotificationProperties {
    private String url;
    private String login;
    private String password;
}
