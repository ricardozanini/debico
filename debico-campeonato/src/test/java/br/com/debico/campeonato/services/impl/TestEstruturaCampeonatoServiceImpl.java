package br.com.debico.campeonato.services.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import br.com.debico.campeonato.factories.impl.QuadrangularSimplesFactory;
import br.com.debico.campeonato.model.EstruturaCampeonato;
import br.com.debico.campeonato.services.EstruturaCampeonatoService;
import br.com.debico.campeonato.test.support.AbstractCampeonatoUnitTest;
import br.com.debico.model.Time;
import br.com.debico.model.campeonato.Campeonato;

@DirtiesContext
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
public class TestEstruturaCampeonatoServiceImpl extends
	AbstractCampeonatoUnitTest {

    @Inject
    private EstruturaCampeonatoService estruturaCampeonatoService;

    @Test
    public void testInserirNovaEstrutura() {
	Time BRASIL = new Time("Brasil");
	Time ARGENTINA = new Time("Argentina");
	Time ITALIA = new Time("Italia");
	final List<Time> times = new ArrayList<Time>();

	times.add(ARGENTINA);
	times.add(ITALIA);
	times.add(BRASIL);

	EstruturaCampeonato estruturaCampeonato = new QuadrangularSimplesFactory()
		.criarCampeonato("Time dos Solteiros", times);

	Campeonato campeonato = estruturaCampeonatoService
		.inserirNovaEstrutura(estruturaCampeonato);

	assertNotNull(campeonato);
	assertTrue(campeonato.getId() > 0);
    }

}
