package br.com.debico.social;

import br.com.debico.model.Apostador;

public interface ApostadorService {

	void atualizarApostador(final Apostador apostador)
			throws CadastroApostadorException;

	/**
	 * Recupera um apostador a partir do seu email.
	 * 
	 * @param email
	 * @return instância de {@link Apostador} ou <code>null</code> caso não
	 *         encontrado.
	 * @since 1.1.0
	 */
	Apostador selecionarApostadorPorEmail(String email);

	/**
	 * Recupera o perfil completo do apostador a partir do seu email.
	 * <p/>
	 * Do contrário de {@link #selecionarApostadorPorEmail(String)}, trás toda a
	 * informação sobre o {@link Apostador}.
	 * <p/>
	 * Utilizar com parcimônia.
	 * 
	 * @param email
	 * @return instância de {@link Apostador} ou <code>null</code> caso não
	 *         encontrado.
	 * @since 1.2.0
	 */
	Apostador selecionarPerfilApostadorPorEmail(String email);

}
