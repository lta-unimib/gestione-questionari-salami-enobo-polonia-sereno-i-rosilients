package com.i_rosilients.backend.services.persistence;

import com.i_rosilients.backend.model.domanda.Domanda;
import com.i_rosilients.backend.model.questionario.DomandaQuestionario;
import com.i_rosilients.backend.model.questionario.DomandaQuestionarioId;
import com.i_rosilients.backend.model.questionario.Questionario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DomandaQuestionarioRepository extends JpaRepository<DomandaQuestionario, DomandaQuestionarioId> {

    List<DomandaQuestionario> findByQuestionario(Questionario questionario);

    List<DomandaQuestionario> findByDomanda(Domanda domanda);

    void deleteByQuestionario(Questionario questionario);

    @Query("SELECT dq.domanda.idDomanda FROM DomandaQuestionario dq WHERE dq.questionario.idQuestionario = :idQuestionario")
    List<Integer> findDomandeIdsByQuestionarioId(Integer idQuestionario);
}
