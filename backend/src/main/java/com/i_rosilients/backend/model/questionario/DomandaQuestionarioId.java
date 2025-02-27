package com.i_rosilients.backend.model.questionario;

import java.io.Serializable;

public class DomandaQuestionarioId implements Serializable {

    private int idDomanda;
    private int idQuestionario;

    public DomandaQuestionarioId() {}

    public DomandaQuestionarioId(int idDomanda, int idQuestionario) {
        this.idDomanda = idDomanda;
        this.idQuestionario = idQuestionario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomandaQuestionarioId that = (DomandaQuestionarioId) o;
        return idDomanda == that.idDomanda && idQuestionario == that.idQuestionario;
    }

    @Override
    public int hashCode() {
        return 31 * idDomanda + idQuestionario;
    }
}
