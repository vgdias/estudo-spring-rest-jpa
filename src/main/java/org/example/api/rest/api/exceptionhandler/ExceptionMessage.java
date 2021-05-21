package org.example.api.rest.api.exceptionhandler;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExceptionMessage {

	private LocalDateTime dataHora;
	private String mensagem;
}
