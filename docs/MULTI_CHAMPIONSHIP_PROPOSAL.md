# Proposta per la Gestione Multi-Campionato (Bot Telegram)

## Stato Attuale
Il backend è già strutturato per supportare più campionati:
- Le entità `Championship`, `Race`, `Pilot` e `RaceResult` sono correttamente relazionate.
- I service e i repository supportano già il filtraggio per `championshipId`.
- Il bot accetta un parametro opzionale ID nei comandi (es. `/standings 2`), ma di default usa `1`.

## Problema
L'utente non ha modo di sapere quali campionati esistono e quali sono i loro ID. L'esperienza utente attuale è "cieca" se non si conosce l'ID a memoria.

## Soluzione Proposta

### 1. Nuovo Comando `/championships`
Introdurre un comando che lista tutti i campionati attivi e futuri.

**Output Esempio:**
```
🏆 Campionati Disponibili:

1. F1 2024 Season (Stagione 2024) - ACTIVE
2. GT3 Cup (Stagione 1) - UPCOMING

Usa l'ID per i dettagli:
/standings [id] - Classifica
/races [id] - Calendario
```

### 2. Miglioramento `/help`
Aggiornare il messaggio di aiuto per spiegare chiaramente come navigare tra i campionati.

### 3. (Opzionale) Selezione Interattiva
In futuro, potremmo implementare una tastiera inline (pulsanti) per permettere all'utente di cliccare sul campionato desiderato invece di digitare l'ID, ma per ora il comando `/championships` è il primo passo essenziale.

## Piano di Implementazione
1.  Aggiungere metodo `getAllChampionships` in `ChampionshipService` (già presente).
2.  Implementare `handleChampionships()` in `RaceHubBot`.
3.  Aggiornare `handleHelp()` in `RaceHubBot`.