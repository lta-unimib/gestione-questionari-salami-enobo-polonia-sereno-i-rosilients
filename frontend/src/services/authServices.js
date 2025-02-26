const API_BASE_URL = "http://localhost:8080/auth";

const login = async (email, password) => {
  try {
    const response = await fetch(`${API_BASE_URL}/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password }),
    });

    let data;
    if (response.ok) {
      data = await response.json();
    } else {
      data = await response.text();
    }

    if (response.status === 404) throw new Error("Email non esistente");
    if (response.status === 500) throw new Error("Email o password errate");
    if (response.status === 401) throw new Error(data || "Credenziali errate");
    if (response.status === 403) throw new Error("Account non verificato. Controlla la tua email.");

    return data;
  } catch (error) {
    console.error("Errore nel login:", error);
    throw error;
  }
};

const register = async (email, password) => {
  try {
    const response = await fetch(`${API_BASE_URL}/signup`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password }),
    });

    const data = await response.json();
    if (!response.ok) {
      if (response.status === 400 && data.message === "Email già registrata") {
        throw new Error("Email già registrata. Prova ad accedere o usa un'altra email.");
      }
      throw new Error(data.message || "Errore nella registrazione");
    }

    return data;
  } catch (error) {
    console.error("Errore nella registrazione:", error);
    throw error;
  }
};

const verify = async (email, verificationCode) => {
  try {
    const response = await fetch(`${API_BASE_URL}/verify`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify({ email, verificationCode }),
    });

    const data = await response.json();
    if (response.status === 401) throw new Error(data.message || "Codice di verifica errato");
    if (!response.ok) throw new Error("Errore nella verifica");

    return data;
  } catch (error) {
    console.error("Errore nella verifica:", error);
    throw error;
  }
};

const resendVerificationCode = async (email) => {
  try {
    const response = await fetch(`${API_BASE_URL}/resend?email=${encodeURIComponent(email)}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
    });

    const data = await response.text();
    if (!response.ok) throw new Error(`Errore: ${data}`);

    return data;
  } catch (error) {
    console.error("Errore nell'invio del codice di verifica:", error);
    throw error;
  }
};

export { login, register, verify, resendVerificationCode };