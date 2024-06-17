package org.example.deferreddemo.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.AsyncEvent;
import org.example.deferreddemo.controllers.exception.DemoExceptionHandler;
import org.example.deferreddemo.controllers.exception.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockAsyncContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(
        classes = {
                DemoExceptionHandler.class,
                DemoController.class,
        })
@WebMvcTest(
        controllers = DemoController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
class DemoControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Test
  void getResult() throws Exception {
    final ErrorResponse expectedResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred.");
    final String json = (new ObjectMapper()).writeValueAsString(expectedResponse);

    final MvcResult mvcResult = mockMvc.perform(get("/")).andExpect(request().asyncStarted()).andReturn();
    final MockAsyncContext context = (MockAsyncContext) mvcResult.getRequest().getAsyncContext();
    assertThat(context).isNotNull();

    for (var listener : context.getListeners()) {
      listener.onError(new AsyncEvent(context, new Exception("An error occurred.")));
    }

    mockMvc.perform(asyncDispatch(mvcResult))
            .andExpectAll(
                    status().isInternalServerError(),
                    content().json(json)
            );
  }

}
