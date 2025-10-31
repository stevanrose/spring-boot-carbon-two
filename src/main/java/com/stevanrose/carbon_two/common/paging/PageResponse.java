package com.stevanrose.carbon_two.common.paging;

import java.util.List;
import org.springframework.data.domain.Page;

public record PageResponse<T>(
    List<T> content, int page, int size, long totalElements, int totalPages) {

  public static <T> PageResponse<T> of(Page<T> p) {
    return new PageResponse<>(
        p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
  }
}
