package br.com.debico.ui.controllers.internal;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import br.com.debico.social.services.LigaService;
import br.com.debico.ui.controllers.AbstractViewController;

@Controller
public class MinhasLigasController extends AbstractViewController {

    @Inject
    private LigaService ligaService;

    public MinhasLigasController() {

    }

    @Override
    protected String getViewName() {
        return "minhas-ligas";
    }

    @RequestMapping(value = "/ligas/administrar", method = RequestMethod.GET)
    public ModelAndView minhasLigas() {
        resetViewName();

        this.addObject("minhasLigas",
                ligaService.consultarLiga(this.getUsuarioIdAutenticado()));

        return this.getModelAndView();
    }

}
