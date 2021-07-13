package org.example.api.rest.api.controller;

import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.port;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import java.math.BigDecimal;

import org.example.api.rest.domain.model.Cidade;
import org.example.api.rest.domain.model.Cozinha;
import org.example.api.rest.domain.model.Endereco;
import org.example.api.rest.domain.model.Estado;
import org.example.api.rest.domain.model.Restaurante;
import org.example.api.rest.domain.service.CidadeService;
import org.example.api.rest.domain.service.CozinhaService;
import org.example.api.rest.domain.service.EstadoService;
import org.example.api.rest.domain.service.RestauranteService;
import org.example.api.rest.util.DatabaseCleaner;
import org.example.api.rest.util.ResourceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = 
SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
public class CozinhaControllerIT {

	private static final long ID_COZINHA_EXISTENTE = 1L;
	private static final long ID_COZINHA_EM_USO = 4L;
	private static final long ID_COZINHA_INEXISTENTE = 100L;
	private static final long ID_COZINHA_INVALIDO = 0L;
	private static final String CAMINHO_RAIZ = "/cozinhas";
	private static final String CAMINHO_INVALIDO = "/caminhoInvalido";
	private static final String PARAMETRO_ID_COZINHA = "cozinhaId";
	private static final String PARAMETRO_NOME_COZINHA_REQUEST = "nome";
	private static final String PARAMETRO_NOME_COZINHA_RESPONSE = "nomeCozinha";
	private static final String PARAMETRO_INVALIDO = "invalido";
	private static final String NOME_COZINHA_INEXISTENTE = "inexistente";
	private static final String NOME_SEMELHANTE_COZINHA = "l";
	private static final Integer NUMERO_COZINHAS_COM_NOME_SEMELHANTE = 2;


	private Cozinha cozinhaFrancesa;
	private int quantidadeCozinhasCadastradas;

	@LocalServerPort
	private int localServerPort;

	@Autowired
	private DatabaseCleaner databaseCleaner;

	@Autowired
	private CozinhaService cadastroCozinhaService;

	@Autowired
	private RestauranteService cadastroRestauranteService;

	@Autowired
	private EstadoService cadastroEstadoService;

	@Autowired
	private CidadeService cadastroCidadeService;

	@BeforeEach
	public void setup() {
		enableLoggingOfRequestAndResponseIfValidationFails();
		port = localServerPort;

		databaseCleaner.clearTables();
		loadData();
	}

	/*===================================
	 * Testes do metodo de servico listar()
	 *===================================*/
	@Test
	public void deveRetornarEstado200_quandoConsultarCozinhas() {
		given()
		.accept(ContentType.JSON)
		.when()
		.get(CAMINHO_RAIZ)
		.then()
		.contentType(ContentType.JSON)
		.statusCode(HttpStatus.OK.value());
	}

	@Test
	public void deveRetornarEstado404_quandoConsultarCaminhoInvalido() {
		given()
		.accept(ContentType.JSON)
		.when()
		.get(CAMINHO_INVALIDO)
		.then()
		.contentType(ContentType.JSON)
		.statusCode(HttpStatus.NOT_FOUND.value());
	}

	@Test
	public void deveRetornarTotalDeCozinhas_quandoConsultarCozinhas() {
		given()
		.accept(ContentType.JSON)
		.when()
		.get(CAMINHO_RAIZ)
		.then()
		.contentType(ContentType.JSON)
		.body("", hasSize(quantidadeCozinhasCadastradas));
	}

	/*===================================
	 * Testes do metodo de servico buscar()
	 *===================================*/
	@Test
	public void deveRetornarRespostaEEstadoCorretos_quandoConsultarCozinhaExistente() {
		given()
		.pathParam(PARAMETRO_ID_COZINHA, cozinhaFrancesa.getId())
		.accept(ContentType.JSON)
		.when()
		.get(CAMINHO_RAIZ + "/{" + PARAMETRO_ID_COZINHA + "}")
		.then()
		.contentType(ContentType.JSON)
		.statusCode(HttpStatus.OK.value())
		.body("nomeCozinha", equalTo(cozinhaFrancesa.getNome()));
	}

