package com.stevanrose.carbon_two.office.web.dto.mapper;

import com.stevanrose.carbon_two.office.domain.Office;
import com.stevanrose.carbon_two.office.web.dto.OfficeRequest;
import com.stevanrose.carbon_two.office.web.dto.OfficeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OfficeMapper {

    Office toEntity(OfficeRequest request);

    OfficeResponse toResponse(Office office);
//    void update(@MappingTarget Office office, OfficeRequest request);

}
