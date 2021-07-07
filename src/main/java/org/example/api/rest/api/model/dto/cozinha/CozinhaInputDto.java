package org.example.api.rest.api.model.dto.cozinha;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CozinhaInputDto {
	@NotBlank(message = "{notBlank}")
	private String nome;
}