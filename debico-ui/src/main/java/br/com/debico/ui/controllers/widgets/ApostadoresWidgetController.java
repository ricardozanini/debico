package br.com.debico.ui.controllers.widgets;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.debico.model.Apostador;
import br.com.debico.social.services.ApostadorService;

@Controller
public class ApostadoresWidgetController {

	@Inject
	private ApostadorService apostadorService;

	public ApostadoresWidgetController() {

	}

	/**
	 * Retorna uma lista de times de acordo com o nome parcial enviado.
	 * 
	 * @param nomeParcial
	 * @return
	 */
	@RequestMapping(value = "/widgets/apostadores", method = RequestMethod.GET)
	public @ResponseBody List<Apostador> procurarPorNome(
			@RequestParam(required = true, value = "nome") String nomeParcial) {
		return apostadorService.pesquisarApostadoresPorParteNome(nomeParcial);
	}

}
