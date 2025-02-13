package com.i_rosilients.backend.repository;

import com.i_rosilients.backend.model.DomandaQuestionario;
import com.i_rosilients.backend.model.Questionario;
import com.i_rosilients.backend.model.Domanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DomandaQuestionarioRepository extends JpaRepository<DomandaQuestionario, Integer> {

    List<DomandaQuestionario> findByQuestionario(Questionario questionario);

    List<DomandaQuestionario> findByDomanda(Domanda domanda);

    void deleteByQuestionario(Questionario questionario);
}
