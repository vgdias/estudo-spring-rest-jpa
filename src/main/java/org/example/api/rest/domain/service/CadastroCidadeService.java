package org.example.api.rest.domain.service;

import java.util.List;
import java.util.Map;

import org.example.api.rest.domain.exception.EntidadeEmUsoException;
import org.example.api.rest.domain.exception.EntidadeNaoEncontradaException;
import org.example.api.rest.domain.model.Cidade;
import org.example.api.rest.domain.model.Estado;
import org.example.api.rest.domain.repository.CidadeRepository;
import org.example.api.rest.domain.repository.EstadoRepository;
import org.example.api.rest.shared.mapper.GenericMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroCidadeService {

	private static final String MSG_CIDADE_EM_USO = "Cidade de codigo %d em uso e nao pode ser removida";
	private static final String MSG_CIDADE_NAO_ENCONTRADA = "Cidade de codigo %d nao encontrada";

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
				.orElseThrow(() -> new EntidadeNaoEncontradaException(
						String.format("Estado de codigo %d nao encontrado", cidade.getEstado().getId())));

		cidade.setEstado(estado);
		return cidadeRepository.save(cidade);
	}

	@Transactional
	public Cidade alterar(Map<String, Object> propriedadesCidadeNova, Long cidadeAtualId) {
		Cidade cidadeAtual = cidadeRepository.findById(cidadeAtualId)
				.orElseThrow(() -> new EntidadeNaoEncontradaException(
						String.format(MSG_CIDADE_NAO_ENCONTRADA, cidadeAtualId)));

		GenericMapper.map(propriedadesCidadeNova, cidadeAtual, Cidade.class);
		Long estadoAtualId = cidadeAtual.getEstado().getId();

		Estado estadoAtual = estadoRepository.findById(estadoAtualId)
				.orElseThrow(() -> new EntidadeNaoEncontradaException(
						String.format("Estado de codigo %d nao encontrado", estadoAtualId)));

		cidadeAtual.setEstado(estadoAtual);
		return cidadeRepository.save(cidadeAtual);
	}

	@Transactional
	public void remover(Long cidadeId) {
		Cidade cidade = cidadeRepository.findById(cidadeId)
				.orElseThrow(() -> new EntidadeNaoEncontradaException(
						String.format(MSG_CIDADE_NAO_ENCONTRADA, cidadeId)));

		try {
			cidadeRepository.delete(cidade);
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
					String.format(MSG_CIDADE_EM_USO, cidadeId));
		}
	}
}