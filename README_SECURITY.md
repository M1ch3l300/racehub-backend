# 🔒 Guida alla Configurazione della Sicurezza - RaceHub Backend

## 📋 Panoramica

Questo documento spiega come configurare correttamente le variabili d'ambiente per proteggere le credenziali sensibili del progetto RaceHub Backend.

## 🚀 Setup Rapido

### 1. Configurazione per Sviluppo Locale

**Opzione A: Usando variabili d'ambiente (Raccomandato per produzione)**

1. Copia il file `.env.example` in `.env`:
   ```bash
   cp .env.example .env
   ```

2. Modifica `.env` con le tue credenziali reali:
   ```properties
   DB_URL=jdbc:postgresql://aws-1-eu-west-1.pooler.supabase.com:6543/postgres?prepareThreshold=0&preparedStatementCacheQueries=0
   DB_USERNAME=postgres.vsbofqeefrfbdrscxsnl
   DB_PASSWORD=la_tua_password_reale
   TELEGRAM_BOT_USERNAME=racehub_simracing_bot
   TELEGRAM_BOT_TOKEN=7942177729:AAGmVTh0TdGf3_GGSW18WQnkK0XpDNNCCGc
   JWT_SECRET=RaceHubSuperSecretKeyForJWTTokenGeneration2025MustBeLongEnough
   JWT_EXPIRATION=86400000
   ```

3. Carica le variabili d'ambiente prima di avviare l'applicazione:
   ```bash
   # Linux/Mac
   export $(cat .env | xargs)
   mvn spring-boot:run
   
   # Windows (PowerShell)
   Get-Content .env | ForEach-Object { $var = $_.Split('='); [Environment]::SetEnvironmentVariable($var[0], $var[1]) }
   mvn spring-boot:run
   ```

**Opzione B: Usando application-local.properties (Più semplice per sviluppo)**

1. Copia il file template:
   ```bash
   cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties
   ```

2. Modifica `application-local.properties` con le tue credenziali reali

3. Avvia l'applicazione specificando il profilo local:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```

### 2. Configurazione per Produzione

**IMPORTANTE**: Non usare mai `application-local.properties` in produzione!

#### Deploy su Server (Linux)

1. Crea un file `.env` sul server con le credenziali di produzione

2. Configura le variabili d'ambiente nel servizio systemd:
   ```ini
   [Service]
   EnvironmentFile=/path/to/your/.env
   ExecStart=/usr/bin/java -jar /path/to/racehub.jar
   ```

#### Deploy su Docker

```dockerfile
# Nel tuo Dockerfile o docker-compose.yml
environment:
  - DB_URL=${DB_URL}
  - DB_USERNAME=${DB_USERNAME}
  - DB_PASSWORD=${DB_PASSWORD}
  - TELEGRAM_BOT_USERNAME=${TELEGRAM_BOT_USERNAME}
  - TELEGRAM_BOT_TOKEN=${TELEGRAM_BOT_TOKEN}
  - JWT_SECRET=${JWT_SECRET}
  - JWT_EXPIRATION=${JWT_EXPIRATION}
```

#### Deploy su Cloud (Heroku, AWS, Azure, etc.)

Configura le variabili d'ambiente tramite la dashboard del provider:
- Heroku: Settings → Config Vars
- AWS Elastic Beanstalk: Configuration → Software → Environment properties
- Azure App Service: Configuration → Application settings

## 🔑 Variabili d'Ambiente Obbligatorie

| Variabile | Descrizione | Esempio |
|-----------|-------------|---------|
| `DB_URL` | URL di connessione al database PostgreSQL | `jdbc:postgresql://host:6543/postgres?prepareThreshold=0` |
| `DB_USERNAME` | Username del database | `postgres.vsbofqeefrfbdrscxsnl` |
| `DB_PASSWORD` | Password del database | `your_secure_password` |
| `TELEGRAM_BOT_USERNAME` | Username del bot Telegram | `racehub_simracing_bot` |
| `TELEGRAM_BOT_TOKEN` | Token del bot Telegram (da @BotFather) | `7942177729:AAGmVTh0TdGf3_GGSW18WQnkK0XpDNNCCGc` |
| `JWT_SECRET` | Chiave segreta per firmare i token JWT (min 256 bit) | `RaceHubSuperSecretKey...` |
| `JWT_EXPIRATION` | Durata del token JWT in millisecondi (opzionale, default: 86400000 = 24h) | `86400000` |

## 🛡️ Best Practices di Sicurezza

### ✅ DA FARE:
- ✅ Usa variabili d'ambiente per tutte le credenziali sensibili
- ✅ Mantieni `.env` e `application-local.properties` fuori dal controllo versione (già in `.gitignore`)
- ✅ Usa password complesse e uniche per ogni ambiente
- ✅ Rigenera `JWT_SECRET` per ogni ambiente (dev, staging, prod)
- ✅ Limita i permessi di accesso ai file di configurazione (chmod 600)
- ✅ Usa secret manager in produzione (AWS Secrets Manager, Azure Key Vault, etc.)

### ❌ NON FARE:
- ❌ Non committare mai file `.env` o `application-local.properties` su Git
- ❌ Non condividere credenziali via email, chat, o documenti non criptati
- ❌ Non usare le stesse credenziali in sviluppo e produzione
- ❌ Non hardcodare credenziali nel codice sorgente
- ❌ Non loggare valori di variabili d'ambiente sensibili

## 🔄 Rotazione delle Credenziali

### Database Password
1. Genera una nuova password su Supabase Dashboard
2. Aggiorna `DB_PASSWORD` in tutti gli ambienti
3. Riavvia l'applicazione

### JWT Secret
1. Genera una nuova chiave sicura:
   ```bash
   openssl rand -base64 64
   ```
2. Aggiorna `JWT_SECRET` in tutti gli ambienti
3. **ATTENZIONE**: Tutti i token JWT esistenti diventeranno invalidi

### Telegram Bot Token
1. Contatta @BotFather su Telegram
2. Usa il comando `/token` per rigenerare
3. Aggiorna `TELEGRAM_BOT_TOKEN` in tutti gli ambienti

## 🆘 Troubleshooting

### Errore: "Could not resolve placeholder 'DB_URL'"
**Causa**: Le variabili d'ambiente non sono state caricate.

**Soluzione**:
- Verifica che il file `.env` esista e contenga tutte le variabili
- Assicurati di aver caricato le variabili prima di avviare l'app
- Oppure usa il profilo `local` con `application-local.properties`

### Errore: "Access denied for user"
**Causa**: Credenziali database errate.

**Soluzione**:
- Verifica `DB_USERNAME` e `DB_PASSWORD` nel file `.env`
- Controlla che le credenziali siano corrette su Supabase Dashboard
- Verifica che l'IP del server sia whitelistato su Supabase

### Errore: "Invalid JWT signature"
**Causa**: `JWT_SECRET` non corrisponde tra generazione e validazione.

**Soluzione**:
- Verifica che `JWT_SECRET` sia lo stesso in tutti i nodi dell'applicazione
- Se hai cambiato il secret, rigenera i token JWT

## 📞 Supporto

Per problemi di sicurezza o configurazione, contatta il team di sviluppo RaceHub.

**IMPORTANTE**: Non condividere mai credenziali reali nei ticket di supporto!