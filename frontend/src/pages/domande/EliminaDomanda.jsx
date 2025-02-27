// src/components/Domande/DeleteModal.jsx
import React from 'react';
import ReactModal from 'react-modal';

// Make sure this is called in your main app component
// ReactModal.setAppElement('#root');

const EliminaDomanda = ({ isOpen, onClose, onDelete }) => {
  return (
    <ReactModal
      isOpen={isOpen}
      onRequestClose={onClose}
      contentLabel="Conferma eliminazione"
      className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50"
      overlayClassName="modal-overlay"
    >
      <div className="bg-white p-8 rounded-lg w-96 text-center">
        <h2 className="text-2xl font-semibold text-gray-800">Sei sicuro di voler eliminare questa domanda?</h2>
        <div className="mt-4">
          <button 
            onClick={onDelete} 
            className="bg-red-500 text-white px-6 py-2 rounded-lg mr-4 hover:bg-red-600 transition">
            Elimina
          </button>
          <button 
            onClick={onClose} 
            className="bg-gray-500 text-white px-6 py-2 rounded-lg hover:bg-gray-600 transition">
            Annulla
          </button>
        </div>
      </div>
    </ReactModal>
  );
};

export default EliminaDomanda;