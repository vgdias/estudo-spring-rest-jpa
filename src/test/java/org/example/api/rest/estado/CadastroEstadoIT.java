package org.example.api.rest.estado;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.ConstraintViolationException;

import org.example.api.rest.domain.exception.RecursoEmUsoException;
import org.example.api.rest.domain.exception.RecursoNaoEncontradoException;
import org.example.api.rest.domain.model.Estado;
import org.example.api.rest.domain.service.CadastroEstadoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = 
SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
public class CadastroEstadoIT {

	private static final long ESTADO_EM_USO = 1L;
	private static final long ESTADO_INEXISTENTE = 100L;

	@Autowired
	private CadastroEstadoService service;

	@Test
	void deveAtribuirId_quandoCadastrarEstadoAmapaComNome() {
		Estado novoEstado = new Estado();
		novoEstado.setNome("Amapa");
		service.adicionar(novoEstado);

		assertThat(novoEstado).isNotNull();
		assertThat(novoEstado.getId()).isNotNull();
	}

	@Test
	public void deveFalhar_quandoCadastrarEstadoSemNome() {
		Estado novoEstado = new Estado();
		novoEstado.setNome(null);
		Assertions.assertThrows(
				ConstraintViolationException.class,
				() -> service.adicionar(novoEstado));
	}

	@Test
	public void deveFalhar_quandoExcluirEstadoEmUso() {
		Assertions.assertThrows(
				RecursoEmUsoException.class, 
				() -> service.remover(ESTADO_EM_USO));
	}

	@Test
	public void deveFalhar_quandoExcluirEstadoInexistente() {
		Assertions.assertThrows(
				RecursoNaoEncontradoException.class , 
				() -> service.remover(ESTADO_INEXISTENTE));
	}
}
