package booking.configuration;

import booking.component.adapter.property.NotificationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {
    @Bean
    public RestClient notificationRestClient (NotificationProperties prop) {
        return RestClient
            .builder()
            .baseUrl(prop.getUrl())
            .defaultHeaders(headers -> {
                headers.setBasicAuth(prop.getLogin(),prop.getPassword());
                headers.setContentType(MediaType.APPLICATION_JSON);
            })
            .build();
    }
}
