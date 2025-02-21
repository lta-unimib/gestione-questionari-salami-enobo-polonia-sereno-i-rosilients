import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import ReactModal from 'react-modal';
import { TrashIcon } from '@heroicons/react/20/solid';

const VisualizzaCompilazioniUtenti = () => {
    const { id } = useParams();
    const [compilazioni, setCompilazioni] = useState([]);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [compilazioneToDelete, setCompilazioneToDelete] = useState(null);
    const token = localStorage.getItem("jwt");
    const userEmail = localStorage.getItem("userEmail");
    const navigate = useNavigate();
    
    useEffect(() => {
        if (!userEmail) return;

        let url = `http://localhost:8080/api/questionariCompilati/others/${userEmail}/${id}`;
        
        fetch(url, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
        })
        .then(response => {
        if (!response.ok) {
            throw new Error('Errore nel recupero delle compilazioni');
        }
        return response.json();
        })
        .then(data => {
        setCompilazioni(data);
        })
        .catch(error => {
        console.error('Errore:', error);
        });
    }, [userEmail, token]);

    // Per visualizzare la compilazione
    const handleVisualizzaCompilazione = (idQuestionario, idCompilazione) => {
        //TO DO: collegare alla route per visualizzare le compilazioni che manca
    };

    const openDeleteModal = (idCompilazione) => {
        setCompilazioneToDelete(idCompilazione);
        setIsDeleteModalOpen(true);
    }

    const closeDeleteModal = () => {
        setIsDeleteModalOpen(false);
        setCompilazioneToDelete(null);
    }

    const handleDelete = () => {
        fetch(`http://localhost:8080/api/questionariCompilati/deleteQuestionarioCompilato/${compilazioneToDelete}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
        })
        .then(response => {
        if (!response.ok) {
            throw new Error('Errore nella cancellazione del questionario');
        }
        setCompilazioni(compilazioni.filter(q => q.idCompilazione !== compilazioneToDelete));
        closeDeleteModal();
        })
        .catch(error => console.error('Errore:', error))
    }

    return (
        <div className="p-8">
        <h1 className="text-2xl font-bold mt-6">Compilazioni per il tuo Questionario</h1>
        
        <div className="mt-6">
            {compilazioni.length === 0 ? (
            <p className="text-center">Non ci sono compilazioni per i tuoi questionari :&#40;</p>
            ) : (
            <ul>
                {compilazioni.map((compilazione) => (
                <li key={compilazione.idCompilazione} className="border p-4 my-2 rounded-lg shadow-lg flex justify-between items-center">

                    <div>       
                        <h2 className="text-xl font-semibold">{compilazione.titoloQuestionario}</h2>
                        <p className="text-gray-600">
                        Creato da: {compilazione.emailCreatore || "Anonymous"}
                        </p>
                        <p className="text-gray-600">
                            Compilato il: {new Date(compilazione.dataCompilazione).toLocaleString()}
                        </p>
                    </div>

                    <div className="edit flex gap-4">
                    <button
                        onClick={() => handleVisualizzaCompilazione(compilazione.idQuestionario, compilazione.idCompilazione)}
                        className="bg-white text-personal-purple border-2 border-personal-purple py-1 px-2 rounded-lg hover:bg-personal-purple hover:text-white transition duration-200 text-lg"
                    >
                        Visualizza compilazione
                    </button>
                    <button
                        onClick={() => openDeleteModal(compilazione.idCompilazione)}
                        className="text-red-600 hover:text-red-800 mr-6"
                        >
                        <TrashIcon className="h-6 w-6" />
                        </button>
                    </div>

                </li>
                ))}
            </ul>
            )}

            <ReactModal
            isOpen={isDeleteModalOpen}
            onRequestClose={closeDeleteModal}
            contentLabel="Conferma eliminazione"
            className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50"
            overlayClassName="modal-overlay"
            >
            <div className="bg-white p-8 rounded-lg w-96 text-center">
                <h2 className="text-2xl font-semibold text-gray-800">Sei sicuro di voler eliminare questa compilazione?</h2>
                <div className="mt-4">
                <button 
                    onClick={handleDelete}
                    className="bg-red-500 text-white px-6 py-2 rounded-lg mr-4 hover:bg-red-600 transition">
                    Elimina
                </button>
                <button 
                    onClick={closeDeleteModal} 
                    className="bg-gray-500 text-white px-6 py-2 rounded-lg hover:bg-gray-600 transition">
                    Annulla
                </button>
                </div>
            </div>
            </ReactModal>
        </div>
        </div>
    );
};

export default VisualizzaCompilazioniUtenti;
