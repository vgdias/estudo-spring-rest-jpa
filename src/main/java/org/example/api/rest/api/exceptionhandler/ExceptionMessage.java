package org.example.api.rest.api.exceptionhandler;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(Include.NON_NULL)
public class ExceptionMessage {

	private Integer status;
	private String title;
	private String detail;
	private List<Error> errors;

	@Getter
	@Builder
	public static class Error {
		private String source;
		private String rule;
	}
}
