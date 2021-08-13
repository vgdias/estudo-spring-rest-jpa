package org.example.api.rest.api.exceptionhandler;

public enum ErrorMessage {
	RESTAURANTE_POR_ID_NAO_ENCONTRADO("Restaurante de id [%d] não encontrado"),
	RESTAURANTE_POR_NOME_ENCONTRADO("Restaurante [%s] já existe"),
	RESTAURANTE_POR_ID_EM_USO("Restaurante de id [%d] em uso"),
	RESTAURANTE_FORMA_PAGAMENTO_POR_ID_NAO_ENCONTRADA("Restaurante não possui forma de pagamento de id [%d]"),
	RESTAURANTE_FORMA_PAGAMENTO_POR_ID_ENCONTRADA("Restaurante já possui forma de pagamento de id [%d]"),
	RESTAURANTE_PRODUTO_POR_ID_NAO_ENCONTRADO("Restaurante não possui produto de id [%d]"), 
	RESTAURANTE_PRODUTO_POR_NOME_ENCONTRADO("Restaurante já possui produto [%s]"),

	FORMA_PAGAMENTO_POR_ID_NAO_ENCONTRADA("Forma de pagamento de id [%d] não encontrada"),
	FORMA_PAGAMENTO_POR_ID_EM_USO("Forma de pagamento de id [%d] em uso"),
	FORMA_PAGAMENTO_POR_NOME_ENCONTRADA("Forma de pagamento [%s] já existe"),

	CIDADE_POR_ID_NAO_ENCONTRADA("Cidade de id [%d] não encontrada"),
	CIDADE_POR_ID_EM_USO("Cidade de id [%d] em uso"),
	CIDADE_POR_NOME_ENCONTRADA("Cidade [%s] já existe"),

	COZINHA_POR_ID_NAO_ENCONTRADA("Cozinha de id [%d] não encontrada"), 
	COZINHA_POR_ID_EM_USO("Cozinha de id [%d] em uso"),
	COZINHA_POR_NOME_NAO_ENCONTRADA("Cozinha de nome [%s] não encontrada"),
	COZINHA_POR_NOME_ENCONTRADA("Cozinha [%s] já existe"),

	ESTADO_POR_ID_EM_USO("Estado de id [%d] em uso"),
	ESTADO_POR_ID_NAO_ENCONTRADO("Estado de id [%d] não encontrado"),
	ESTADO_POR_NOME_ENCONTRADO("Estado [%s] já existe"),

	GRUPO_ENCONTRADO_POR_NOME("Grupo [%s] já existe"),
	GRUPO_POR_ID_NAO_ENCONTRADO("Grupo de id [%d] não encontrado"),

	PERMISSAO_POR_ID_NAO_ENCONTRADA("Permissão de id [%d] não encontrado"),
	PERMISSAO_POR_ID_EM_USO("Permissão de id [%d] em uso"),

	GRUPO_PERMISSAO_POR_ID_NAO_ENCONTRADA("Grupo não possui permissão de id [%d]"),
	GRUPO_PERMISSAO_POR_ID_ENCONTRADA("Grupo já possui permissão de id [%d]"),

	USUARIO_POR_ID_NAO_ENCONTRADO("Usuário de id [%d] não encontrado"),

	PRODUTO_POR_ID_NAO_ENCONTRADO("Produto de id [%d] não encontrado"),

	PEDIDO_POR_ID_NAO_ENCONTRADO("Pedido de id [%s] não encontrado") 
	;

	private final String text;
	ErrorMessage(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}