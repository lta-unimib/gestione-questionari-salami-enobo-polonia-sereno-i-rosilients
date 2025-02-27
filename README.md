# Web Surveys - Documentazione

## Componenti del gruppo
- Ilaria Salami 894473
- Franck Enobo 899857
- Luca Polonia 899698
- Leonardo Sereno 899881

## 9.1 Tecnologie
- Node.js (per React)
- Java 17 (per Spring Boot)
- Maven (per gestire le dipendenze di Spring Boot)
- Database (MySQLWorkBench)
- XAMPP
- IDE consigliato (IntelliJ, VSCode, ecc.)

## 9.2 Installazione

### 9.2.1 Clonare il repository
```bash
git clone
```

### 9.2.2 Creazione del database in MySQL
Aprire MySQL, creare un nuovo database chiamato websurveys, esempio:
```sql
CREATE SCHEMA IF NOT EXISTS websurveys;
USE websurveys;
```

### 9.2.3 Avvio di XAMPP
Avviare moduli Apache e MySQL, verificare che entrambe i servizi siano attivi e funzionanti.

### 9.2.4 Backend
1. **Configurazione Database**
   - Aggiungi le credenziali del tuo DBMS nel file di configurazione di Spring Boot `application.properties`
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/websurveys
   spring.datasource.username=
   spring.datasource.password=
   spring.jpa.hibernate.ddl-auto=update
   ```

2. **Aprire il terminale nella directory backend**
   - Eseguire il seguente comando
   ```bash
   mvn clean install
   ```

3. **Avviare il backend**
   - Usa Maven per costruire il progetto
   ```bash
   ./mvnw spring-boot:run
   ```
   - Il backend si avvierà su una porta predefinita (es. 8080)

### 9.2.5 Frontend
1. **Spostarsi, nel terminale, nella directory frontend**

2. **Installazione delle dipendenze**
   ```bash
   npm install
   ```

3. **Avviare il frontend**
   - Una volta installate le dipendenze
   ```bash
   npm start
   ```
   - L'app React sarà accessibile su http://localhost:3000

## 9.3 Test Applicazione
Dopo aver avviato sia il backend che il frontend, puoi testare l'applicazione aprendo il browser e accedendo a http://localhost:3000. Assicurati che tutte le funzionalità funzionino correttamente.
