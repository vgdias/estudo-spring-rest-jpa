package org.example.api.rest.domain.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
		Optional<Estado> estadoOpt = estadoRepository.findById(cidade.getEstado().getId());
		if (estadoOpt.isPresent()) {
			cidade.setEstado(estadoOpt.get());
			return cidadeRepository.save(cidade);
		}
		throw new EntidadeNaoEncontradaException(
				String.format("Estado de codigo %d nao encontrado", cidade.getEstado().getId()));
	}

	@Transactional
	public Cidade alterar(Map<String, Object> propriedadesCidadeNova, Long cidadeAtualId) {
		Optional<Cidade> cidadeAtualOpt = cidadeRepository.findById(cidadeAtualId);
		if (cidadeAtualOpt.isPresent()) {
			Cidade cidadeAtual = cidadeAtualOpt.get();
			GenericMapper.map(propriedadesCidadeNova, cidadeAtual, Cidade.class);

			Optional<Estado> estadoAtualOpt = estadoRepository.findById(cidadeAtual.getEstado().getId());
			if (estadoAtualOpt.isPresent()) {
				cidadeAtual.setEstado(estadoAtualOpt.get());
			} else {
				throw new EntidadeNaoEncontradaException(
						String.format("Estado de codigo %d nao encontrado", estadoAtualOpt.get().getId()));
			}
			return cidadeRepository.save(cidadeAtual);
		}
		throw new EntidadeNaoEncontradaException(
				String.format(MSG_CIDADE_NAO_ENCONTRADA, cidadeAtualId));
	}

	@Transactional
	public void remover(Long cidadeId) {
		try {
			Optional<Cidade> cidadeOpt = cidadeRepository.findById(cidadeId);
			if (cidadeOpt.isPresent()) {
				cidadeRepository.delete(cidadeOpt.get());
			} else {
				throw new EntidadeNaoEncontradaException(
						String.format(MSG_CIDADE_NAO_ENCONTRADA, cidadeId));
			}
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
					String.format(MSG_CIDADE_EM_USO, cidadeId));
		}
	}

}