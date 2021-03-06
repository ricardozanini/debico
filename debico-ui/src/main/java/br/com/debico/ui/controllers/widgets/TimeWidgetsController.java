package br.com.debico.ui.controllers.widgets;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.debico.campeonato.services.TimeService;
import br.com.debico.model.Time;

/**
 * Controller para interação com o domínio de {@link Time}
 * 
 * @author Ricardo Zanini (ricardozanini@gmail.com)
 *
 */
@Controller
public class TimeWidgetsController {

    @Inject
    private TimeService timeService;

    public TimeWidgetsController() {

    }

    /**
     * Retorna uma lista de times de acordo com o nome parcial enviado.
     * 
     * @param nomeParcial
     * @return
     */
    @RequestMapping(value = "/widgets/time", method = RequestMethod.GET)
    public @ResponseBody List<Time> procurarPorNome(
            @RequestParam(required = true, value = "nome") String nomeParcial) {
        return timeService.procurarTimePorNome(nomeParcial);
    }

}
