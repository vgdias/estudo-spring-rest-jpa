package org.example.api.rest.domain.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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

	private static final String ESTADO_NAO_ENCONTRADO = "Estado de id %d nao encontrado";
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
						String.format(ESTADO_NAO_ENCONTRADO, cidade.getEstado().getId())));

		cidade.setEstado(estado);
		return cidadeRepository.save(cidade);
	}

	@Transactional
	public Cidade alterar(Map<String, Object> propriedadesCidadeNova, Long cidadeAtualId, HttpServletRequest request) {
		Cidade cidadeAtual = cidadeRepository.findById(cidadeAtualId)
				.orElseThrow(() -> new EntidadeNaoEncontradaException(
						String.format(MSG_CIDADE_NAO_ENCONTRADA, cidadeAtualId)));

		GenericMapper.map(propriedadesCidadeNova, cidadeAtual, Cidade.class, request);
		Long estadoAtualId = cidadeAtual.getEstado().getId();

		Estado estadoAtual = estadoRepository.findById(estadoAtualId)
				.orElseThrow(() -> new DependenciaNaoEncontradaException(
						String.format(ESTADO_NAO_ENCONTRADO, estadoAtualId)));

		cidadeAtual.setEstado(estadoAtual);
		return cidadeRepository.save(cidadeAtual);
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