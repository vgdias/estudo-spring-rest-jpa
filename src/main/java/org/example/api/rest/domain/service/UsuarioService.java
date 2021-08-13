package org.example.api.rest.domain.service;

import static org.example.api.rest.api.exceptionhandler.ErrorMessage.USUARIO_POR_ID_NAO_ENCONTRADO;

import java.util.List;

import javax.transaction.Transactional;

import org.example.api.rest.domain.exception.RecursoNaoEncontradoException;
import org.example.api.rest.domain.model.Grupo;
import org.example.api.rest.domain.model.Usuario;
import org.example.api.rest.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

	@Autowired
	GrupoService grupoService;

	@Autowired
	UsuarioRepository usuarioRepository;

	public List<Usuario> listar() {
		return usuarioRepository.findAll();
	}

	@Transactional
	public void excluirGrupo(Long usuarioId, Long grupoId) {
		Usuario usuario = buscarUsuarioPorId(usuarioId);
		Grupo grupo = grupoService.buscarPorId(grupoId);

		usuario.removerGrupo(grupo);
	}

	@Transactional
	public void incluirGrupo(Long usuarioId, Long grupoId) {
		Usuario usuario = buscarUsuarioPorId(usuarioId);
		Grupo grupo = grupoService.buscarPorId(grupoId);

		usuario.adicionarGrupo(grupo);
	}

	public Usuario buscarUsuarioPorId(Long id) {
		return usuarioRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(
								USUARIO_POR_ID_NAO_ENCONTRADO.toString(), 
								id)));
	}
}