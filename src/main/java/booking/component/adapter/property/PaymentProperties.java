package booking.component.adapter.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "services.jbank")
@Component
@Getter
@Setter
public class PaymentProperties {
    private String url;
    private String merchantId;
    private String merchantToken;
}
