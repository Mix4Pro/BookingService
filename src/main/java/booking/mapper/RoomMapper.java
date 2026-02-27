package booking.mapper;

import booking.dto.response.roomResponse.RoomResponseDto;
import booking.entity.RoomEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    @Mapping(target = "hotelId", source = "hotel.id")
    RoomResponseDto toResponseFromEntity(RoomEntity roomEntity);

    List<RoomResponseDto> toListOfResponseFromListOfEntities(List<RoomEntity> roomEntities);
}
