package br.com.debico.social.services.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;

import org.jasypt.util.password.PasswordEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import br.com.debico.core.MessagesCodes;
import br.com.debico.model.Apostador;
import br.com.debico.model.ApostadorOpcoes;
import br.com.debico.model.Usuario;
import br.com.debico.notify.model.ContatoImpl;
import br.com.debico.notify.model.TipoNotificacao;
import br.com.debico.notify.services.EmailNotificacaoService;
import br.com.debico.notify.services.TemplateContextoBuilder;
import br.com.debico.social.CadastroApostadorException;
import br.com.debico.social.UsuarioInexistenteException;
import br.com.debico.social.dao.ApostadorDAO;
import br.com.debico.social.dao.UsuarioDAO;
import br.com.debico.social.model.PasswordContext;
import br.com.debico.social.model.TokenLostPassword;
import br.com.debico.social.services.UsuarioService;

/**
 * Além das funções do bolão, implementa as interfaces de acesso do
 * <code>Spring Security</code> para realizar os casos de uso de login.
 * 
 * @see <a
 *      href="http://www.mkyong.com/spring-security/spring-security-hibernate-annotation-example/">Spring
 *      Security + Hibernate Annotation Example</a>
 * @see <a
 *      href="http://www.mkyong.com/tutorials/spring-security-tutorials/">Spring
 *      Security Tutorials</a>
 * 
 * @author ricardozanini
 * @since 0.1
 */
@Named
@Transactional
class UsuarioServiceImpl implements UsuarioService, UserDetailsService {

    protected static final Logger LOGGER = LoggerFactory
	    .getLogger(UsuarioServiceImpl.class);

    @Inject
    private UsuarioDAO usuarioDAO;

    @Inject
    private ApostadorDAO apostadorDAO;

    @Inject
    private PasswordEncryptor passwordEncryptor;

    @Inject
    private EmailNotificacaoService notificacaoService;

    @Inject
    @Named("resourceBundleMessageSource")
    private MessageSource messageSource;

    public UsuarioServiceImpl() {

    }

    @Transactional(rollbackFor = CadastroApostadorException.class)
    public void cadastrarApostadorUsuario(Apostador apostador,
	    String confirmacaoSenha) throws CadastroApostadorException {
	LOGGER.debug(
		"[cadastrarApostadorUsuario] Tentando realizar o cadastro do apostador '{}'.",
		apostador);
	checkNotNull(apostador, "Apostador nao pode ser nulo");
	checkNotNull(emptyToNull(confirmacaoSenha),
		"Confirmacao de senha em branco!");

	Usuario usuario = apostador.getUsuario();

	if (usuarioDAO.selecionarPorEmail(usuario.getEmail()) == null) {
	    // todas opções padrão.
	    apostador.setOpcoes(new ApostadorOpcoes());

	    this.checarConfirmacaoSenha(usuario, confirmacaoSenha);
	    this.confirirPoliticaSenha(usuario.getSenha());
	    UsuarioUtils.criptografarSenha(passwordEncryptor, usuario);

	    usuarioDAO.create(usuario);
	    apostadorDAO.create(apostador);

	    // TODO: enviar email de confirmação.
	    LOGGER.debug(
		    "[cadastrarApostadorUsuario] Apostador '{}' cadastrado com sucesso!",
		    usuario);
	} else {
	    LOGGER.warn(
		    "[cadastrarApostadorUsuario] Tentativa de cadastro de usuario com o email '{}'. Ja existe.",
		    usuario.getEmail());
	    throw new CadastroApostadorException(messageSource,
		    MessagesCodes.USUARIO_JA_CADASTRADO, usuario.getEmail());
	}
    }

    public UserDetails loadUserByUsername(String username)
	    throws UsernameNotFoundException {
	LOGGER.debug("[loadUserByUsername] Tentando carregar o usuario '{}'.",
		username);

	Apostador apostador = apostadorDAO.selecionarPorEmail(username);

	if (apostador == null || apostador.getUsuario() == null) {
	    throw new UsernameNotFoundException(messageSource.getMessage(
		    MessagesCodes.USUARIO_NAO_ENCONTRADO,
		    new Object[] { username }, Locale.getDefault()));
	}

	Usuario usuario = apostador.getUsuario();
	usuario.setUltimoLogin(new Date());

	usuarioDAO.update(usuario);

	LOGGER.debug(
		"[loadUserByUsername] Apostador com o usuario '{}' carregado.",
		usuario);
	return UsuarioUtils.construirUsuario(apostador);
    }

