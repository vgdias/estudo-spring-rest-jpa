package org.example.api.rest.cozinha;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.ConstraintViolationException;

import org.example.api.rest.domain.exception.RecursoEmUsoException;
import org.example.api.rest.domain.exception.RecursoNaoEncontradoException;
import org.example.api.rest.domain.model.Cozinha;
import org.example.api.rest.domain.service.CadastroCozinhaService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = 
SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
public class CadastroCozinhaIT {

	private static final long COZINHA_EM_USO = 1L;
	private static final long COZINHA_INEXISTENTE = 100L;

	@Autowired
	private CadastroCozinhaService service;

	@Test
	void deveAtribuirId_quandoCadastrarCozinhaChinesaComNome() {
		Cozinha novaCozinha = new Cozinha();
		novaCozinha.setNome("Chinesa");
		service.adicionar(novaCozinha);

		assertThat(novaCozinha).isNotNull();
		assertThat(novaCozinha.getId()).isNotNull();
	}

	@Test
	public void deveFalhar_quandoCadastrarCozinhaSemNome() {
		Cozinha novaCozinha = new Cozinha();
		novaCozinha.setNome(null);
		Assertions.assertThrows(
				ConstraintViolationException.class,
				() -> service.adicionar(novaCozinha));
	}

	@Test
	public void deveFalhar_quandoExcluirCozinhaEmUso() {
		Assertions.assertThrows(
				RecursoEmUsoException.class, 
				() -> service.remover(COZINHA_EM_USO));
	}

	@Test
	public void deveFalhar_quandoExcluirCozinhaInexistente() {
		Assertions.assertThrows(
				RecursoNaoEncontradoException.class , 
				() -> service.remover(COZINHA_INEXISTENTE));
	}
}
