import React, { useState, useEffect } from "react";
import { verify, resendVerificationCode } from "../../services/authServices";

const Verify = ({ toggleModal, email }) => { 
  const [verificationCode, setVerificationCode] = useState("");
  const [countdown, setCountdown] = useState(30);
  const [canResend, setCanResend] = useState(false);
  const [loading, setLoading] = useState(false);
  const [resending, setResending] = useState(false);

  useEffect(() => {
    if (countdown > 0) {
      const timer = setTimeout(() => setCountdown(countdown - 1), 1000);
      return () => clearTimeout(timer);
    } else {
      setCanResend(true);
    }
  }, [countdown]);

  const handleVerify = async (event) => {
    event.preventDefault();
    setLoading(true);
  
    console.log("Verifica in corso...", { email, verificationCode });
  
    try {
      const response = await verify(email, verificationCode);
  
      alert(response.message || "Verifica completata con successo!");
      toggleModal();
    } catch (error) {
      console.error("Errore:", error);
      alert(error.message || "Si è verificato un errore durante la verifica.");
    } finally {
      setLoading(false);
    }
  };
  const handleResendCode = async () => {
    if (!canResend) return;
  
    setResending(true);
    console.log("Invio nuovo codice a:", email);
  
    try {
      const response = await resendVerificationCode(email);
  
      alert(response || "Codice di verifica inviato!");
      setCountdown(30);
      setCanResend(false);
    } catch (error) {
      console.error("Errore nell'invio del codice:", error);
      alert(error.message || "Si è verificato un errore, riprova più tardi.");
    } finally {
      setResending(false);
    }
  };

  return (
    <div className="bg-white p-8 rounded-lg shadow-lg w-96 border border-purple-100">
      <h2 className="text-2xl mb-6 font-bold text-center text-purple-900">Verifica il tuo account</h2>
      
      <p className="mb-4 text-center text-gray-600">
        Abbiamo inviato un codice di verifica a <span className="font-medium">{email}</span>
      </p>
      
      <form onSubmit={handleVerify} className="space-y-4">
        <div>
          <label htmlFor="verificationCode" className="block text-sm font-medium text-gray-700 mb-1">Codice di verifica</label>
          <input
            id="verificationCode"
            type="text"
            placeholder="Inserisci il codice a 6 cifre"
            value={verificationCode}
            onChange={(e) => setVerificationCode(e.target.value)}
            className="w-full p-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all"
            required
          />
        </div>
        
        <div className="flex justify-center">
          <button 
            type="button"
            onClick={handleResendCode} 
            disabled={!canResend || resending}
            className={`text-sm transition-all ${!canResend ? 'text-gray-400 cursor-not-allowed' : resending ? 'text-purple-400 cursor-wait' : 'text-purple-700 hover:text-purple-900 hover:underline cursor-pointer'}`}
          >
            {resending ? (
              <span className="flex items-center">
                <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-purple-700" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Invio in corso...
              </span>
            ) : !canResend ? (
              `Invia nuovo codice (${countdown}s)`
            ) : (
              "Invia nuovo codice di verifica"
            )}
          </button>
        </div>
        
        <button 
          type="submit" 
          className="w-full p-3 rounded-md text-white font-medium transition-all"
          disabled={loading}
          style={{ backgroundColor: loading ? '#6b46c1' : '#3603CD' }}
        >
          {loading ? (
            <div className="flex items-center justify-center">
              <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Verifica in corso...
            </div>
          ) : (
            'Verifica account'
          )}
        </button>
      </form>
      
      <div className="mt-6 text-center">
        <p className="text-gray-600 text-sm">
          Non hai ricevuto il codice? Controlla la cartella spam o richiedi un nuovo codice.
        </p>
      </div>
    </div>
  );
};

export default Verify;