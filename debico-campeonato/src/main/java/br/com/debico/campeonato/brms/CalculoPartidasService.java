package br.com.debico.campeonato.brms;

import java.util.List;

import br.com.debico.model.Partida;
import br.com.debico.model.PartidaBase;
import br.com.debico.model.PartidaRodada;
import br.com.debico.model.StatusPartida;
import br.com.debico.model.campeonato.AbstractRodada;
import br.com.debico.model.campeonato.Campeonato;


/**
 * Lida com o processamento das regras para definir os resultados das partidas.
 * <p />
 * Deve ser executada dentro de um contexto já transacionado.
 * 
 * @author r_fernandes
 * @since 0.1
 */
public interface CalculoPartidasService {
    
    /**
     * Atualiza o status das {@link Partida}s ainda não definidas ({@link StatusPartida#ND}), mas com placar.
     * 
     * @param rodada desejada.
     * @return partidas processadas.
     */
    List<PartidaRodada> definirStatusPartidas(AbstractRodada rodada);
    
    /**
     * Atualiza a pontuação dos times de acordo com as partidas realizadas.
     * 
     * @param partidas partidas com placar e status já definidos.
     * @see CalculoPartidasService#definirStatusPartidas()
     */
    void calcularPontuacaoTimes(Campeonato campeonato, List<? extends PartidaBase> partidas);

}
