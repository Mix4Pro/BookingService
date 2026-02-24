package booking.mapper;

import booking.dto.request.RoomRequestDto;
import booking.dto.response.roomResponse.RoomResponseDto;
import booking.entity.RoomEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    RoomEntity toEntityFromRequest(RoomRequestDto roomRequestDto);

    RoomResponseDto toResponseFromEntity(RoomEntity roomEntity);

    List<RoomResponseDto> toListOfResponseFromListOfEntities (List<RoomEntity> roomEntities);
}
