package com.stevanrose.carbon_two.common.controller.slice;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

public abstract class BaseControllerTest {
  @Autowired protected ObjectMapper objectMapper;
  @Autowired protected MockMvc mvc;

  protected String json(Object value) throws Exception {
    return objectMapper.writeValueAsString(value);
  }

  protected ResultActions putJson(String url, Object body) throws Exception {
    return mvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(json(body)));
  }
}
