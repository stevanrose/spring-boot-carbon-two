package com.stevanrose.carbon_two.office.service;

import com.stevanrose.carbon_two.office.domain.Office;
import com.stevanrose.carbon_two.office.repository.OfficeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OfficeService {

    private final OfficeRepository officeRepository;

    @Transactional(readOnly = true)
    public Page<Office> list(Pageable pageable) {
        return officeRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Office findById(UUID id) {
        return officeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Office not found with id: " + id));
    }

    @Transactional
    public Office create(Office office) {
        return officeRepository.save(office);
    }

}
