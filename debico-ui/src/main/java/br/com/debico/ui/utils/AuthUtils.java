package br.com.debico.ui.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.debico.core.spring.security.UserDetailsImpl;

/**
 * Utilidade para tratar dos cenários de autenticação
 * 
 * @author ricardozanini
 *
 */
public final class AuthUtils {

	private AuthUtils() {

	}

	/**
	 * Retorna o usuário logado. Nunca nulo. Mesmo se não estiver no contexto,
	 * uma string em branco é retornada.
	 * 
	 * @return
	 */
	public static String usuarioAutenticado() {
		final Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();

		if (auth == null) {
			return "";
		}

		return auth.getName();
	}

	public static int userId() {
		final Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();

		if (auth == null) {
			return 0;
		}
		
		return ((UserDetailsImpl)auth.getPrincipal()).getId();
	}

}