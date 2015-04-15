package br.com.debico.social.services.impl;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import br.com.debico.core.helpers.WebUtils;
import br.com.debico.model.Apostador;
import br.com.debico.social.CadastroLigaException;
import br.com.debico.social.dao.LigaDAO;
import br.com.debico.social.model.Liga;
import br.com.debico.social.services.ApostadorService;
import br.com.debico.social.services.LigaApostadorService;
import br.com.debico.social.services.LigaService;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import static com.google.common.base.Strings.emptyToNull;

@Named
@Transactional(readOnly = false)
class LigaServiceImpl implements LigaService {

	@Inject
	private LigaDAO ligaDAO;

	@Inject
	private LigaApostadorService ligaApostadorService;

	@Inject
	private ApostadorService apostadorService;

	public LigaServiceImpl() {

	}

	@Override
	public List<Liga> consultarLiga(String emailUsuario) {
		checkNotNull(emptyToNull(emailUsuario));

		final Apostador apostador = apostadorService
				.selecionarApostadorPorEmail(emailUsuario);

		if (apostador == null) {
			return Collections.emptyList();
		}

		return ligaDAO.selecionarPorApostador(apostador.getId());
	}
	
	@Override
	public Liga recuperarLiga(long idLiga) {
		checkArgument(idLiga > 0);
		return ligaDAO.findById(idLiga);
	}

	@Override
	public Liga cadastrarNovaLiga(String nome, String emailAdmin)
			throws CadastroLigaException {

		checkNotNull(emptyToNull(nome));

		final Apostador apostador = this.recuperarApostador(emailAdmin);

		final Liga liga = new Liga(nome);
		liga.setAdministrador(apostador);

		ligaDAO.create(liga);
		ligaApostadorService.inscreverApostador(liga, apostador);

		// enviar email.

		return liga;
	}

	@Override
	public Liga atualizarLiga(long idLiga, String nome, String emailAdmin)
			throws CadastroLigaException {
		checkNotNull(emptyToNull(nome));

		final Apostador apostador = this.recuperarApostador(emailAdmin);
		final Liga liga = ligaDAO.findById(idLiga);
		
		if(liga.getAdministrador().equals(apostador)) {
			liga.setNome(nome);
			liga.setPermalink(WebUtils.toPermalink(nome));
			
			ligaDAO.update(liga);
			
			return liga;
		}
		
		throw new CadastroLigaException("Adm nao pertence liga", "liga.err.atualiza.adm");
	}
	

	private Apostador recuperarApostador(final String email)
			throws CadastroLigaException {
		checkNotNull(emptyToNull(email),
				"O email do administrador da liga nao pode ser nulo");

		final Apostador apostador = apostadorService
				.selecionarApostadorPorEmail(email);

		if (apostador == null) {
			throw new CadastroLigaException("Apostador nao encontrado", "liga.err.cad.adm.not_found");
		}

		return apostador;
	}


	

}
