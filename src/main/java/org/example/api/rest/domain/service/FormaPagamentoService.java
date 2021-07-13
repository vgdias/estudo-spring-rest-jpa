package org.example.api.rest.domain.service;

import static org.example.api.rest.api.exceptionhandler.ErrorMessage.FORMA_PAGAMENTO_POR_ID_EM_USO;
import static org.example.api.rest.api.exceptionhandler.ErrorMessage.FORMA_PAGAMENTO_POR_ID_NAO_ENCONTRADA;

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
public class FormaPagamentoService {

	@Autowired
	private FormaPagamentoRepository formaPagamentoRepository;

	public List<FormaPagamento> listar() {
		return formaPagamentoRepository.findAll();
	}

	public FormaPagamento buscarPorId(Long formaPagamentoId) {
		return buscarFormaPagamentoPorId(formaPagamentoId);
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
					String.format(
							FORMA_PAGAMENTO_POR_ID_NAO_ENCONTRADA.toString(), 
							formaPagamentoId));
		} catch (DataIntegrityViolationException e) {
			throw new RecursoEmUsoException(
					String.format(
							FORMA_PAGAMENTO_POR_ID_EM_USO.toString(), 
							formaPagamentoId));
		}
	}

	public FormaPagamento buscarFormaPagamentoPorId(Long id) {
		return formaPagamentoRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(
								FORMA_PAGAMENTO_POR_ID_NAO_ENCONTRADA.toString(), 
								id)));
	}
}