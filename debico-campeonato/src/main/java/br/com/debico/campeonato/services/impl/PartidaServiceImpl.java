package br.com.debico.campeonato.services.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

import br.com.debico.campeonato.dao.PartidaDAO;
import br.com.debico.campeonato.services.PartidaService;
import br.com.debico.core.helpers.CacheKeys;
import br.com.debico.model.Partida;
import br.com.debico.model.Placar;

@Named
@Transactional(readOnly = false)
class PartidaServiceImpl implements PartidaService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PartidaServiceImpl.class);

	@Inject
	private PartidaDAO partidaDAO;

	@CacheEvict(value = CacheKeys.PARTIDAS_RODADA, allEntries = true)
	public Partida salvarPlacar(final int idPartida, final Placar placar) {
		checkArgument(idPartida > 0, "O id da partida eh invalido.");
		checkNotNull(placar, "O placar nao pode ser nulo.");

		LOGGER.debug("[salvarPlacar] Tentando salvar o placar {} da partida {}", placar, idPartida);

		Partida partida = partidaDAO.findById(idPartida);
		partida.setPlacar(placar);

		return partidaDAO.update(partida);
	}

	@Override
	@CacheEvict(value = CacheKeys.PARTIDAS_RODADA, allEntries = true)
	public Partida atualizarDataHorario(final int idPartida, final Date dataHorarioPartida) {
		checkArgument(idPartida > 0, "O id da partida eh invalido.");
		checkNotNull(dataHorarioPartida, "A data e horario da partida nao pode sernulo.");

		LOGGER.debug("[atualizarDataHorario] Tentando salvar a data {} da partida {}", dataHorarioPartida, idPartida);

		Partida partida = partidaDAO.findById(idPartida);
		partida.setDataHoraPartida(dataHorarioPartida);

		return partidaDAO.update(partida);
	}

	@Override
	public Partida atualizarDataHorarioLocal(int idPartida, Date dataHorarioPartida, String local) {
		checkArgument(idPartida > 0, "O id da partida eh invalido.");
		checkNotNull(dataHorarioPartida, "A data e horario da partida nao pode sernulo.");

		LOGGER.debug("[atualizarDataHorarioLocal] Tentando salvar a data {} e local {} da partida {}",
				dataHorarioPartida, local, idPartida);

		Partida partida = partidaDAO.findById(idPartida);

		partida.setDataHoraPartida(dataHorarioPartida);
		partida.setLocal(nullToEmpty(local));

		return partidaDAO.update(partida);
	}

}
