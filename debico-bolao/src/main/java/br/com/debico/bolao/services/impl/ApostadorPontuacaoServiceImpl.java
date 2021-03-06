package br.com.debico.bolao.services.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;

import br.com.debico.bolao.ApostadorJaInscritoException;
import br.com.debico.bolao.dao.ApostadorPontuacaoDAO;
import br.com.debico.bolao.services.ApostadorPontuacaoService;
import br.com.debico.core.helpers.CacheKeys;
import br.com.debico.model.AbstractApostadorPontuacao;
import br.com.debico.model.Apostador;
import br.com.debico.model.ApostadorPontuacao;
import br.com.debico.model.campeonato.Campeonato;
import br.com.debico.model.campeonato.CampeonatoImpl;

@Named
@Transactional(readOnly = false)
class ApostadorPontuacaoServiceImpl implements ApostadorPontuacaoService {

    protected static final Logger LOGGER = LoggerFactory
	    .getLogger(ApostadorPontuacaoServiceImpl.class);

    @Inject
    private ApostadorPontuacaoDAO apostadorPontuacaoDAO;

    @Inject
    @Named("resourceBundleMessageSource")
    private MessageSource messageSource;

    public ApostadorPontuacaoServiceImpl() {

    }

    public void inscreverApostadorCampeonato(final Apostador apostador,
	    final CampeonatoImpl campeonato)
	    throws ApostadorJaInscritoException {
	checkNotNull(apostador);
	checkNotNull(campeonato);

	if (apostadorPontuacaoDAO.selecionarApostador(apostador, campeonato) == null) {
	    ApostadorPontuacao apostadorPontuacao = new ApostadorPontuacao(
		    apostador, campeonato);
	    apostadorPontuacaoDAO.create(apostadorPontuacao);
	    LOGGER.debug(
		    "[inscreverApostadorCampeonato] Apostador {} inscrito no campeonato {} com sucesso!",
		    apostador, campeonato);
	} else {
	    throw new ApostadorJaInscritoException(messageSource, apostador,
		    campeonato);
	}
    }

    public void inscreverApostadorCampeonatoSileciosamente(Apostador apostador,
	    CampeonatoImpl campeonato) {
	LOGGER.debug(
		"[inscreverApostadorCampeonatoSileciosamente] Tentando inscrever o apostador {}.",
		apostador);

	try {
	    this.inscreverApostadorCampeonato(apostador, campeonato);
	} catch (ApostadorJaInscritoException e) {
	    LOGGER.trace(
		    "[inscreverApostadorCampeonatoSileciosamente] Apostador ja inscrito. Ignorando.",
		    e);
	}
    }

    @Cacheable(value = CacheKeys.RANKING_APOSTADORES, key = "{#idCampeonato, #idLiga}")
    public List<ApostadorPontuacao> listarRankingPorLiga(int idCampeonato,
	    long idLiga) {
	LOGGER.debug(
		"[listarRankingPorLiga] Realizando a consulta do Ranking para a liga {}",
		idLiga);
	final List<ApostadorPontuacao> pontuacao = apostadorPontuacaoDAO
		.selecionarApostadoresPorLiga(idCampeonato, idLiga);

	Collections.sort(pontuacao);

	LOGGER.debug(
		"[listarRankingPorLiga] Consulta da liga {} realizada com sucesso.",
		idLiga);

	return pontuacao;
    }

    @Cacheable(value = CacheKeys.RANKING_APOSTADORES, key = "{#idRodada, 'R'}")
    public List<AbstractApostadorPontuacao> listarRankingPorRodada(int idRodada) {
	final List<AbstractApostadorPontuacao> pontuacao = apostadorPontuacaoDAO
		.selecionarApostadoresPorRodada(idRodada);

	Collections.sort(pontuacao);

	return pontuacao;
    }

    @Cacheable(value = CacheKeys.RANKING_APOSTADORES, key = "{#idRodada, #idLiga, 'RL'}")
    public List<AbstractApostadorPontuacao> listarRankingPorRodadaELiga(
	    int idRodada, long idLiga) {
	final List<AbstractApostadorPontuacao> pontuacao = apostadorPontuacaoDAO
		.selecionarApostadoresPorRodadaELiga(idRodada, idLiga);

	Collections.sort(pontuacao);

	return pontuacao;
    }

    @Cacheable(value = CacheKeys.RANKING_APOSTADORES)
    public List<ApostadorPontuacao> listarRanking(Campeonato campeonato) {
	final List<ApostadorPontuacao> pontuacao = apostadorPontuacaoDAO
		.selecionarApostadores(campeonato);

	Collections.sort(pontuacao);

	return pontuacao;
    }

    @Override
    public void removerPontuacaoRodada(int idRodada) {
	apostadorPontuacaoDAO.excluirApostadorPontuacaoRodada(idRodada);
    }

}
