package com.stevanrose.carbon_two.office.web.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "OfficePageResponse", description = "Paginated response for Office entities")
public class OfficePageResponse {
    @ArraySchema(schema = @Schema(implementation = OfficeResponse.class))
    private java.util.List<OfficeResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
