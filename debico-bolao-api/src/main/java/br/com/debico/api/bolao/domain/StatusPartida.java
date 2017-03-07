package br.com.debico.api.bolao.domain;

import java.io.Serializable;

/**
 * Status da Partida.
 * 
 * @author r_fernandes
 *
 */
public enum StatusPartida implements Serializable {
    
    /**
     * Vencedor Mandante.
     */
    VM,
    /**
     * Vencedor Visitante.
     */
    VV,
    /**
     * Empate.
     */
    EM,
    /**
     * Nao definida.
     */
    ND;

}
