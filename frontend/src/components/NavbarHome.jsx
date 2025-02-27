import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import Modal from '../pages/home/Modal'; 

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
      <div className="flex justify-between py-5">
        <div className="ml-16">
          <Link className='text-3xl text-personal-purple font-semibold' to='/'>WebSurveys</Link>
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