    @Override
    public boolean alterarSenhaApostadorUsuario(PasswordContext passwordContext)
	    throws CadastroApostadorException {
	if (passwordContext.hasToken()) {
	    // TODO validar o token
	} else {
	    final String senhaAtual = usuarioDAO
		    .recuperarSenhaAtual(passwordContext.getEmailUsuario());

	    if (!this.passwordEncryptor.checkPassword(
		    passwordContext.getSenhaAtual(), senhaAtual)) {
		throw new CadastroApostadorException(messageSource,
			MessagesCodes.SENHA_ATUAL_NAO_CONFERE);
	    }
	}

	this.checarConfirmacaoSenha(passwordContext.getNovaSenha(),
		passwordContext.getConfirmacaoSenha());
	this.confirirPoliticaSenha(passwordContext.getNovaSenha());

	usuarioDAO.alterarSenha(passwordContext.getEmailUsuario(), UsuarioUtils
		.criptografarSenha(passwordEncryptor,
			passwordContext.getNovaSenha()));

	return true;
    }

    @Override
    public void enviarTokenEsqueciMinhaSenha(String emailUsuario)
	    throws UsuarioInexistenteException {
	checkNotNull(emptyToNull(emailUsuario), "Email do usuario obrigatorio");

	final Usuario usuario = this.recuperarUsuario(emailUsuario);
	final TokenLostPassword token = TokenLostPassword.newInstance(usuario);
	final Map<String, Object> contextoEmail = TemplateContextoBuilder
		.contextLinkBuilder(token.getToken());
	contextoEmail.put("usuario", usuario);

	// salvar o token
	// enviar por email
	notificacaoService.enviarNotificacao(
		new ContatoImpl(emailUsuario),
		TipoNotificacao.ESQUECI_SENHA, 
		contextoEmail);
    }

    /**
     * Recupera o usuário com o email em questão
     * 
     * @param email
     * @return instância do usuário
     * @throws UsuarioInexistenteException
     *             caso não encontre.
     */
    private Usuario recuperarUsuario(String email)
	    throws UsuarioInexistenteException {
	final Usuario usuario = usuarioDAO.selecionarPorEmail(email);

	if (usuario == null) {
	    throw new UsuarioInexistenteException(email);
	}

	return usuario;
    }

    /*
     * TODO: passar os metodos abaixo para UsuarioUtils assim que o
     * messageSource não for mais obrigado na exception.
     */

    /**
     * Confere a política de senha.
     * <p/>
     * Atualmente utilizamos o seguinte
     * <code>regexp: (?=.{6,})(?=.*[a-zA-Z])(?=.*[0-9]).*</code>.
     * <p/>
     * Isso significa ao menos 6 caraterers com dígitos e letras maiúsculas ou
     * minúsculas.
     * 
     * @param senha
     *            a ser conferida
     * @throws CadastroApostadorException
     *             caso a <code>regexp</code> não seja atendida.
     * @see <a href="http://stackoverflow.com/a/9922150">Regex for password
     *      policy</a>
     */
    protected void confirirPoliticaSenha(final String senha)
	    throws CadastroApostadorException {
	Pattern p = Pattern.compile("(?=.{6,})" + // "" followed by 6+ symbols
		"(?=.*[a-zA-Z])" + // --- ' ' --- at least 1 lower or upper
		// "(?=.*[A-Z])" + // --- ' ' --- at least 1 upper
		"(?=.*[0-9])" + // --- ' ' --- at least 1 digit
		// "(?=.*\\p{Punct})"+ // --- ' ' --- at least 1 symbol
		".*"); // the actual characters

	if (!p.matcher(senha).matches()) {
	    throw new CadastroApostadorException(messageSource,
		    MessagesCodes.SENHA_FAIL_POLITICA);
	}
    }

    protected void checarConfirmacaoSenha(final Usuario usuario,
	    final String confirmacaoSenha) throws CadastroApostadorException {
	this.checarConfirmacaoSenha(usuario.getSenha(), confirmacaoSenha);
    }

    protected void checarConfirmacaoSenha(final String senha,
	    final String confirmacaoSenha) throws CadastroApostadorException {
	if (!confirmacaoSenha.equals(senha)) {
	    throw new CadastroApostadorException(messageSource,
		    MessagesCodes.SENHA_NAO_CONFERE);
	}
    }

}
