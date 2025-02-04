import React, { useState } from 'react';
import Login from '../pages/home/Login'; // Importa il componente Login
import Registration from '../pages/home/Registrazione'; // Importa il componente Registrazione

const Modal = ({ toggleModal }) => {
const [formType, setFormType] = useState('login'); // Stato per determinare se mostrare il login o la registrazione

const handleSwitchForm = (form) => {
    setFormType(form);
};

return (
    <div
      className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50" // Overlay a tutto schermo e centrato
      onClick={() => toggleModal(null)}
    >
      <div
        className="" // Modal box
        onClick={(e) => e.stopPropagation()} // Evita la propagazione del click all'overlay
      >
        <button
          onClick={() => toggleModal(null)}
          className="absolute top-2 right-2 text-gray-600 hover:text-gray-900"
        >
          <span className="text-2xl">&times;</span>
        </button>

        {/* Condizione per rendere il form di Login o Registrazione */}
        {formType === 'login' ? (
          <Login toggleModal={handleSwitchForm} />
        ) : (
          <Registration toggleModal={handleSwitchForm} />
        )}
      </div>
    </div>
);
};

export default Modal;
