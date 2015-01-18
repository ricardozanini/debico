package br.com.debico.model.campeonato;

import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.google.common.base.Objects;

@Entity
@DiscriminatorValue(Ranking.RANKING_GRUPO)
public class Grupo extends Ranking implements Comparable<Grupo> {

	private static final long serialVersionUID = 6351437481076599225L;

	@Column(name = "DC_RANKING", nullable = true, length = 255)
	private String nome;

	public Grupo() {
		this.nome = "";
	}
	
	public Grupo(final FaseGrupos fase){
	    super(fase);
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	@Override
	public void setFase(FaseImpl fase) {
	    checkArgument(fase instanceof FaseGrupos, "Apenas fase de grupos sao suportadas em ranking do tipo Grupo.");
	    
	    super.setFase(fase);
	}

	@Override
	public int hashCode() {
		return super.hashCode() + Objects.hashCode(this.nome);
	}

	@Override
	public String toString() {
		return toStringHelper(this).addValue(this.getId()).addValue(this.nome)
				.toString();
	}

	public int compareTo(Grupo o) {
		return o.getNome().compareTo(this.nome);
	}
}
