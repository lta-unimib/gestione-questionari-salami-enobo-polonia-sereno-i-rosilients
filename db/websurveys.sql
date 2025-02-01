CREATE SCHEMA IF NOT EXISTS websurveys;

USE websurveys;

CREATE TABLE IF NOT EXISTS UTENTE (
    email VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS QUESTIONARIO (
    id_questionario INT PRIMARY KEY AUTO_INCREMENT,
    email_utente VARCHAR(255),
    nome VARCHAR(255) NOT NULL,
    FOREIGN KEY (email_utente) REFERENCES UTENTE(email) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS DOMANDA (
    id_domanda INT PRIMARY KEY AUTO_INCREMENT,
    email_utente VARCHAR(255), -- L'utente che ha creato la domanda
    argomento TEXT NOT NULL,
    testo_domanda TEXT NOT NULL,
    FOREIGN KEY (email_utente) REFERENCES UTENTE(email) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS OPZIONE (
    id_opzione INT PRIMARY KEY AUTO_INCREMENT,
    id_domanda INT NOT NULL,
    testo_opzione TEXT NOT NULL,
    FOREIGN KEY (id_domanda) REFERENCES DOMANDA(id_domanda) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS DOMANDA_QUESTIONARIO (
    id_domanda INT,
    id_questionario INT,
    PRIMARY KEY (id_domanda, id_questionario),
    FOREIGN KEY (id_domanda) REFERENCES DOMANDA(id_domanda) ON DELETE CASCADE,
    FOREIGN KEY (id_questionario) REFERENCES QUESTIONARIO(id_questionario) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS QUESTIONARIO_COMPILATO (
    id_compilazione INT PRIMARY KEY AUTO_INCREMENT,
    email_utente VARCHAR(255),  -- Se compilato da utente registrato
    id_questionario INT NOT NULL,
    utente_anonimo VARCHAR(255), -- Se compilato da utente anonimo (può essere UUID o IP hashato)
    data_compilazione DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (email_utente) REFERENCES UTENTE(email) ON DELETE CASCADE,
    FOREIGN KEY (id_questionario) REFERENCES QUESTIONARIO(id_questionario) ON DELETE CASCADE
);

DELIMITER //

CREATE TRIGGER IF NOT EXISTS chk_email_anonimo_update
BEFORE UPDATE ON questionario_compilato
FOR EACH ROW
BEGIN
    IF NEW.email_utente IS NULL AND NEW.utente_anonimo IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Errore: email_utente e utente_anonimo non possono essere entrambi NULL';
    END IF;
END;
//

DELIMITER ;

CREATE TABLE IF NOT EXISTS RISPOSTA (
    id_risposta INT PRIMARY KEY AUTO_INCREMENT,
    id_compilazione INT NOT NULL,
    id_domanda INT NOT NULL,
    testo_risposta TEXT NOT NULL,
    FOREIGN KEY (id_compilazione) REFERENCES QUESTIONARIO_COMPILATO(id_compilazione) ON DELETE CASCADE,
    FOREIGN KEY (id_domanda) REFERENCES DOMANDA(id_domanda) ON DELETE CASCADE
);
