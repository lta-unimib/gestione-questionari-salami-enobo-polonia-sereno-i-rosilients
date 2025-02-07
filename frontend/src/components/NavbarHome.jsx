import React, { useState } from 'react';
import Modal from './Modal'; 

const NavbarHome = ({ setUser }) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formType, setFormType] = useState(null); 

  const toggleModal = (type = null) => {
    setIsModalOpen(type !== null); 
    setFormType(type);
  };

  const handleRegistrationSuccess = () => {
    toggleModal("verify"); 
  };

  return (
    <nav>
      <div className="flex justify-between my-5">
        <div className="ml-16">
          <h1 className="text-3xl text-personal-purple font-semibold">WebSurveys</h1>
        </div>
        <div className="flex justify-end mr-16">
          <div className='my-auto'>
            <button onClick={() => toggleModal('register')}>Registrazione</button>
          </div>
          <div className="ml-16 bg-personal-purple text-white px-8 rounded-md py-1">
            <button onClick={() => toggleModal('login')}>Login</button>
          </div>
        </div>
      </div>

      {/* Modal Popup */}
      {isModalOpen && (
        <Modal
          toggleModal={toggleModal}
          formType={formType}
          setUser={setUser}
          onRegistrationSuccess={handleRegistrationSuccess} 
        />
      )}
    </nav>
  );
};

export default NavbarHome;
