import React, { useState } from 'react';
import Login from './Login';
import Registration from './Registrazione';
import Verify from './Verify';

const Modal = ({ toggleModal, formType, setUser }) => {
  const [userEmail, setUserEmail] = useState(""); // Stato per memorizzare l'email

  const handleToggleModal = (type, email = "") => {
    if (type === "verify" && email) {
      setUserEmail(email); // Salva l'email quando si apre il modal di verifica
    }
    toggleModal(type);
  };

  return (
    <div
      className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50"
      onClick={() => handleToggleModal(null)}
    >
      <div className="" onClick={(e) => e.stopPropagation()}>
        <button
          onClick={() => handleToggleModal(null)}
          className="absolute top-2 right-2 text-gray-600 hover:text-gray-900"
        >
          <span className="text-2xl">&times;</span>
        </button>

        {/* Mostra il form corrispondente */}
        {formType === 'login' && <Login toggleModal={handleToggleModal} setUser={setUser} />}
        {formType === 'register' && (
          <Registration toggleModal={handleToggleModal} onRegistrationSuccess={(email) => handleToggleModal('verify', email)} />
        )}
        {formType === 'verify' && <Verify toggleModal={handleToggleModal} email={userEmail} />}
      </div>
    </div>
  );
};

export default Modal;
