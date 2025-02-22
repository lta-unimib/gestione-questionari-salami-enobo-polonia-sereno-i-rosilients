import React, { useState, useEffect } from 'react';
import ReactModal from 'react-modal';
import { useNavigate } from 'react-router-dom';

const Compilazioni = ({ user }) => {
  const [compilazioni, setCompilazioni] = useState([]);
  const [filtro, setFiltro] = useState("Tutti");
  const [searchTerm, setSearchTerm] = useState("");

  const token = localStorage.getItem("jwt");
  const userEmail = localStorage.getItem("userEmail");

  const navigate = useNavigate();

  ReactModal.setAppElement('#root');

  useEffect(() => {

    if (!userEmail) {
      console.warn("userEmail non disponibile, il fetch non verrà eseguito.");
      return;
    }

    let url = "";
    let stato = "";

    if (filtro === "Definitivi") {
      url = `http://localhost:8080/api/questionariCompilati/definitivi/utente/${userEmail}`;
    } else if (filtro === "In Sospeso") {
      url = `http://localhost:8080/api/questionariCompilati/inSospeso/utente/${userEmail}`;
    } else if (filtro === "Tutti") {
      url = `http://localhost:8080/api/questionariCompilati/all/utente/${userEmail}`;
    }

    fetch(url, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        }
      })
      .then((response) => {
        if (response.status === 204) {
            return [];
        } else if (!response.ok) {
            throw new Error('Errore nel recupero delle compilazioni');
        }
        return response.json();
      })
      .then(async (data) => {
    
        const compilazioniConStato = await Promise.all(
          data.map(async (compilazione) => {
            const isDefinitivo = await handleIsDefinitivo(compilazione.idCompilazione);
            console.log("isDefinitivo", isDefinitivo);
            return {
              ...compilazione,
              stato: isDefinitivo ? "DEFINITIVO" : "IN SOSPESO",
            };
          })
        );
    
        const filteredCompilazioni = compilazioniConStato.filter((compilazione) => {
          if (filtro === "Definitivi") {
            return compilazione.stato === "DEFINITIVO";
          } else if (filtro === "In Sospeso") {
            return compilazione.stato === "IN SOSPESO";
          } else {
            return true;
          }
        });
    
        const sortedCompilazioni = filteredCompilazioni.sort(
          (a, b) => new Date(b.dataCompilazione) - new Date(a.dataCompilazione)
        );
    
        setCompilazioni(sortedCompilazioni);
      })
      .catch((error) => {
        console.error("Errore durante il fetch:", error);
      });
  }, [userEmail, filtro]);

  const compilazioniFiltrate = compilazioni.filter(c =>
    c.titoloQuestionario && c.titoloQuestionario.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const continuaCompilazione = (idQuestionario, idCompilazione) => {
    navigate(`/questionari/compilaQuestionario/${idQuestionario}?idCompilazione=${idCompilazione}`);
  };

  const visualizzaQuestionario = (idCompilazione) => {
    navigate(`/questionari/visualizzaQuestionarioCompilato/${idCompilazione}`);
  };

  const handleIsDefinitivo = async (idCompilazione) => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/questionariCompilati/checkIsDefinitivo/${idCompilazione}`,
        {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
          }
        }
      );
  
      if (!response.ok) {
        throw new Error('Errore nel verificare lo stato della compilazione');
      }
  
      const result = await response.json();
      return result;
    } catch (error) {
      console.error("Errore durante la verifica dello stato:", error);
      return false;
    }
  };

  return (
    <div className='mx-24'>
      <h1 className="text-4xl">Compilazioni</h1>
      <div className="flex justify-between items-center mt-4">
        <select value={filtro} onChange={(e) => setFiltro(e.target.value)} className="border rounded-lg p-2">
          <option value="Tutti">Tutti</option>
          <option value="Definitivi">Definitivi</option>
          <option value="In Sospeso">In sospeso</option>
        </select>
        <input 
          type="text"
          placeholder="Cerca per titolo..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="border rounded-lg p-2"
        />
      </div>

      {compilazioniFiltrate.length > 0 ? (
        <ul className='mt-12'>
          {compilazioniFiltrate.map(c => (
            <li key={c.idCompilazione} className="border p-4 my-2 rounded-lg shadow-lg flex justify-between">
              <div>
                <h3 className="text-xl font-semibold">{c.titoloQuestionario}</h3>
                <p>Data compilazione: {new Date(c.dataCompilazione).toLocaleDateString()}</p>
                <p>Stato: {c.stato === "DEFINITIVO" ? "✅ Definitiva" : "⏳ In sospeso"}</p>
              </div>
              
              <div className="flex items-center">
                {c.stato === "DEFINITIVO" && (
                  <button 
                    className="bg-white text-personal-purple border-2 border-personal-purple py-1 px-3 rounded-lg hover:bg-personal-purple hover:text-white transition duration-200 ml-4"
                    onClick={() => visualizzaQuestionario(c.idCompilazione)}
                  >
                    Visualizza
                  </button>
                )}

                {c.stato === "IN SOSPESO" && (
                  <button
                    className="bg-white text-personal-purple border-2 border-personal-purple py-1 px-3 rounded-lg hover:bg-personal-purple hover:text-white transition duration-200 ml-4"
                    onClick={() => continuaCompilazione(c.idQuestionario, c.idCompilazione)}
                  >
                    Termina
                  </button>
                )}
              </div>
            </li>
          ))}
        </ul>
      ) : (
        <p className="text-gray-500 mt-4">Nessuna compilazione trovata.</p>
      )}
    </div>
  );
};

export default Compilazioni;