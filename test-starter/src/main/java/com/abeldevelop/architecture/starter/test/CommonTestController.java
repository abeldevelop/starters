package com.abeldevelop.architecture.starter.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.abeldevelop.architecture.library.common.dto.exception.ErrorResponseResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CommonTestController {

	@Autowired
	protected MockMvc mockMvc;

	protected ObjectMapper objectMapper;

	public CommonTestController() {
		objectMapper = new ObjectMapper()
				.registerModule(new Jdk8Module())
				.registerModule(new JavaTimeModule());
	}

	protected <T> T makeRestRequest(EndpointData<T> endpointData) throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = null;
		switch(endpointData.getMethod()) {
			case POST:
				mockHttpServletRequestBuilder = post(endpointData.getEndpoint()).content(writeValueAsString(endpointData.getContent()));
				break;
			case PUT:
				mockHttpServletRequestBuilder = put(endpointData.getEndpoint()).content(writeValueAsString(endpointData.getContent()));
				break;
			case DELETE:
				mockHttpServletRequestBuilder = delete(endpointData.getEndpoint());
				break;
			case GET:
				mockHttpServletRequestBuilder = get(endpointData.getEndpoint());
				break;
			default:
				throw new Exception("Method not supported!!");
		}
		addHeadersToRequest(mockHttpServletRequestBuilder, endpointData.getHeaders());
		addParamsToRequest(mockHttpServletRequestBuilder, endpointData.getParams());
		ResultActions resultActions = this.mockMvc.perform(mockHttpServletRequestBuilder);
		checkStatusCode(resultActions, endpointData.getExpectedStatus().value());
		MvcResult mvcResult = resultActions.andReturn();
		mvcResult.getResponse().setCharacterEncoding("UTF-8");
		
		if(Void.class.getCanonicalName().equals(endpointData.getTypeReturn().getCanonicalName())) {
			return null;
		}
		return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), endpointData.getTypeReturn());
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
		if(headers == null || headers.isEmpty()) {
			headers = getDefaultHeaders();
		}
		for(Map.Entry<String, String> entry : headers.entrySet()) {
			mockHttpServletRequestBuilder.header(entry.getKey(), entry.getValue());
		}
	}
	
	protected Map<String, String> getDefaultHeaders() {
		Map<String, String> defaultHeaders = new HashMap<>();
		defaultHeaders.put("Content-Type", "application/json");
		defaultHeaders.put("Accept-Language", "es");
		return defaultHeaders;
	}
	
	protected void assertEqualsErrorResponseResourceMessage(String expected, ErrorResponseResource response) {
		assertEquals(expected, response.getMessage());
	}
}
