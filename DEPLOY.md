# RaceHub Backend - Deploy su Render

## 🚀 Deploy Automatico

### Prerequisiti
- Account Render.com (gratuito)
- Repository GitHub connesso

### Step 1: Connetti Repository
1. Vai su [Render Dashboard](https://dashboard.render.com/)
2. Click su "New +" → "Blueprint"
3. Connetti il repository: `https://github.com/M1ch3l300/racehub-backend.git`
4. Seleziona branch: `security/env-variables`
5. Render rileverà automaticamente il file `render.yaml`

### Step 2: Configura Variabili d'Ambiente
Nel Render Dashboard, imposta le seguenti variabili d'ambiente:

#### Database Configuration (OBBLIGATORIO)
```
DB_URL=jdbc:postgresql://your-db-host:5432/racehub
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
```

#### Telegram Bot Configuration (OBBLIGATORIO)
```
TELEGRAM_BOT_TOKEN=your_bot_token_from_botfather
TELEGRAM_BOT_USERNAME=your_bot_username
```

#### Spring Profile (Già configurato in render.yaml)
```
SPRING_PROFILES_ACTIVE=prod
```

### Step 3: Deploy
1. Click su "Apply" per avviare il deploy
2. Render eseguirà:
   - `./mvnw clean package -DskipTests`
   - `java -Dserver.port=$PORT -jar target/racehub-backend-0.0.1-SNAPSHOT.jar`
3. Il servizio sarà disponibile su: `https://racehub-backend-XXXX.onrender.com`

## 📊 Health Check
- Endpoint: `/actuator/health`
- Render monitora automaticamente questo endpoint

## 🔧 Configurazione Database Gratuito su Render

### Opzione 1: PostgreSQL su Render (Consigliato)
1. Nel Render Dashboard, click "New +" → "PostgreSQL"
2. Nome: `racehub-db`
3. Database: `racehub`
4. User: `racehub_user`
5. Region: `Frankfurt` (stessa del backend)
6. Plan: `Free`
7. Copia le credenziali generate:
   - Internal Database URL (usa questo per DB_URL)
   - Username
   - Password

### Opzione 2: Database Esterno
Puoi usare:
- [ElephantSQL](https://www.elephantsql.com/) - Free tier PostgreSQL
- [Supabase](https://supabase.com/) - Free tier PostgreSQL
- [Neon](https://neon.tech/) - Free tier PostgreSQL

## 🔐 Sicurezza

### File Esclusi dal Repository (.gitignore)
```
.env
application-local.properties
*.log
target/
```

### Variabili d'Ambiente - NON committare mai:
- ❌ DB_PASSWORD
- ❌ TELEGRAM_BOT_TOKEN
- ❌ Credenziali di qualsiasi tipo

## 📝 Note Importanti

### Free Tier Limitations
- Il servizio si spegne dopo 15 minuti di inattività
- Primo avvio dopo inattività: ~30-60 secondi
- 750 ore/mese di uptime (sufficiente per un progetto)

### Logs
- Visualizza logs in tempo reale nel Render Dashboard
- Sezione "Logs" del tuo servizio

### Troubleshooting

**Problema**: Build fallisce
- Verifica che `./mvnw` abbia permessi di esecuzione
- Controlla i logs di build nel Dashboard

**Problema**: Applicazione non si avvia
- Verifica che tutte le variabili d'ambiente siano configurate
- Controlla i logs dell'applicazione
- Verifica connessione al database

**Problema**: Database connection error
- Verifica DB_URL, DB_USERNAME, DB_PASSWORD
- Assicurati che il database sia nella stessa region
- Usa Internal Database URL se database su Render

## 🔄 Aggiornamenti Automatici
- Ogni push su branch `security/env-variables` triggera un nuovo deploy
- Render esegue automaticamente build e restart

## 📞 Support
- [Render Documentation](https://render.com/docs)
- [Render Community](https://community.render.com/)

## 🎯 URL Finale
Dopo il deploy, il backend sarà disponibile su:
```
https://racehub-backend-XXXX.onrender.com
```

Usa questo URL come `REACT_APP_API_URL` nel frontend!