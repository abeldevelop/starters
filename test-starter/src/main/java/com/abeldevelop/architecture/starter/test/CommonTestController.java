package com.abeldevelop.architecture.starter.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CommonTestController {

	@Autowired
	protected MockMvc mockMvc;

	protected ObjectMapper objectMapper;

	@BeforeEach
	public void setUp() {
	}
	
	public CommonTestController() {
		objectMapper = new ObjectMapper()
				.registerModule(new Jdk8Module())
				.registerModule(new JavaTimeModule());
	}

	protected <T> T callPostEndpoint(String endpoint, Object content, int expectedStatusCode, Class<T> clazzReturn) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = post(endpoint).content(writeValueAsString(content));
		addHeadersToRequest(mockHttpServletRequestBuilder, null);
		
		ResultActions resultActions = this.mockMvc.perform(mockHttpServletRequestBuilder);
		checkStatusCode(resultActions, expectedStatusCode);
		
		MvcResult mvcResult = resultActions.andReturn();

		return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), clazzReturn);
	}

	protected <T> T callPutEndpoint(String endpoint, Object content, int expectedStatusCode, Class<T> clazzReturn) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = put(endpoint).content(writeValueAsString(content));
		addHeadersToRequest(mockHttpServletRequestBuilder, null);
		
		ResultActions resultActions = this.mockMvc.perform(mockHttpServletRequestBuilder);
		checkStatusCode(resultActions, expectedStatusCode);
		
		MvcResult mvcResult = resultActions.andReturn();

		return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), clazzReturn);
	}

	protected <T> T callDeleteEndpoint(String endpoint, int expectedStatusCode, Class<T> clazzReturn) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = delete(endpoint);
		addHeadersToRequest(mockHttpServletRequestBuilder, null);
		
		ResultActions resultActions = this.mockMvc.perform(mockHttpServletRequestBuilder);
		checkStatusCode(resultActions, expectedStatusCode);
		
		MvcResult mvcResult = resultActions.andReturn();
		
		if (Void.class.getCanonicalName().equals(clazzReturn.getCanonicalName())) {
			return null;
		}
		return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), clazzReturn);
	}

	protected <T> T callGetEndpoint(String endpoint, int expectedStatusCode, Class<T> clazzReturn) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = get(endpoint);
		addHeadersToRequest(mockHttpServletRequestBuilder, null);
		
		ResultActions resultActions = this.mockMvc.perform(mockHttpServletRequestBuilder);
		checkStatusCode(resultActions, expectedStatusCode);
		
		MvcResult mvcResult = resultActions.andReturn();

		return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), clazzReturn);
	}

	protected <T> T callGetEndpoint(String endpoint, int expectedStatusCode, Class<T> clazzReturn, Map<String, String> params) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = get(endpoint);
		addHeadersToRequest(mockHttpServletRequestBuilder, null);
		addParamsToRequest(mockHttpServletRequestBuilder, params);
		
		ResultActions resultActions = this.mockMvc.perform(mockHttpServletRequestBuilder);
		checkStatusCode(resultActions, expectedStatusCode);
		
		MvcResult mvcResult = resultActions.andReturn();
		
		return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), clazzReturn);
	}

	private void checkStatusCode(ResultActions resultActions, int expectedStatusCode) throws Exception {
		resultActions.andExpect(status().is(expectedStatusCode));
	}
	
	private String writeValueAsString(Object content) throws Exception {
		if(content instanceof String) {
			return (String) content;
		}
		return objectMapper.writeValueAsString(content);
	}

	private void addParamsToRequest(MockHttpServletRequestBuilder mockHttpServletRequestBuilder, Map<String, String> params) {
		if(params == null || params.isEmpty()) {
			return;
		}
		for(Map.Entry<String, String> entry : params.entrySet()) {
			mockHttpServletRequestBuilder.param(entry.getKey(), entry.getValue());
		}
	}

	private void addHeadersToRequest(MockHttpServletRequestBuilder mockHttpServletRequestBuilder, Map<String, String> headers) {
		mockHttpServletRequestBuilder.header("Content-Type", "application/json");
		mockHttpServletRequestBuilder.header("Accept-Language", "en");
		if(headers == null || headers.isEmpty()) {
			return;
		}
		for(Map.Entry<String, String> entry : headers.entrySet()) {
			mockHttpServletRequestBuilder.param(entry.getKey(), entry.getValue());
		}
	}
	
}
