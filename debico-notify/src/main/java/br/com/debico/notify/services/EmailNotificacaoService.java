package br.com.debico.notify.services;

import java.util.Map;

import br.com.debico.notify.model.Contato;
import br.com.debico.notify.model.EmailTemplate;
import br.com.debico.notify.model.TipoNotificacao;

/**
 * <i>Marked interface</i> para definir o serviço de envio de notificações por
 * email.
 * 
 * @author Ricardo Zanini (ricardozanini@gmail.com)
 *
 */
public interface EmailNotificacaoService extends
	NotificacaoService<EmailTemplate> {

    public void enviarNotificacao(Contato destinatario, EmailTemplate template,
	    Map<String, Object> contexto);

    public void enviarNotificacao(Contato destinatario,
	    TipoNotificacao notificacao, Map<String, Object> contexto);

}
