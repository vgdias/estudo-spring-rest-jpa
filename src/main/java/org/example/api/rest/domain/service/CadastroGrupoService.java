package org.example.api.rest.domain.service;

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
public class CadastroGrupoService {

	private static final String MSG_GRUPO_NAO_ENCONTRADO = "Grupo de id [%d] nao encontrado";
	private static final String MSG_GRUPO_ENCONTRADO_POR_NOME = "Grupo [%s] já existe";

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private GrupoRepository grupoRepository;

	public List<Grupo> listar() {
		return grupoRepository.findAll();
	}

	public Grupo buscar(@Positive Long grupoId) {
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
					String.format(MSG_GRUPO_NAO_ENCONTRADO, grupoId));
		}
	}
	
	private void verificarSeGrupoExiste(String nome) {
		grupoRepository.findByNome(nome)
		.ifPresent((grupoEncontrado) -> { 
			throw new RecursoEmUsoException(
					String.format(MSG_GRUPO_ENCONTRADO_POR_NOME, grupoEncontrado.getNome())
					);
		});
	}

	private Grupo obterGrupoPorId(Long id) {
		return grupoRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(MSG_GRUPO_NAO_ENCONTRADO, id)));
	}
	
}