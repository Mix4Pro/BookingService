package booking.service.roomService.impl;

import booking.dto.request.RoomRequestDto;
import booking.dto.response.roomResponse.RoomPageResponseDto;
import booking.dto.response.roomResponse.RoomResponseDto;
import booking.entity.RoomEntity;
import booking.exception.roomException.RoomNotFoundException;
import booking.exception.roomException.RoomNumberExistsException;
import booking.mapper.RoomMapper;
import booking.repository.RoomRepository;
import booking.service.roomService.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    @Transactional
    public RoomResponseDto createRoom (RoomRequestDto roomRequestDto) {
        try{
            RoomEntity room = roomMapper.toEntityFromRequest(roomRequestDto);
            RoomEntity savedRoom = roomRepository.save(room);
            return roomMapper.toResponseFromEntity(savedRoom);
        }catch (DataIntegrityViolationException e) {
            throw new RoomNumberExistsException("Room number already exists", HttpStatus.CONFLICT);
        }
    }

    public RoomResponseDto getRoomById(long id) {
        RoomEntity room = roomRepository.findById(id)
            .orElseThrow(()-> new RoomNotFoundException("Room not found",HttpStatus.NOT_FOUND));

        return roomMapper.toResponseFromEntity(room);
    }

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
}
