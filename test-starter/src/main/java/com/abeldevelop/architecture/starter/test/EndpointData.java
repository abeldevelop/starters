package com.abeldevelop.architecture.starter.test;

import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class EndpointData<T> {

	private HttpMethod method;
	private String endpoint;
	Object content;
	private Map<String, String> params;
	private Map<String, String> headers;
	private HttpStatus expectedStatus;
	private Class<T> typeReturn;
}
