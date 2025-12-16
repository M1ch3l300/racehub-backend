# RaceHub Backend - Deploy su Render con Docker

## 🚀 Deploy Automatico con Docker

### Prerequisiti
- Account Render.com (gratuito)
- Repository GitHub connesso
- Database PostgreSQL (vedi sezione Database)

### Step 1: Connetti Repository
1. Vai su [Render Dashboard](https://dashboard.render.com/)
2. Click su "New +" → "Web Service"
3. Connetti il repository: `https://github.com/M1ch3l300/racehub-backend.git`
4. Seleziona branch: `security/env-variables`
5. Render rileverà automaticamente il `Dockerfile`

### Step 2: Configura il Servizio
Render rileverà automaticamente la configurazione da `render.yaml`:

**Nome**: `racehub-backend`
**Runtime**: Docker
**Region**: Frankfurt (EU)
**Plan**: Free
**Branch**: security/env-variables

### Step 3: Configura Variabili d'Ambiente
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

#### JWT Configuration (OBBLIGATORIO)
```
JWT_SECRET=your_secure_jwt_secret_key_min_256_bits
JWT_EXPIRATION=86400000
```

#### Spring Profile (Già configurato in render.yaml)
```
SPRING_PROFILES_ACTIVE=prod
```

### Step 4: Deploy
1. Click su "Create Web Service"
2. Render eseguirà il build Docker:
   - **Stage 1**: Maven build con `mvn clean package -DskipTests`
   - **Stage 2**: Crea immagine runtime con OpenJDK 17
   - **Deploy**: Avvia container con porta dinamica
3. Il servizio sarà disponibile su: `https://racehub-backend-XXXX.onrender.com`

## 🐳 Docker Build Process

### Multi-Stage Build
Il `Dockerfile` utilizza un build multi-stage per ottimizzare dimensioni e performance:

**Stage 1 - Build (maven:3.8.5-openjdk-17)**:
```dockerfile
- Copia pom.xml
- Download dipendenze (cached layer)
- Copia source code
- Build con Maven: mvn clean package -DskipTests
```

**Stage 2 - Runtime (openjdk:17-jdk-slim)**:
```dockerfile
- Copia solo il JAR compilato
- Configura health check
- ENTRYPOINT con porta dinamica: java -Dserver.port=${PORT:-8080}
```

### Ottimizzazioni
- ✅ Layer caching per dipendenze Maven
- ✅ Immagine runtime slim (riduce dimensioni)
- ✅ Health check integrato
- ✅ Memoria JVM ottimizzata: -Xmx512m -Xms256m
- ✅ Porta dinamica per compatibilità Render

## 📊 Health Check
- **Endpoint**: `/actuator/health`
- **Intervallo**: 30 secondi
- **Timeout**: 3 secondi
- **Start Period**: 60 secondi (tempo per avvio app)
- **Retries**: 3 tentativi prima di considerare unhealthy

Render monitora automaticamente questo endpoint per verificare lo stato del servizio.

## 🔧 Configurazione Database Gratuito

### Opzione 1: PostgreSQL su Render (Consigliato)
1. Nel Render Dashboard, click "New +" → "PostgreSQL"
2. **Nome**: `racehub-db`
3. **Database**: `racehub`
4. **User**: `racehub_user`
5. **Region**: `Frankfurt` (stessa del backend)
6. **Plan**: `Free`
7. Copia le credenziali generate:
   - **Internal Database URL** (usa questo per DB_URL)
   - Username
   - Password

**Formato DB_URL per Render PostgreSQL**:
```
jdbc:postgresql://dpg-XXXXX-a.frankfurt-postgres.render.com/racehub
```

### Opzione 2: Supabase (Alternativa Gratuita)
1. Vai su [Supabase](https://supabase.com/)
2. Crea nuovo progetto
3. Vai su Settings → Database
4. Copia **Session Pooler** connection string (porta 6543)
5. Converti in formato JDBC:
```
jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres
```

### Opzione 3: ElephantSQL
1. Vai su [ElephantSQL](https://www.elephantsql.com/)
2. Piano: Tiny Turtle (Free)
3. Region: EU-West-1 (Ireland)
4. Copia URL e converti in formato JDBC

### Opzione 4: Neon
1. Vai su [Neon](https://neon.tech/)
2. Crea progetto gratuito
3. Region: Frankfurt
4. Copia connection string e converti in JDBC

## 🔐 Sicurezza

### File Esclusi dal Repository (.gitignore)
```
.env
application-local.properties
*.log
target/
```

### File Esclusi dal Docker Build (.dockerignore)
```
target/
.git/
.env
*.log
src/test/
```

### Variabili d'Ambiente - NON committare mai:
- ❌ DB_PASSWORD
- ❌ TELEGRAM_BOT_TOKEN
- ❌ JWT_SECRET
- ❌ Credenziali di qualsiasi tipo

## 📝 Note Importanti

### Free Tier Limitations (Render)
- Il servizio si spegne dopo **15 minuti di inattività**
- Primo avvio dopo inattività: **~30-60 secondi**
- **750 ore/mese** di uptime (sufficiente per un progetto)
- **512 MB RAM**
- **0.1 CPU**

### Free Tier Database (Render PostgreSQL)
- **1 GB storage**
- **Expires dopo 90 giorni** (ricrea gratuitamente)
- **Max 97 connessioni**

### Docker Build Time
- Primo build: **~5-8 minuti** (download dipendenze Maven)
- Build successivi: **~2-3 minuti** (cache layers)

### Logs
- Visualizza logs in tempo reale nel Render Dashboard
- Sezione "Logs" del tuo servizio
- Filtra per errori, warnings, info

## 🐛 Troubleshooting

### Problema: Docker build fallisce

**Soluzione 1 - Verifica Dockerfile localmente**:
```bash
cd /workspace/racehub-backend
docker build -t racehub-backend .
docker run -p 8080:8080 \
  -e DB_URL="jdbc:postgresql://..." \
  -e DB_USERNAME="..." \
  -e DB_PASSWORD="..." \
  -e TELEGRAM_BOT_TOKEN="..." \
  -e TELEGRAM_BOT_USERNAME="..." \
  -e JWT_SECRET="..." \
  racehub-backend
```

**Soluzione 2 - Controlla logs di build**:
- Vai su Render Dashboard → tuo servizio → "Logs"
- Cerca errori durante Maven build
- Verifica che tutte le dipendenze siano scaricabili

### Problema: Applicazione non si avvia

**Soluzione**:
1. Verifica che **tutte** le variabili d'ambiente siano configurate
2. Controlla i logs dell'applicazione in Render Dashboard
3. Verifica connessione al database:
   - DB_URL corretto?
   - Username/Password corretti?
   - Database accessibile dalla region Render?

### Problema: Database connection error

**Errore comune**: `Connection refused` o `Timeout`

**Soluzione**:
1. **Render PostgreSQL**: Usa **Internal Database URL** (non External)
2. **Supabase**: Usa **Session Pooler** (porta 6543), non Direct Connection
3. Verifica formato JDBC URL:
   ```
   jdbc:postgresql://host:port/database
   ```
4. Assicurati che database e backend siano nella **stessa region** (o region vicine)

### Problema: Health check fails

**Soluzione**:
1. Verifica che `/actuator/health` sia accessibile
2. Controlla che l'app si avvii correttamente (logs)
3. Aumenta `start-period` nel Dockerfile se l'app impiega più tempo ad avviarsi
4. Verifica che la porta sia configurata correttamente:
   ```properties
   server.port=${PORT:8080}
   ```

### Problema: Out of Memory

**Soluzione**:
1. Verifica impostazioni JVM nel Dockerfile:
   ```
   -Xmx512m -Xms256m
   ```
2. Riduci `spring.datasource.hikari.maximum-pool-size` se necessario
3. Considera upgrade a piano Render a pagamento per più RAM

### Problema: Slow cold start

**Causa**: Free tier Render spegne il servizio dopo 15 minuti di inattività

**Soluzioni**:
1. **Accettare il comportamento** (normale per free tier)
2. **Keep-alive service**: Usa un servizio esterno per ping ogni 10 minuti
   - [UptimeRobot](https://uptimerobot.com/) (free)
   - [Cron-job.org](https://cron-job.org/) (free)
3. **Upgrade a Render Starter plan** ($7/mese) per servizio always-on

## 🔄 Aggiornamenti Automatici

### Auto-Deploy da Git
- Ogni push su branch `security/env-variables` triggera un nuovo deploy
- Render esegue automaticamente:
  1. Docker build
  2. Push immagine a registry
  3. Deploy nuovo container
  4. Health check
  5. Switch traffic al nuovo container

### Manual Deploy
- Dashboard Render → tuo servizio → "Manual Deploy"
- Seleziona branch e commit
- Click "Deploy"

### Rollback
- Dashboard Render → "Deployments"
- Click su deploy precedente → "Rollback to this version"

## 📞 Support
- [Render Documentation](https://render.com/docs)
- [Render Community](https://community.render.com/)
- [Docker Documentation](https://docs.docker.com/)

## 🎯 URL Finale
Dopo il deploy, il backend sarà disponibile su:
```
https://racehub-backend-XXXX.onrender.com
```

**Usa questo URL come `REACT_APP_API_URL` nel frontend Vercel!**

Esempio:
```
REACT_APP_API_URL=https://racehub-backend-abc123.onrender.com/api
```

## ✅ Checklist Deploy

### Pre-Deploy
- [ ] Account Render creato
- [ ] Repository GitHub connesso
- [ ] Database PostgreSQL configurato
- [ ] Credenziali database ottenute
- [ ] Telegram Bot creato (token ottenuto)
- [ ] JWT Secret generato (min 256 bit)

### Durante Deploy
- [ ] Web Service creato su Render
- [ ] Branch `security/env-variables` selezionato
- [ ] Tutte le variabili d'ambiente configurate
- [ ] Docker build completato con successo
- [ ] Health check passa

### Post-Deploy
- [ ] URL backend ottenuto
- [ ] Endpoint `/actuator/health` risponde 200 OK
- [ ] Test login API
- [ ] Test CRUD endpoints
- [ ] Logs verificati (no errori critici)
- [ ] URL backend configurato nel frontend Vercel

## 🎉 Deploy Completato!

Una volta completato il deploy:
1. ✅ Backend disponibile su Render
2. ✅ Database PostgreSQL connesso
3. ✅ Health check attivo
4. ✅ Auto-deploy configurato
5. ✅ Pronto per connettere il frontend!

**Next Step**: Deploy frontend su Vercel con `REACT_APP_API_URL` configurato! 🚀
## 🌐 CORS Configuration

Il backend utilizza una variabile d'ambiente per configurare le origini permesse (CORS).

### Variabile d'ambiente richiesta

```bash
ALLOWED_ORIGINS=http://localhost:3000,https://mgx.dev,https://app.mgx.dev
```

**Formato:** Lista di URL separati da virgola, senza spazi.

### Configurazione su Render

1. Vai su **Dashboard** → **racehub-backend** → **Environment**
2. Aggiungi la variabile:
   - **Key:** `ALLOWED_ORIGINS`
   - **Value:** `http://localhost:3000,https://mgx.dev,https://app.mgx.dev`
3. Salva e rideploy

### Sviluppo locale

Se `ALLOWED_ORIGINS` non è definita, il backend usa automaticamente `http://localhost:3000` come fallback.

Per testare con origini multiple in locale, aggiungi al file `.env`:

```bash
ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3001
```
