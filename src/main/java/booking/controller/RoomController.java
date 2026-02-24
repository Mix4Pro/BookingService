package booking.controller;

import booking.dto.request.RoomRequestDto;
import booking.dto.response.roomResponse.RoomPageResponseDto;
import booking.dto.response.roomResponse.RoomResponseDto;
import booking.service.roomService.RoomService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/booking/api/v1/rooms")
@RequiredArgsConstructor
@Validated
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<RoomPageResponseDto> getAllRooms(Pageable pageable) {
        return ResponseEntity.ok(roomService.getAllRooms(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDto> getRoomById (@PathVariable @Min(value = 1, message = "Id number should be greater than 0") long id){
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @PostMapping
    public ResponseEntity<RoomResponseDto> createRoom(@RequestBody @Valid RoomRequestDto requestDto){
        return ResponseEntity.ok(roomService.createRoom(requestDto));
    }

}
