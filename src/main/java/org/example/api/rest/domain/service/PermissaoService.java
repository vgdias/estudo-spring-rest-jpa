package org.example.api.rest.domain.service;

import static org.example.api.rest.api.exceptionhandler.ErrorMessage.PERMISSAO_POR_ID_EM_USO;
import static org.example.api.rest.api.exceptionhandler.ErrorMessage.PERMISSAO_POR_ID_NAO_ENCONTRADA;

import java.util.List;

import org.example.api.rest.domain.exception.RecursoEmUsoException;
import org.example.api.rest.domain.exception.RecursoNaoEncontradoException;
import org.example.api.rest.domain.model.Permissao;
import org.example.api.rest.domain.repository.PermissaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissaoService {

	@Autowired
	private PermissaoRepository permissaoRepository;

	public List<Permissao> listar() {
		return permissaoRepository.findAll();
	}

	public Permissao buscarPorId(Long permissaoId) {
		return buscarPermissaoPorId(permissaoId);
	} 

	@Transactional
	public Permissao adicionar(Permissao permissao) {
		return permissaoRepository.save(permissao);
	}

	@Transactional
	public Permissao alterar(Permissao permissaoNova) {
		return permissaoRepository.save(permissaoNova);
	}

	@Transactional
	public void remover(Long permissaoId) {
		try {
			permissaoRepository.deleteById(permissaoId);
			permissaoRepository.flush();

		} catch (EmptyResultDataAccessException e) {
			throw new RecursoNaoEncontradoException(
					String.format(
							PERMISSAO_POR_ID_NAO_ENCONTRADA.toString(), 
							permissaoId));
		} catch (DataIntegrityViolationException e) {
			throw new RecursoEmUsoException(
					String.format(
							PERMISSAO_POR_ID_EM_USO.toString(), 
							permissaoId));
		}
	}

	public Permissao buscarPermissaoPorId(Long id) {
		return permissaoRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(
								PERMISSAO_POR_ID_NAO_ENCONTRADA.toString(), 
								id)));
	}
}