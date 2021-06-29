package org.example.api.rest.domain.service;

import java.util.List;

import org.example.api.rest.domain.exception.RecursoEmUsoException;
import org.example.api.rest.domain.exception.RecursoNaoEncontradoException;
import org.example.api.rest.domain.model.FormaPagamento;
import org.example.api.rest.domain.repository.FormaPagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroFormaPagamentoService {

	private static final String MSG_FORMA_PAGAMENTO_NAO_ENCONTRADA = 
			"Forma de pagamento de id [%d] nao encontrada";
	private static final String MSG_FORMA_PAGAMENTO_EM_USO = 
			"Forma de pagamento de id [%d] em uso";

	@Autowired
	private FormaPagamentoRepository formaPagamentoRepository;

	public List<FormaPagamento> listar() {
		return formaPagamentoRepository.findAll();
	}

	public FormaPagamento buscar(Long formaPagamentoId) {
		return obtemFormaPagamento(formaPagamentoId);
	} 

	@Transactional
	public FormaPagamento adicionar(FormaPagamento formaPagamento) {
		return formaPagamentoRepository.save(formaPagamento);
	}

	@Transactional
	public FormaPagamento alterar(FormaPagamento formaPagamentoNova) {
		return formaPagamentoRepository.save(formaPagamentoNova);
	}

	@Transactional
	public void remover(Long formaPagamentoId) {
		try {
			formaPagamentoRepository.deleteById(formaPagamentoId);
			formaPagamentoRepository.flush();

		} catch (EmptyResultDataAccessException e) {
			throw new RecursoNaoEncontradoException(
					String.format(MSG_FORMA_PAGAMENTO_NAO_ENCONTRADA, formaPagamentoId));
		} catch (DataIntegrityViolationException e) {
			throw new RecursoEmUsoException(
					String.format(MSG_FORMA_PAGAMENTO_EM_USO, formaPagamentoId));
		}
	}

	public FormaPagamento obtemFormaPagamento(Long id) {
		return formaPagamentoRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(MSG_FORMA_PAGAMENTO_NAO_ENCONTRADA, id)));
	}
}
