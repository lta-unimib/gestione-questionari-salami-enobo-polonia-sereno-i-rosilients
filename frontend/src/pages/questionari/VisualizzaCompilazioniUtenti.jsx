import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import ReactModal from 'react-modal';
import { TrashIcon } from '@heroicons/react/20/solid';
import { EyeIcon } from '@heroicons/react/24/solid';

import {getCompilazioniUtenti, inviaEmailNotifica, deleteQuestionarioCompilato} from '../../services/questionarioService';

const VisualizzaCompilazioniUtenti = () => {
    const { id } = useParams();
    const [compilazioni, setCompilazioni] = useState([]);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [compilazioneToDelete, setCompilazioneToDelete] = useState(null);
    const [userCompilazioneToDelete, setUserCompilazioneToDelete] = useState(null);
    const userEmail = localStorage.getItem("userEmail");
    const navigate = useNavigate();
 
    
    useEffect(() => {
        if (!userEmail) return;

        const fetchCompilazioni = async () => {
            try {
                const data = await getCompilazioniUtenti(userEmail, id);
                setCompilazioni(data);
            } catch (error) {
                console.error('Errore nel recupero delle compilazioni:', error);
            }
        };

        fetchCompilazioni();
    }, [userEmail, id]);
    
    const handleVisualizzaCompilazione = (idQuestionario, idCompilazione) => {
        navigate(`/questionari/visualizzaQuestionarioCompilato/${idCompilazione}/${idQuestionario}`);
    };

    const openDeleteModal = (idCompilazione, userCompilazione) => {
        setCompilazioneToDelete(idCompilazione);
        setUserCompilazioneToDelete(userCompilazione);
        setIsDeleteModalOpen(true);
    }

    const closeDeleteModal = () => {
        setCompilazioneToDelete(null);
        setUserCompilazioneToDelete(null);
        setIsDeleteModalOpen(false);   
    }

    const handleDelete = async () => {
        try {
            if (userCompilazioneToDelete !== "Anonymous") {
                await inviaEmailNotifica(
                    compilazioneToDelete, 
                    userCompilazioneToDelete
                );
                alert("L'utente sarà notificato via email");
            } else {
                alert("La compilazione da parte di Anonymous è stata eliminata correttamente");
            }

            await deleteQuestionarioCompilato(compilazioneToDelete);
            
            setCompilazioni(compilazioni.filter((q) => q.idCompilazione !== compilazioneToDelete));
            closeDeleteModal();
        } catch (error) {
            console.error('Errore:', error);
            alert("Si è verificato un errore durante l'eliminazione della compilazione");
        }
    };

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
                            Compilato da: {compilazione.emailCreatore}
                        </p>
                        <p className="text-gray-600">
                            In data: {new Date(compilazione.dataCompilazione).toLocaleString()}
                        </p>
                    </div>

                    <div className="edit flex gap-4">
                    <EyeIcon
                          className="w-5 h-5 text-gray-700 cursor-pointer hover:text-gray-800"
                          onClick={() => handleVisualizzaCompilazione(compilazione.idQuestionario, compilazione.idCompilazione)}
                        />
                    <button
                        onClick={() => openDeleteModal(compilazione.idCompilazione, compilazione.emailCreatore)}
                        className="text-red-600 hover:text-red-800 ml-3 mr-6"
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