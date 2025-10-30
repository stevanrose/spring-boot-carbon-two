package com.stevanrose.carbon_two.office.controller;

import com.stevanrose.carbon_two.office.repository.OfficeRepository;
import com.stevanrose.carbon_two.office.web.dto.OfficeRequest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OfficeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OfficeRepository officeRepository;

    @BeforeEach
    void setUp() {
        officeRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    void shouldCreateOffice() {


        String reqJson = """
                    {
                      "code": "LON-01",
                      "name": "London HQ",
                      "address": "10 Downing Street",
                      "gridRegionCode": "GB-LDN",
                      "floorAreaM2": 300.0
                    }
                """;

        mockMvc.perform(post("/api/offices")
                .contentType("application/json")
                .content(reqJson))
                .andExpect(status().isCreated());

    }
}