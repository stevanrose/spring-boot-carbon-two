package com.stevanrose.carbon_two.officeenergystatement.web.dto.mapper;

import com.stevanrose.carbon_two.office.domain.Office;
import com.stevanrose.carbon_two.officeenergystatement.domain.OfficeEnergyStatement;
import com.stevanrose.carbon_two.officeenergystatement.web.dto.OfficeEnergyStatementRequest;
import com.stevanrose.carbon_two.officeenergystatement.web.dto.OfficeEnergyStatementResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OfficeEnergyStatementMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "office", source = "office")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  OfficeEnergyStatement toEntity(OfficeEnergyStatementRequest request, Office office);

  @Mapping(target = "officeId", source = "office.id")
  OfficeEnergyStatementResponse toResponse(OfficeEnergyStatement entity);
}
