package org.example.api.rest.domain.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

import org.example.api.rest.domain.exception.DependenciaNaoEncontradaException;
import org.example.api.rest.domain.exception.EntidadeEmUsoException;
import org.example.api.rest.domain.exception.EntidadeNaoEncontradaException;
import org.example.api.rest.domain.model.Cidade;
import org.example.api.rest.domain.model.Estado;
import org.example.api.rest.domain.repository.CidadeRepository;
import org.example.api.rest.domain.repository.EstadoRepository;
import org.example.api.rest.shared.mapping.GenericMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroCidadeService {

	private static final String MSG_ESTADO_NAO_ENCONTRADO = "Estado de id %d nao encontrado";
	private static final String MSG_CIDADE_EM_USO = "Cidade de id %d em uso e nao pode ser removida";
	private static final String MSG_CIDADE_NAO_ENCONTRADA = "Cidade de id %d nao encontrada";

	@Autowired
	private CidadeRepository cidadeRepository;

	@Autowired
	private EstadoRepository estadoRepository;

	public List<Cidade> listar() {
		return cidadeRepository.findAll();
	}

	public Cidade buscar(Long cidadeId) {
		return cidadeRepository.findById(cidadeId)
				.orElseThrow(() -> new EntidadeNaoEncontradaException(
						String.format(MSG_CIDADE_NAO_ENCONTRADA, cidadeId)));
	} 

	@Transactional
	public Cidade adicionar(Cidade cidade) {
		Estado estado = estadoRepository.findById(cidade.getEstado().getId()) 
				.orElseThrow(() -> new DependenciaNaoEncontradaException(
						String.format(MSG_ESTADO_NAO_ENCONTRADO, cidade.getEstado().getId())));

		cidade.setEstado(estado);
		return cidadeRepository.save(cidade);
	}

	@Transactional
	public Cidade alterar(Map<String, Object> propriedadesCidadeNova, Long cidadeAtualId, 
			HttpServletRequest request) {

		if (propriedadesCidadeNova.isEmpty()) {
			throw new ValidationException("Nenhuma propriedade foi fornecida");
		}
		if (propriedadesCidadeNova.containsKey("id")) {
			throw new ValidationException("A propriedade 'cidade.id' nao pode ser alterada");
		}

		Cidade cidadeAtual = cidadeRepository.findById(cidadeAtualId)
				.orElseThrow(() -> new EntidadeNaoEncontradaException(
						String.format(MSG_CIDADE_NAO_ENCONTRADA, cidadeAtualId)));

		GenericMapper.map(propriedadesCidadeNova, cidadeAtual, Cidade.class, request);

		if ( (Objects.nonNull(cidadeAtual.getEstado()) ) 
				&& (Objects.nonNull(cidadeAtual.getEstado().getId()))) {

			Long estadoAtualId = cidadeAtual.getEstado().getId();

			Estado estadoAtual = estadoRepository.findById(estadoAtualId)
					.orElseThrow(() -> new DependenciaNaoEncontradaException(
							String.format(MSG_ESTADO_NAO_ENCONTRADO, estadoAtualId)));

			cidadeAtual.setEstado(estadoAtual);
			
			if (cidadeAtual.getNome().trim().isEmpty()) {
				throw new ValidationException("A propriedade 'nome' nao pode ser vazia");
			}
			return cidadeRepository.save(cidadeAtual);
		} else {
			throw new DependenciaNaoEncontradaException(
					String.format(MSG_ESTADO_NAO_ENCONTRADO, cidadeAtual.getEstado().getId()));
		}
	}

	//	@Transactional
	public void remover(Long cidadeId) {
		try {
			cidadeRepository.deleteById(cidadeId);

		} catch (EmptyResultDataAccessException e) {
			throw new EntidadeNaoEncontradaException(
					String.format(MSG_CIDADE_NAO_ENCONTRADA, cidadeId));
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
					String.format(MSG_CIDADE_EM_USO, cidadeId));
		}
	}
}