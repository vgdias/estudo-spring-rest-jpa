package org.example.api.rest.api.controller;

import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.port;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import org.example.api.rest.domain.model.Cozinha;
import org.example.api.rest.domain.service.CadastroCozinhaService;
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
	private static final long ID_COZINHA_INEXISTENTE = 100L;
	private static final long ID_COZINHA_INVALIDO = 0L;
	private static final String CAMINHO_RAIZ = "/cozinhas";
	private static final String CAMINHO_INVALIDO = "/caminhoInvalido";
	private static final String PARAMETRO_ID_COZINHA = "cozinhaId";

	private Cozinha cozinhaFrancesa;
	private int quantidadeCozinhasCadastradas;

	@LocalServerPort
	private int localServerPort;

	@Autowired
	private DatabaseCleaner databaseCleaner;

	@Autowired
	CadastroCozinhaService service;

	@BeforeEach
	public void setup() {
		enableLoggingOfRequestAndResponseIfValidationFails();
		port = localServerPort;

		databaseCleaner.clearTables();
		loadData();
	}

	/*=============================
	 * Testes do metodo de servico listar()
	 *=============================*/
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

	/*=============================
	 * Testes do metodo de servico buscar()
	 *=============================*/
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
	public void deveRetornarEstado400_quandoConsultarIdInvalido() {
		given()
		.pathParam(PARAMETRO_ID_COZINHA, ID_COZINHA_INVALIDO)
		.accept(ContentType.JSON)
		.when()
		.get(CAMINHO_RAIZ + "/{" + PARAMETRO_ID_COZINHA + "}")
		.then()
		.contentType(ContentType.JSON)
		.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	/*=============================
	 * Testes do metodo de servico adicionar()
	 *=============================*/
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
	
	/*=============================
	 * Testes do metodo de servico alterar()
	 *=============================*/
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
	
	/**
	 * Carrega dados para os testes
	 */
	private void loadData() {
		Cozinha cozinhaIndiana = new Cozinha();
		cozinhaIndiana.setNome("Indiana");
		service.adicionar(cozinhaIndiana);
		Cozinha cozinhaTailandesa = new Cozinha();
		cozinhaTailandesa.setNome("Tailandesa");
		service.adicionar(cozinhaTailandesa);
		Cozinha cozinhaAlema = new Cozinha();
		cozinhaAlema.setNome("Alemã");
		service.adicionar(cozinhaAlema);
		cozinhaFrancesa = new Cozinha();
		cozinhaFrancesa.setNome("Francesa");
		service.adicionar(cozinhaFrancesa);

		quantidadeCozinhasCadastradas = (int) service.count();
	}
}
