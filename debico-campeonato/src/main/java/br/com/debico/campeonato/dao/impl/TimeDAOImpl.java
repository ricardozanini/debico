package br.com.debico.campeonato.dao.impl;

import java.util.List;

import javax.inject.Named;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.debico.campeonato.dao.TimeDAO;
import br.com.debico.model.Time;
import br.com.tecnobiz.spring.config.dao.AbstractJPADao;


@Named
@Transactional(propagation=Propagation.MANDATORY)
class TimeDAOImpl extends AbstractJPADao<Time, Integer> implements TimeDAO {

    public TimeDAOImpl() {
        super(Time.class);
    }
    
    /**
     * @see <a href="http://stackoverflow.com/questions/1341104/parameter-in-like-clause-jpql">Parameter in like clause JPQL</a>
     * @see <a href="http://openjpa.apache.org/builds/1.0.1/apache-openjpa-1.0.1/docs/manual/jpa_overview_query.html#jpa_overview_query_functions">1.4.  JPQL Functions</a>
     */
    public List<Time> pesquisarParteNome(String nome) {
        return getEntityManager()
                .createQuery("SELECT time FROM Time time WHERE time.nome LIKE CONCAT(:nome, '%')", Time.class)
                .setParameter("nome", nome)
                .setMaxResults(20)
                .getResultList();
    }

}
