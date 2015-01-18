package br.com.debico.social;

import br.com.debico.model.Apostador;



/**
 * Implementação dos Casos de Uso relacionados ao Usuário do Bolão.
 * 
 * @author ricardozanini
 * @since 1.0.0
 */
public interface UsuarioService {

	/**
	 * Realiza o cadastro do Usuario.
	 * 
	 * @param email o email do usuário (único e identificador)
	 * @param senha em formato aberto, conforme cadastro.
	 */
	void cadastrarApostadorUsuario(final Apostador apostador, final String confirmacaoSenha) throws CadastroApostadorException;
	
}
