package br.com.debico.campeonato.services.impl;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Strings.emptyToNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Charsets;

import br.com.debico.campeonato.services.ExploraWebResultadosJogosService;
import br.com.debico.campeonato.services.RodadaService;
import br.com.debico.campeonato.util.PlacarUtils;
import br.com.debico.campeonato.util.RodadaUtils;
import br.com.debico.campeonato.util.TimeUtils;
import br.com.debico.core.helpers.DefaultLocale;
import br.com.debico.model.PartidaBase;
import br.com.debico.model.PartidaRodada;
import br.com.debico.model.StatusPartida;
import br.com.debico.model.Time;
import br.com.debico.model.campeonato.Campeonato;
import br.com.debico.model.campeonato.Rodada;

/**
 * Efetua a busca no site http://www.tabeladobrasileirao.net/ pelos resultados
 * do Campeonato Brasileiro.
 * 
 * @author Ricardo Zanini (ricardozanini@gmail.com)
 * @since 2.0.5
 */
@Named
@Transactional(readOnly = true)
class ExploraTabelaBrasileiraoResultadosJogosService implements ExploraWebResultadosJogosService<PartidaRodada> {

	private URL siteURL;
	private static final String PROTOCOL_HTTP = "http";
	private static final DateTimeFormatter FMT = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm")
			.withLocale(DefaultLocale.LOCALE);
	private static final AdicionaPartida ADICIONA_PARTIDA_INTEGRIDADE = new AdicionaPartidaComIntegridade();
	private static final AdicionaPartida ADICIONA_PARTIDA_PLACAR = new AdicionaPartidaComPlacar();
	/**
	 * Hora padrão caso não encontre no site.
	 */
	private static final String HORA_PADRAO_JOGO = "16:00";
	/*
	 * Índices de busca na linha da tabela do HTML
	 */
	private static final int IDX_RODADA = 0;
	private static final int IDX_DATA = 1;
	private static final int IDX_HORA = 3;
	private static final int IDX_MANDANTE = 4;
	private static final int IDX_GOLS_MANDANTE = 5;
	private static final int IDX_GOLS_VISITANTE = 7;
	private static final int IDX_VISITANTE = 8;
	private static final int IDX_LOCAL1 = 9;

	@SuppressWarnings("unused")
	private static final int IDX_LOCAL2 = 10;

	@Inject
	private RodadaService rodadaService;

	public ExploraTabelaBrasileiraoResultadosJogosService() {

	}

	@Override
	public void setPesquisaURL(URL siteURL) {
		this.siteURL = siteURL;
	}

	private boolean isProtocolPesquisaURLHTTP() {
		return this.siteURL.getProtocol().contains(PROTOCOL_HTTP);
	}

	/**
	 * Formata a data de acordo com os dados da tabela HTML do Site.
	 * 
	 * @param data
	 * @param hora
	 * @param ano
	 * @return
	 */
	private Date formatarData(String data, String hora, int ano) {
		data = firstNonNull(data, "").trim();
		hora = firstNonNull(hora, "").trim();
		if (emptyToNull(data) == null) {
			return null;
		}
		if (emptyToNull(hora) == null) {
			hora = HORA_PADRAO_JOGO;
		}
		return DateTime.parse(String.format("%s/%s %s", data, ano, hora), FMT).toDate();
	}

	private List<PartidaRodada> doRecuperarPartidas(Campeonato campeonato, AdicionaPartida callback) {
		final List<PartidaRodada> partidas = new ArrayList<>();
		final Set<Time> times = campeonato.getTimes();
		final List<Rodada> rodadas = rodadaService.selecionarRodadasNaoCalculadas(campeonato);
		final int anoAtual = DateTime.now().year().get();

		try {
			Document doc = null;
			if (isProtocolPesquisaURLHTTP()) {
				doc = Jsoup.connect(this.siteURL.getPath()).get();
			} else {
				doc = Jsoup.parse(this.siteURL.openStream(), Charsets.UTF_8.name(), "");
			}

			final Element tabelaResultados = doc.select("table#jogos").first();
			final Elements linhaJogos = tabelaResultados.select("tr");
			for (Element jogo : linhaJogos) {
				if (!jogo.hasClass("titulo")) {
					PartidaRodada partida = new PartidaRodada();
					partida.setMandante(TimeUtils.procuraTime(jogo.child(IDX_MANDANTE).text(), times));
					partida.setVisitante(TimeUtils.procuraTime(jogo.child(IDX_VISITANTE).text(), times));
					partida.setRodada(RodadaUtils.procuraRodadaPorOrdem(jogo.child(IDX_RODADA).ownText(), rodadas));
					partida.setStatus(StatusPartida.ND);
					partida.setComputadaCampeonato(false);
					partida.setLocal(jogo.child(IDX_LOCAL1).text());
					partida.setPlacar(PlacarUtils.novoPlacarOuNull(jogo.child(IDX_GOLS_MANDANTE).text(),
							jogo.child(IDX_GOLS_VISITANTE).text()));
					partida.setDataHoraPartida(
							formatarData(jogo.child(IDX_DATA).text(), jogo.child(IDX_HORA).text(), anoAtual));
					callback.adicionarPartida(partidas, partida);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return partidas;
	}

	@Override
	public List<PartidaRodada> recuperarPartidas(Campeonato campeonato) {
		return this.doRecuperarPartidas(campeonato, ADICIONA_PARTIDA_INTEGRIDADE);
	}

	@Override
	public List<PartidaRodada> recuperarPartidasFinalizadas(Campeonato campeonato) {
		// finalizada se possui placar, certo?
		return this.doRecuperarPartidas(campeonato, ADICIONA_PARTIDA_PLACAR);
	}

	// ----------- Inner
	private static abstract class AdicionaPartida {
		abstract void adicionarPartida(Collection<PartidaRodada> partidas, PartidaRodada partida);

		protected boolean verificarIntegridadePartida(final PartidaBase partida) {
			return partida.getVisitante() != null && partida.getMandante() != null
					&& partida.getDataHoraPartida() != null;
		}
	}

	private static final class AdicionaPartidaComIntegridade extends AdicionaPartida {
		@Override
		public void adicionarPartida(Collection<PartidaRodada> partidas, PartidaRodada partida) {
			if (verificarIntegridadePartida(partida)) {
				partidas.add(partida);
			}
		}
	}

	private static final class AdicionaPartidaComPlacar extends AdicionaPartida {
		@Override
		public void adicionarPartida(Collection<PartidaRodada> partidas, PartidaRodada partida) {
			if (verificarIntegridadePartida(partida) && partida.getPlacar() != null) {
				partidas.add(partida);
			}
		}
	}

}
