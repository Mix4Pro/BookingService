package booking.service.impl;

import booking.dto.request.RoomSearchRequestDto;
import booking.dto.response.roomResponse.RoomPageResponseDto;
import booking.dto.response.roomResponse.RoomResponseDto;
import booking.entity.RoomEntity;
import booking.mapper.RoomMapper;
import booking.repository.RoomRepository;
import booking.repository.specification.RoomSpecification;
import booking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    public RoomPageResponseDto getAllRooms(Pageable pageable) {
        Page<RoomEntity> page = roomRepository.findAll(pageable);
        List<RoomResponseDto> pageContent = roomMapper.toListOfResponseFromListOfEntities(page.getContent());

        return new RoomPageResponseDto(
                pageContent,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public RoomPageResponseDto getSortedRooms(Pageable pageable, RoomSearchRequestDto roomSearchRequestDto) {
        Specification<RoomEntity> spec = Specification
                .where(RoomSpecification.byCity(roomSearchRequestDto.cityId()))
                .and(RoomSpecification.byGuests(roomSearchRequestDto.guests()))
                .and(RoomSpecification.byRoomType(roomSearchRequestDto.roomType()))
                .and(RoomSpecification.byMealPlan(roomSearchRequestDto.mealPlan()))
                .and(RoomSpecification.byMinRating(roomSearchRequestDto.minRating()))
                .and(RoomSpecification.isAvailable(roomSearchRequestDto.checkIn(), roomSearchRequestDto.checkOut()));

        Page<RoomEntity> rooms = roomRepository.findAll(spec, pageable);

        List<RoomResponseDto> pageContent = roomMapper.toListOfResponseFromListOfEntities(rooms.getContent());

        return new RoomPageResponseDto(
                pageContent,
                rooms.getNumber(),
                rooms.getSize(),
                rooms.getTotalElements(),
                rooms.getTotalPages()
        );
    }

}
