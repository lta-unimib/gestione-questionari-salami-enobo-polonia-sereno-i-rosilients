import React, { useState, useEffect } from "react";

const Verify = ({ toggleModal, email }) => { 
  const [verificationCode, setVerificationCode] = useState("");
  const [countdown, setCountdown] = useState(30);
  const [canResend, setCanResend] = useState(false);

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

    console.log("Verifica in corso...", { email, verificationCode });

    const user = { email, verificationCode };

    try {
      const response = await fetch("http://localhost:8080/auth/verify", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify(user),
      });

      const data = await response.json();

      if (response.status === 401) {
        alert(data.message || "Credenziali errate");
        return;
      }

      if (response.ok) {
        alert(data.message);
        toggleModal(); // Chiudi il modal dopo la verifica
      }
    } catch (error) {
      console.error("Errore:", error);
      alert("Si è verificato un errore durante la verifica.");
    }
  };

  const handleResendCode = async () => {
    if (!canResend) return;

    console.log("Invio nuovo codice a:", email);
    

    try {
      const response = await fetch(`http://localhost:8080/auth/resend?email=${encodeURIComponent(email)}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
      });
  
      const data = await response.text(); // L'API risponde con un messaggio di testo
  
      if (!response.ok) {
        alert(`Errore: ${data}`);
        return;
      }
  
      alert(data); // Mostra "Verification code sent"
      setCountdown(30);
      setCanResend(false);
    } catch (error) {
      console.error("Errore nell'invio del codice:", error);
      alert("Si è verificato un errore, riprova più tardi.");
    }
  };

  return (
    <div className="bg-white p-6 rounded-lg w-96">
      <h2 className="text-2xl mb-4">Verifica Account</h2>
      <form onSubmit={handleVerify}>
        <input
          type="text"
          placeholder="Codice di verifica"
          value={verificationCode}
          onChange={(e) => setVerificationCode(e.target.value)}
          className="w-full p-2 mb-3 border border-gray-300 rounded"
          required
        />
        <button 
          onClick={handleResendCode} 
          disabled={!canResend}
          className={`underline mb-3 ${canResend ? 'text-blue-500 cursor-pointer' : 'text-gray-400 cursor-not-allowed'}`}
        >
          {canResend ? "Invia di nuovo il codice di verifica" : `Invia di nuovo il codice di verifica ${countdown}s`}
        </button>
        <button type="submit" className="w-full p-2 bg-blue-500 text-white rounded">
          Conferma
        </button>
      </form>
    </div>
  );
};

export default Verify;
