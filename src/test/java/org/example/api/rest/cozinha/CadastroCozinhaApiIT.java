package org.example.api.rest.cozinha;

import static io.restassured.RestAssured.basePath;
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
public class CadastroCozinhaApiIT {

	private static final int COZINHA_ID_INEXISTENTE = 100;
	private Cozinha cozinhaFrancesa;
	private String jsonCozinhaChinesaComNome;
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
		basePath = "/cozinhas";
		port = localServerPort;
		jsonCozinhaChinesaComNome = ResourceUtils.getContentFromResource (
				"/json/cozinha/valido/cozinha-chinesa-com-nome.json");

		databaseCleaner.clearTables();
		loadData();
	}
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

	@Test
	public void deveRetornarEstado200_quandoConsultarCozinhas() {
		given()
		.accept(ContentType.JSON)
		.when()
		.get()
		.then()
		.statusCode(HttpStatus.OK.value());
	}

	@Test
	public void deveRetornarTotalDeCozinhas_quandoConsultarCozinhas() {
		given()
		.accept(ContentType.JSON)
		.when()
		.get()
		.then()
		.body("", hasSize(quantidadeCozinhasCadastradas));
	}

	@Test
	public void deveRetornarEstado201_quandoCadastrarCozinha() {
		given()
		.body(jsonCozinhaChinesaComNome)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.post()
		.then()
		.statusCode(HttpStatus.CREATED.value());
	}

	@Test
	public void deveRetornarRespostaEEstadoCorretos_quandoConsultarCozinhaExistente() {
		given()
		.pathParam("cozinhaId", cozinhaFrancesa.getId())
		.accept(ContentType.JSON)
		.when()
		.get("/{cozinhaId}")
		.then()
		.statusCode(HttpStatus.OK.value())
		.body("nomeCozinha", equalTo(cozinhaFrancesa.getNome()));
	}

	@Test
	public void deveRetornarEstado404_quandoConsultarCozinhaInexistente() {
		given()
		.pathParam("cozinhaId", COZINHA_ID_INEXISTENTE)
		.accept(ContentType.JSON)
		.when()
		.get("/{cozinhaId}")
		.then()
		.statusCode(HttpStatus.NOT_FOUND.value());
	}
}