	@Test
	public void deveRetornarEstado404_quandoConsultarCozinhaInexistente() {
		given()
		.pathParam(PARAMETRO_ID_COZINHA, ID_COZINHA_INEXISTENTE)
		.accept(ContentType.JSON)
		.when()
		.get(CAMINHO_RAIZ + "/{" + PARAMETRO_ID_COZINHA + "}")
		.then()
		.contentType(ContentType.JSON)
		.statusCode(HttpStatus.NOT_FOUND.value());
	}

	@Test
	public void deveRetornarEstado400_quandoConsultarCozinhaCom_idInvalido() {
		given()
		.pathParam(PARAMETRO_ID_COZINHA, ID_COZINHA_INVALIDO)
		.accept(ContentType.JSON)
		.when()
		.get(CAMINHO_RAIZ + "/{" + PARAMETRO_ID_COZINHA + "}")
		.then()
		.contentType(ContentType.JSON)
		.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	/*===================================
	 * Testes do metodo de servico adicionar()
	 *===================================*/
	@Test
	public void deveRetornarEstado201_quandoCadastrarCozinhaCom_nome() {
		String jsonCozinhaCom_nome = 
				ResourceUtils.getContentFromResource (
						"/json/cozinha/cozinha-com-nome.json");

		given()
		.body(jsonCozinhaCom_nome)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.post(CAMINHO_RAIZ)
		.then()
		.contentType(ContentType.JSON)
		.statusCode(HttpStatus.CREATED.value());
	}

	@Test
	public void deveRetornarEstado400_quandoCadastrarCozinhaSem_nome() {
		String jsonCozinhaSem_nome = 
				ResourceUtils.getContentFromResource (
						"/json/cozinha/cozinha-sem-nome.json");

		given()
		.body(jsonCozinhaSem_nome)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.post(CAMINHO_RAIZ)
		.then()
		.contentType(ContentType.JSON)
		.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	public void deveRetornarEstado400_quandoCadastrarCozinhaCom_id_nome() {
		String jsonCozinhaCom_id_nome = 
				ResourceUtils.getContentFromResource (
						"/json/cozinha/cozinha-com-id-nome.json");

		given()
		.body(jsonCozinhaCom_id_nome)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.post(CAMINHO_RAIZ)
		.then()
		.contentType(ContentType.JSON)
		.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	public void deveRetornarEstado400_quandoCadastrarCozinhaCom_nomeVazio() {
		String jsonCozinhaCom_nomeVazio = 
				ResourceUtils.getContentFromResource (
						"/json/cozinha/cozinha-com-nome_vazio.json");

		given()
		.body(jsonCozinhaCom_nomeVazio)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.post(CAMINHO_RAIZ)
		.then()
		.contentType(ContentType.JSON)
		.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	public void deveRetornarEstado400_quandoCadastrarCozinhaComParametroInexistente() {
		String jsonCozinhaComParametroInexistente = 
				ResourceUtils.getContentFromResource (
						"/json/cozinha/cozinha-com-parametro_inexistente.json");

		given()
		.body(jsonCozinhaComParametroInexistente)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.post(CAMINHO_RAIZ)
		.then()
		.contentType(ContentType.JSON)
		.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	public void deveRetornarEstado400_quandoCadastrarCozinhaComSintaxeInvalida() {
		String jsonCozinhaComSintaxeInvalida = 
				ResourceUtils.getContentFromResource (
						"/json/cozinha/cozinha-com-sintaxe_invalida.json");

		given()
		.body(jsonCozinhaComSintaxeInvalida)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.post(CAMINHO_RAIZ)
		.then()
		.contentType(ContentType.JSON)
		.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	/*===================================
	 * Testes do metodo de servico alterar()
	 *===================================*/
	@Test
	public void deveRetornarEstado200_quandoAlterarCozinhaCom_nome() {
		String jsonCozinhaCom_nome = 
				ResourceUtils.getContentFromResource (
						"/json/cozinha/cozinha-com-nome.json");

		given()
		.pathParam(PARAMETRO_ID_COZINHA, ID_COZINHA_EXISTENTE)
		.body(jsonCozinhaCom_nome)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.put(CAMINHO_RAIZ + "/{" + PARAMETRO_ID_COZINHA + "}")
		.then()
		.contentType(ContentType.JSON)
		.statusCode(HttpStatus.OK.value());
	}

	@Test
	public void deveRetornarEstado400_quandoAlterarCozinhaSem_nome() {
		String jsonCozinhaCom_nome = 
				ResourceUtils.getContentFromResource (
						"/json/cozinha/cozinha-sem-nome.json");

		given()
		.pathParam(PARAMETRO_ID_COZINHA, ID_COZINHA_EXISTENTE)
		.body(jsonCozinhaCom_nome)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.put(CAMINHO_RAIZ + "/{" + PARAMETRO_ID_COZINHA + "}")
		.then()
		.contentType(ContentType.JSON)
		.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	public void deveRetornarEstado400_quandoAlterarCozinhaCom_id_nome() {
		String jsonCozinhaCom_id_nome = 
				ResourceUtils.getContentFromResource (
						"/json/cozinha/cozinha-com-id-nome.json");

		given()
		.pathParam(PARAMETRO_ID_COZINHA, ID_COZINHA_EXISTENTE)
		.body(jsonCozinhaCom_id_nome)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.put(CAMINHO_RAIZ + "/{" + PARAMETRO_ID_COZINHA + "}")
		.then()
		.contentType(ContentType.JSON)
		.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	public void deveRetornarEstado400_quandoAlterarCozinhaCom_nomeVazio() {
		String jsonCozinhaCom_nomeVazio = 
				ResourceUtils.getContentFromResource (
						"/json/cozinha/cozinha-com-nome_vazio.json");

		given()
		.pathParam(PARAMETRO_ID_COZINHA, ID_COZINHA_EXISTENTE)
		.body(jsonCozinhaCom_nomeVazio)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.put(CAMINHO_RAIZ + "/{" + PARAMETRO_ID_COZINHA + "}")
		.then()
		.contentType(ContentType.JSON)
		.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	public void deveRetornarEstado400_quandoAlterarCozinhaComParametroInexistente() {
		String jsonCozinhaComParametroInexistente = 
				ResourceUtils.getContentFromResource (
						"/json/cozinha/cozinha-com-parametro_inexistente.json");

		given()
		.pathParam(PARAMETRO_ID_COZINHA, ID_COZINHA_EXISTENTE)
		.body(jsonCozinhaComParametroInexistente)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.put(CAMINHO_RAIZ + "/{" + PARAMETRO_ID_COZINHA + "}")
		.then()
		.contentType(ContentType.JSON)
		.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	public void deveRetornarEstado400_quandoAlterarCozinhaComSintaxeInvalida() {
		String jsonCozinhaComSintaxeInvalida = 
				ResourceUtils.getContentFromResource (
						"/json/cozinha/cozinha-com-sintaxe_invalida.json");

		given()
		.pathParam(PARAMETRO_ID_COZINHA, ID_COZINHA_EXISTENTE)
		.body(jsonCozinhaComSintaxeInvalida)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.put(CAMINHO_RAIZ + "/{" + PARAMETRO_ID_COZINHA + "}")
		.then()
		.contentType(ContentType.JSON)
		.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	/*===================================
	 * Testes do metodo de servico remover()
	 *===================================*/
	@Test
	public void deveRetornarEstado204_quandoRemoverCozinhaExistente() {
		given()
		.pathParam(PARAMETRO_ID_COZINHA, ID_COZINHA_EXISTENTE)
		.when()
		.delete(CAMINHO_RAIZ + "/{" + PARAMETRO_ID_COZINHA + "}")
		.then()
		.statusCode(HttpStatus.NO_CONTENT.value());
	}

	@Test
	public void deveRetornarEstado404_quandoRemoverCozinhaInexistente() {
		given()
		.pathParam(PARAMETRO_ID_COZINHA, ID_COZINHA_INEXISTENTE)
		.when()
		.delete(CAMINHO_RAIZ + "/{" + PARAMETRO_ID_COZINHA + "}")
		.then()
		.statusCode(HttpStatus.NOT_FOUND.value());
	}

	@Test
	public void deveRetornarEstado400_quandoRemoverCozinhaCom_id() {
		given()
		.pathParam(PARAMETRO_ID_COZINHA, ID_COZINHA_EM_USO)
		.when()
		.delete(CAMINHO_RAIZ + "/{" + PARAMETRO_ID_COZINHA + "}")
		.then()
		.statusCode(HttpStatus.CONFLICT.value());
	}

	@Test
	public void deveRetornarEstado400_quandoRemoverCozinhaCom_idInvalido() {
		given()
		.pathParam(PARAMETRO_ID_COZINHA, ID_COZINHA_INVALIDO)
		.when()
		.delete(CAMINHO_RAIZ + "/{" + PARAMETRO_ID_COZINHA + "}")
		.then()
		.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	/*===================================
	 * Testes do metodo de servico porNome()
	 *===================================*/
	@Test
	public void deveRetornarRespostaEEstadoCorretos_quandoConsultarCozinhaCom_nome() {
		given()
		.queryParam(PARAMETRO_NOME_COZINHA_REQUEST, cozinhaFrancesa.getNome())
		.accept(ContentType.JSON)
		.when()
		.get(CAMINHO_RAIZ  + "/por-nome")
		.then()
		.contentType(ContentType.JSON)
		.statusCode(HttpStatus.OK.value())
		.body(PARAMETRO_NOME_COZINHA_RESPONSE, equalTo(cozinhaFrancesa.getNome()));
	}

	@Test
	public void deveRetornar404_quandoConsultarCozinhaCom_nomeInexistente() {
		given()
		.queryParam(PARAMETRO_NOME_COZINHA_REQUEST, NOME_COZINHA_INEXISTENTE)
		.accept(ContentType.JSON)
		.when()
		.get(CAMINHO_RAIZ  + "/por-nome")
		.then()
		.contentType(ContentType.JSON)
		.statusCode(HttpStatus.NOT_FOUND.value());
	}

	@Test
	public void deveRetornarEstado400_quandoConsultarCozinhaComParametroInvalido() {
		given()
		.queryParam(PARAMETRO_INVALIDO, cozinhaFrancesa.getNome())
		.accept(ContentType.JSON)
		.when()
		.get(CAMINHO_RAIZ  + "/por-nome")
		.then()
		.contentType(ContentType.JSON)
		.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	/*===================================
	 * Testes do metodo de servico porNomeSemelhante()
	 *===================================*/
	@Test
	public void deveRetornarTotalDeCozinhas_quandoConsultarCozinhaCom_nomeSemelhante() {
		given()
		.queryParam(PARAMETRO_NOME_COZINHA_REQUEST, NOME_SEMELHANTE_COZINHA)
		.accept(ContentType.JSON)
		.when()
		.get(CAMINHO_RAIZ  + "/com-nome-semelhante")
		.then()
		.contentType(ContentType.JSON)
		.statusCode(HttpStatus.OK.value())
		.body("", hasSize(NUMERO_COZINHAS_COM_NOME_SEMELHANTE));
	}

	/**
	 * Carrega dados para os testes
	 */
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
		cozinhaFrancesa = new Cozinha();
		cozinhaFrancesa.setNome("Francesa");
		cadastroCozinhaService.adicionar(cozinhaFrancesa);

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
		restaurante.setNome("Pane e Vino");
		restaurante.setTaxaFrete(new BigDecimal(0.0));
		restaurante.setCozinha(cozinhaFrancesa);
		restaurante.setEndereco(endereco);

		cadastroRestauranteService.adicionar(restaurante);

		quantidadeCozinhasCadastradas = (int) cadastroCozinhaService.count();
	}
}
