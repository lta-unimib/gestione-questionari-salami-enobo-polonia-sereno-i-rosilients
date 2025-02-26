async function fetchAllQuestionari() {
    try {
      const response = await fetch("http://localhost:8080/api/questionari/tuttiIQuestionari", {
        method: "GET",
        headers: { "Content-Type": "application/json" }
      });
      if (!response.ok) throw new Error("Errore nel recupero dei questionari");
      return await response.json();
    } catch (error) {
      console.error("Errore nella fetch:", error);
      throw error;
    }
  }
  
  async function checkCodeExists(codiceInt) {
    try {
      const response = await fetch(`http://localhost:8080/api/questionariCompilati/utenteNonRegistrato/${codiceInt}`, {
        method: "GET",
        headers: { "Content-Type": "application/json" }
      });
      if (!response.ok) throw new Error("Codice univoco non valido");
      return await response.json();
    } catch (error) {
      console.error("Errore nella fetch:", error);
      throw error;
    }
  }
  
  async function checkIsDefinitivo(codiceInt) {
    try {
      const response = await fetch(`http://localhost:8080/api/questionariCompilati/checkIsDefinitivo/${codiceInt}`, {
        method: "GET",
        headers: { "Content-Type": "application/json" }
      });
      if (!response.ok) throw new Error("Errore nei controlli del parametro definitivo");
      return await response.json();
    } catch (error) {
      console.error("Errore nella fetch:", error);
      throw error;
    }
  }
  
  async function deleteCompilazione(codiceInt) {
    try {
      const response = await fetch(`http://localhost:8080/api/questionariCompilati/deleteQuestionarioCompilato/${codiceInt}`, {
        method: "DELETE",
        headers: { "Content-Type": "application/json" }
      });
      if (!response.ok) throw new Error("Errore nella cancellazione");
      return true;
    } catch (error) {
      console.error("Errore nella fetch:", error);
      throw error;
    }
  }
  
  export { fetchAllQuestionari, checkCodeExists, checkIsDefinitivo, deleteCompilazione };
  