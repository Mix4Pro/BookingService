package booking.component.adapter;

import booking.dto.request.NotificationRequestDto;
import booking.dto.response.NotificationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationAdapter {
    private final RestClient notificationRestClient;
    public NotificationResponseDto sendNotification (NotificationRequestDto notificationRequestDto) {
        var notificationResponseDto =
            notificationRestClient
                .post()
                .uri("/sending")
                .body(notificationRequestDto)
                .retrieve()
                .toEntity(NotificationResponseDto.class);



        return notificationResponseDto.getBody();
    }
}
