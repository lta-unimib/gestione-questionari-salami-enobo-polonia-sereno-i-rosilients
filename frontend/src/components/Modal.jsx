import React from 'react';
import Login from '../pages/home/Login';
import Registration from '../pages/home/Registrazione';
import Verify from '../pages/home/Verify'; // 🔥 Importa Verify

const Modal = ({ toggleModal, formType, setUser }) => {
  return (
    <div
      className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50"
      onClick={() => toggleModal(null)}
    >
      <div className="" onClick={(e) => e.stopPropagation()}>
        <button
          onClick={() => toggleModal(null)}
          className="absolute top-2 right-2 text-gray-600 hover:text-gray-900"
        >
          <span className="text-2xl">&times;</span>
        </button>

        {/* Mostra il form corrispondente */}
        {formType === 'login' && <Login toggleModal={toggleModal} setUser={setUser} />}
        {formType === 'register' && (
          <Registration toggleModal={toggleModal} onRegistrationSuccess={() => toggleModal('verify')} />
        )}
        {formType === 'verify' && <Verify toggleModal={toggleModal} setUser={setUser} />}
      </div>
    </div>
  );
};

export default Modal;
