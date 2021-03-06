package br.com.debico.bolao.dao;

import java.util.List;

import br.com.debico.model.AbstractApostadorPontuacao;
import br.com.debico.model.Apostador;
import br.com.debico.model.ApostadorPontuacao;
import br.com.debico.model.campeonato.Campeonato;
import br.com.tecnobiz.spring.config.dao.Dao;

public interface ApostadorPontuacaoDAO extends Dao<ApostadorPontuacao, Apostador> {
    
    List<ApostadorPontuacao> selecionarApostadores(Campeonato campeonato);
    
    /**
     * Retorna os apostadores de determinado campeonato, de determinada liga.
     * 
     * @param idCampeonato identificador do campeonato
     * @param idLiga identificador da liga
     * @return
     */
    List<ApostadorPontuacao> selecionarApostadoresPorLiga(int idCampeonato, long idLiga);
    
    /**
     * Retorna os apostadores de determinada rodada.
     * 
     * @param idRodada
     * @return
     * @since 2.0.3
     */
    List<AbstractApostadorPontuacao> selecionarApostadoresPorRodada(int idRodada);
    
    /**
     * Retorna os apostadores de determinada rodada e liga.
     * 
     * @param idRodada
     * @param idLiga
     * @return
     * @since 2.0.3
     */
    List<AbstractApostadorPontuacao> selecionarApostadoresPorRodadaELiga(int idRodada, long idLiga);
    
    /**
     * Elimina a pontuação dos apostadores de uma rodada específica.
     * @param idRodada
     * @since 2.0.4
     */
    void excluirApostadorPontuacaoRodada(int idRodada);
        
    ApostadorPontuacao selecionarApostador(Apostador apostador, Campeonato campeonato);

}
