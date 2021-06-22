package org.example.api.rest.domain.service;

import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.port;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import javax.validation.ConstraintViolationException;

import org.example.api.rest.domain.exception.RecursoEmUsoException;
import org.example.api.rest.domain.exception.RecursoNaoEncontradoException;
import org.example.api.rest.domain.model.Cidade;
import org.example.api.rest.domain.model.Cozinha;
import org.example.api.rest.domain.model.Endereco;
import org.example.api.rest.domain.model.Estado;
import org.example.api.rest.domain.model.Restaurante;
import org.example.api.rest.util.DatabaseCleaner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = 
SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
public class CadastroCozinhaServiceIT {

	private static final long COZINHA_EM_USO = 1L;
	private static final long ID_COZINHA_INEXISTENTE = 100L;
	private static final String NOME_COZINHA_INEXISTENTE = 
			"cozinha inexistente";

	@LocalServerPort
	private int localServerPort;

	@Autowired
	private DatabaseCleaner databaseCleaner;

	@Autowired
	private CadastroCozinhaService cadastroCozinhaService;

	@Autowired
	private CadastroRestauranteService cadastroRestauranteService;

	@Autowired
	private CadastroEstadoService cadastroEstadoService;

	@Autowired
	private CadastroCidadeService cadastroCidadeService;

	@BeforeEach
	public void setup() {
		enableLoggingOfRequestAndResponseIfValidationFails();
		port = localServerPort;

		databaseCleaner.clearTables();
		loadData();
	}

	@Test
	void deveAtribuirId_quandoCadastrarCozinhaChinesaComNome() {
		Cozinha novaCozinha = new Cozinha();
		novaCozinha.setNome("Chinesa");
		cadastroCozinhaService.adicionar(novaCozinha);

		assertThat(novaCozinha).isNotNull();
		assertThat(novaCozinha.getId()).isNotNull();
	}

	@Test
	public void deveFalhar_quandoCadastrarCozinhaSemNome() {
		Cozinha novaCozinha = new Cozinha();
		novaCozinha.setNome(null);
		Assertions.assertThrows(
				ConstraintViolationException.class,
				() -> cadastroCozinhaService.adicionar(novaCozinha));
	}

	@Test
	public void deveFalhar_quandoExcluirCozinhaEmUso() {
		Assertions.assertThrows(
				RecursoEmUsoException.class, 
				() -> cadastroCozinhaService.remover(COZINHA_EM_USO));
	}

	@Test
	public void deveFalhar_quandoExcluirCozinhaInexistente() {
		Assertions.assertThrows(
				RecursoNaoEncontradoException.class , 
				() -> cadastroCozinhaService.remover(ID_COZINHA_INEXISTENTE));
	}

	@Test
	public void deveFalhar_quandoBuscarPorNomeDeCozinhaInexistente() {
		Assertions.assertThrows(
				RecursoNaoEncontradoException.class , 
				() -> cadastroCozinhaService.porNome(
						NOME_COZINHA_INEXISTENTE));
	}

	@Test
	public void deveFalhar_quandoBuscarPorIdDeCozinhaInexistente() {
		Assertions.assertThrows(
				RecursoNaoEncontradoException.class , 
				() -> cadastroCozinhaService.obterCozinha(
						ID_COZINHA_INEXISTENTE));
	}

	private void loadData() {
		Cozinha cozinhaIndiana = new Cozinha();
		cozinhaIndiana.setNome("Indiana");
		cadastroCozinhaService.adicionar(cozinhaIndiana);
		Cozinha cozinhaTailandesa = new Cozinha();
		cozinhaTailandesa.setNome("Tailandesa");
		cadastroCozinhaService.adicionar(cozinhaTailandesa);
		Cozinha cozinhaAlema = new Cozinha();
		cozinhaAlema.setNome("Alemã");
		cadastroCozinhaService.adicionar(cozinhaAlema);

		Estado estado = new Estado();
		estado.setNome("Rio de Janeiro");
		cadastroEstadoService.adicionar(estado);

		Cidade cidade = new Cidade();
		cidade.setNome("Rio de Janeiro");
		cidade.setEstado(estado);
		cadastroCidadeService.adicionar(cidade);

		Endereco endereco = new Endereco();
		endereco.setBairro("Tijuca");
		endereco.setCep("20500-400");
		endereco.setLogradouro("Rua Santo Afonso");
		endereco.setNumero("20");
		endereco.setCidade(cidade);

		Restaurante restaurante = new Restaurante();
		restaurante.setNome("Restaurante de teste 1");
		restaurante.setTaxaFrete(new BigDecimal(0.0));
		restaurante.setCozinha(cozinhaIndiana);
		restaurante.setEndereco(endereco);

		cadastroRestauranteService.adicionar(restaurante);
	}

}
