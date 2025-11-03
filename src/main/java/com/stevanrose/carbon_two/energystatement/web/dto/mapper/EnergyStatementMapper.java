package com.stevanrose.carbon_two.energystatement.web.dto.mapper;

import com.stevanrose.carbon_two.energystatement.domain.EnergyStatement;
import com.stevanrose.carbon_two.energystatement.web.dto.EnergyStatementRequest;
import com.stevanrose.carbon_two.energystatement.web.dto.EnergyStatementResponse;
import com.stevanrose.carbon_two.office.domain.Office;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnergyStatementMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "office", source = "office")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  EnergyStatement toEntity(EnergyStatementRequest request, Office office);

  @Mapping(target = "officeId", source = "office.id")
  EnergyStatementResponse toResponse(EnergyStatement entity);
}
