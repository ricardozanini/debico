package br.com.debico.model.campeonato;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.google.common.base.Objects;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;

@Entity
@Table(name = "tb_campeonato_param")
public class ParametrizacaoCampeonato implements Serializable {

	private static final long serialVersionUID = 8974054005868047552L;

	@Id
	@Column(name = "ID_CAMPEONATO")
	private int idCampeonato;

	@Column(name = "NU_POS_LIM_INFERIOR")
	private int posicaoLimiteInferior;

	@Column(name = "NU_POS_LIM_ELITE")
	private int posicaoLimiteElite;
	
	@Column(name = "DC_URL_FETCH_JOGOS", nullable = true, length = 2048)
	private String siteURLFetchJogos;

	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	private CampeonatoImpl campeonato;

	public ParametrizacaoCampeonato() {

	}

	/**
	 * Cria uma nova estrutura de parametrização onde o primeiro colocado é o
	 * campeão e o último, o "rebaixado".
	 * 
	 * @param idCampeonato
	 * @return
	 */
	public static ParametrizacaoCampeonato parametrizacaoPrimeiroUltimo(
			final int idCampeonato) {
		ParametrizacaoCampeonato parametrizacaoCampeonato = new ParametrizacaoCampeonato();
		parametrizacaoCampeonato.setPosicaoLimiteElite(1);
		parametrizacaoCampeonato.setPosicaoLimiteInferior(1);
		parametrizacaoCampeonato.setIdCampeonato(idCampeonato);

		return parametrizacaoCampeonato;
	}

	public int getIdCampeonato() {
		return idCampeonato;
	}

	public void setIdCampeonato(int idCampeonato) {
		this.idCampeonato = idCampeonato;
	}

	/**
	 * A partir de que posição (inclusive) o time será rebaixado para outra
	 * Série? Pode ser utilizado para determinar o que vai ser feito com times
	 * nessas condições nas classes de {@link Ranking}: FaseUnica, Grupos, etc.
	 * <p/>
	 * Ex: 17. Nesse caso a partir da posicao 17 até o final, são considerados
	 * inferiores.
	 * 
	 * @return
	 */
	public final int getPosicaoLimiteInferior() {
		return posicaoLimiteInferior;
	}

	/**
	 * @see #getPosicaoLimiteInferior()
	 * @param posicaoLimiteInferior
	 */
	public final void setPosicaoLimiteInferior(int posicaoLimiteInferior) {
		this.posicaoLimiteInferior = posicaoLimiteInferior;
	}

	/**
	 * Qual a posição limite para o time, em um cameponato de pontos corridos,
	 * ser considerado de elite?
	 * <p/>
	 * Um time classificado na 'elite' pode ser referenciado, no BR, como
	 * classificado para a Libertadores. Em campeonatos europeus, para a
	 * Champions League.
	 * <p/>
	 * Pode ser utilizado para determinar o que vai ser feito com times nessas
	 * condições nas classes de {@link Ranking}: FaseUnica, Grupos, etc.
	 * <p/>
	 * Ex: 4. Nesse caso até o 4 colocado é considerado elite.
	 * 
	 * @return
	 */
	public final int getPosicaoLimiteElite() {
		return posicaoLimiteElite;
	}

	/**
	 * @see #getPosicaoLimiteElite()
	 * @param posicaoLimiteElite
	 */
	public final void setPosicaoLimiteElite(int posicaoLimiteElite) {
		this.posicaoLimiteElite = posicaoLimiteElite;
	}
	
	/**
	 * URL utilizada para recuperar os jogos de um recurso Web externo.
	 * 
	 * @return
	 */
	public String getSiteURLFetchJogos() {
	    return siteURLFetchJogos;
	}
	
	/**
	 * @see #getSiteURLFetchJogos()
	 * @param siteURLFetchJogos
	 */
	public void setSiteURLFetchJogos(String siteURLFetchJogos) {
	    this.siteURLFetchJogos = siteURLFetchJogos;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.idCampeonato);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj.getClass() != getClass()) {
			return false;
		}

		ParametrizacaoCampeonato that = (ParametrizacaoCampeonato) obj;

		return equal(this.idCampeonato, that.getIdCampeonato());
	}

	@Override
	public String toString() {
		return toStringHelper(this)
				.addValue(this.getIdCampeonato())
				.add("Limite Posicao Elite", this.getPosicaoLimiteElite())
				.add("Limite Posicao Inferior", this.getPosicaoLimiteInferior())
				.toString();
	}

}
