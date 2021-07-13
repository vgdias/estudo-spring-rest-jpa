package org.example.api.rest.domain.service;

import static org.example.api.rest.api.exceptionhandler.ErrorMessage.GRUPO_ENCONTRADO_POR_NOME;
import static org.example.api.rest.api.exceptionhandler.ErrorMessage.GRUPO_POR_ID_NAO_ENCONTRADO;

import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.constraints.Positive;

import org.example.api.rest.domain.exception.RecursoEmUsoException;
import org.example.api.rest.domain.exception.RecursoNaoEncontradoException;
import org.example.api.rest.domain.model.Grupo;
import org.example.api.rest.domain.repository.GrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class GrupoService {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private GrupoRepository grupoRepository;

	public List<Grupo> listar() {
		return grupoRepository.findAll();
	}

	public Grupo buscarPorId(@Positive Long grupoId) {
		return obterGrupoPorId(grupoId);
	}

	@Transactional
	public Grupo adicionar(Grupo grupo) {
		verificarSeGrupoExiste(grupo.getNome());
		return grupoRepository.save(grupo);
	}

	@Transactional
	public Grupo alterar(Grupo grupo) {
		entityManager.detach(grupo);
		verificarSeGrupoExiste(grupo.getNome());
		return grupoRepository.save(grupo);
	}

	@Transactional
	public void remover(Long grupoId) {
		try {
			grupoRepository.deleteById(grupoId);
			grupoRepository.flush();

		} catch (EmptyResultDataAccessException e) {
			throw new RecursoNaoEncontradoException(
					String.format(
							GRUPO_POR_ID_NAO_ENCONTRADO.toString(), 
							grupoId));
		}
	}

	private void verificarSeGrupoExiste(String nome) {
		grupoRepository.findByNome(nome)
		.ifPresent((grupoEncontrado) -> { 
			throw new RecursoEmUsoException(
					String.format(
							GRUPO_ENCONTRADO_POR_NOME.toString(), 
							grupoEncontrado.getNome())
					);
		});
	}

	private Grupo obterGrupoPorId(Long id) {
		return grupoRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(
								GRUPO_POR_ID_NAO_ENCONTRADO.toString(), 
								id)));
	}

}