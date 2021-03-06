package br.com.debico.campeonato.services;

import java.net.URL;
import java.util.List;

import br.com.debico.model.PartidaBase;

/**
 * Serviço para recuperar o histórico de partidas de um determinado Campeonato
 * e/ou Rodada. Efetua a requisição para páginas da Web com o placar já definido
 * e as transforma em partidas válidas do nosso domínio.
 * 
 * @author Ricardo Zanini (ricardozanini@gmail.com)
 * @since 2.0.5
 */
public interface ExploraWebResultadosJogosService<P extends PartidaBase> {

	/**
	 * Recupera as partidas já finalizadas (com placar) de determinado
	 * campeonato.
	 * 
	 * @param campeonatoId
	 *            Identificador do campeonato. Feito dessa forma para que o
	 *            serviço busque os dados desejados do campeonato.
	 * @return
	 */
	List<P> recuperarPartidasFinalizadas(int campeonatoId, URL siteURL) throws ExploraWebResultadosException;

	/**
	 * Recupera todas as partidas do campeonato da fonte Web.
	 * 
	 * @param campeonatoId Identificador do campeonato.
	 * @return
	 */
	List<P> recuperarPartidas(int campeonatoId, URL siteURL) throws ExploraWebResultadosException;
}
