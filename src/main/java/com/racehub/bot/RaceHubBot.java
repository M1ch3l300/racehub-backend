package com.racehub.bot;

import com.racehub.model.*;
import com.racehub.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RaceHubBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    private final ChampionshipService championshipService;
    private final RaceService raceService;
    private final PilotService pilotService;
    private final AnnouncementService announcementService;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            String response = handleCommand(messageText);
            sendMessage(chatId, response);
        }
    }

    private String handleCommand(String command) {
        String[] parts = command.split(" ");
        String cmd = parts[0].toLowerCase();

        switch (cmd) {
            case "/start":
                return handleStart();

            case "/help":
                return handleHelp();

            case "/championships":
                return handleChampionships();

            case "/standings":
                return handleStandings(parts);

            case "/races":
                return handleRaces(parts);

            case "/results":
                return handleResults(parts);

            case "/pilots":
                return handlePilots();

            case "/announcements":
                return handleAnnouncements(parts);

            default:
                return "❌ Comando non riconosciuto. Usa /help per vedere i comandi disponibili.";
        }
    }

    private String handleStart() {
        return "🏁 *Benvenuto in RaceHub!*\n\n" +
                "Bot ufficiale per F1 Sim Racing Championships.\n\n" +
                "Usa /help per vedere tutti i comandi disponibili.";
    }

    private String handleHelp() {
        return "📋 *Comandi Disponibili:*\n\n" +
                "/championships - Lista campionati attivi\n" +
                "/standings [id] - Classifica campionato\n" +
                "/races [id] - Lista gare campionato\n" +
                "/results [raceId] - Risultati gara\n" +
                "/pilots - Lista piloti\n" +
                "/announcements [id] - Annunci campionato\n\n" +
                "Esempio: /standings 1";
    }

    private String handleChampionships() {
        try {
            List<Championship> championships = championshipService.getAllChampionships();

            if (championships.isEmpty()) {
                return "⚠️ Nessun campionato trovato.";
            }

            StringBuilder response = new StringBuilder();
            response.append("🏆 *CAMPIONATI DISPONIBILI*\n\n");

            for (Championship ch : championships) {
                String statusIcon = "ACTIVE".equals(ch.getStatus()) ? "🟢" :
                                  "COMPLETED".equals(ch.getStatus()) ? "🏁" : "📅";
                
                response.append(statusIcon).append(" *").append(ch.getName()).append("* (ID: ").append(ch.getId()).append(")\n")
                        .append("   Stagione: ").append(ch.getSeason()).append(" | Status: ").append(ch.getStatus()).append("\n\n");
            }
            
            response.append("Usa l'ID per vedere i dettagli:\n")
                    .append("/standings [id] - Classifica\n")
                    .append("/races [id] - Calendario");

            return response.toString();

        } catch (Exception e) {
            return "❌ Errore nel recuperare i campionati.";
        }
    }

    private String handleStandings(String[] parts) {
        try {
            Long championshipId = parts.length > 1 ? Long.parseLong(parts[1]) : 1L;

            Map<String, Object> standings = raceService.getChampionshipStandings(championshipId);
            Championship championship = (Championship) standings.get("championship");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results = (List<Map<String, Object>>) standings.get("standings");

            StringBuilder response = new StringBuilder();
            response.append("🏆 *").append(championship.getName()).append("*\n");
            response.append("📅 Stagione ").append(championship.getSeason()).append("\n\n");
            response.append("*CLASSIFICA:*\n\n");

            int position = 1;
            for (Map<String, Object> result : results) {
                Pilot pilot = (Pilot) result.get("pilot");
                Integer points = (Integer) result.get("points");

                String medal = position == 1 ? "🥇" : position == 2 ? "🥈" : position == 3 ? "🥉" : "  ";
                response.append(medal).append(" ")
                        .append(position).append(". ")
                        .append(pilot.getName())
                        .append(" - ").append(points).append(" pts\n");
                position++;
            }

            return response.toString();

        } catch (Exception e) {
            return "❌ Errore nel recuperare la classifica. Assicurati di specificare un ID valido.";
        }
    }

    private String handleRaces(String[] parts) {
        try {
            Long championshipId = parts.length > 1 ? Long.parseLong(parts[1]) : 1L;

            List<Race> races = raceService.getRacesByChampionship(championshipId);

            if (races.isEmpty()) {
                return "⚠️ Nessuna gara trovata per questo campionato.";
            }

            StringBuilder response = new StringBuilder();
            response.append("🏁 *CALENDARIO GARE*\n\n");

            for (Race race : races) {
                String status = race.getStatus().equals("COMPLETED") ? "✅" :
                        race.getStatus().equals("IN_PROGRESS") ? "🏎️" : "📅";

                response.append(status).append(" Round ").append(race.getRound())
                        .append(" - ").append(race.getCircuit()).append("\n");
            }

            return response.toString();

        } catch (Exception e) {
            return "❌ Errore nel recuperare le gare.";
        }
    }

    private String handleResults(String[] parts) {
        try {
            if (parts.length < 2) {
                return "⚠️ Specifica l'ID della gara. Esempio: /results 1";
            }

            Long raceId = Long.parseLong(parts[1]);
            List<RaceResult> results = raceService.getRaceResults(raceId);
            Race race = raceService.getRaceById(raceId);

            if (results.isEmpty()) {
                return "⚠️ Nessun risultato disponibile per questa gara.";
            }

            StringBuilder response = new StringBuilder();
            response.append("🏁 *").append(race.getCircuit()).append("*\n");
            response.append("📅 Round ").append(race.getRound()).append("\n\n");
            response.append("*RISULTATI:*\n\n");

            for (RaceResult result : results) {
                String medal = result.getPosition() == 1 ? "🥇" :
                        result.getPosition() == 2 ? "🥈" :
                                result.getPosition() == 3 ? "🥉" : "  ";

                response.append(medal).append(" ")
                        .append(result.getPosition()).append(". ")
                        .append(result.getPilot().getName())
                        .append(" - ").append(result.getPoints()).append(" pts\n");
            }

            return response.toString();

        } catch (Exception e) {
            return "❌ Errore nel recuperare i risultati.";
        }
    }

    private String handlePilots() {
        try {
            List<Pilot> pilots = pilotService.getAllPilots();

            if (pilots.isEmpty()) {
                return "⚠️ Nessun pilota trovato.";
            }

            StringBuilder response = new StringBuilder();
            response.append("👥 *PILOTI REGISTRATI*\n\n");

            for (Pilot pilot : pilots) {
                response.append("🏎️ ").append(pilot.getName())
                        .append(" (#").append(pilot.getRacingNumber()).append(")")
                        .append(" - ").append(pilot.getTeam()).append("\n");
            }

            return response.toString();

        } catch (Exception e) {
            return "❌ Errore nel recuperare i piloti.";
        }
    }

    private String handleAnnouncements(String[] parts) {
        try {
            Long championshipId = parts.length > 1 ? Long.parseLong(parts[1]) : 1L;

            List<Announcement> announcements = announcementService.getAnnouncementsByChampionship(championshipId);

            if (announcements.isEmpty()) {
                return "⚠️ Nessun annuncio disponibile.";
            }

            StringBuilder response = new StringBuilder();
            response.append("📢 *ANNUNCI RECENTI*\n\n");

            int count = 0;
            for (Announcement ann : announcements) {
                if (count >= 5) break; // Mostra solo ultimi 5

                String icon = ann.getType().equals("PENALTY") ? "⚠️" :
                        ann.getType().equals("NEWS") ? "📰" : "📣";

                response.append(icon).append(" *").append(ann.getTitle()).append("*\n")
                        .append(ann.getContent()).append("\n\n");
                count++;
            }

            return response.toString();

        } catch (Exception e) {
            return "❌ Errore nel recuperare gli annunci.";
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setParseMode("Markdown");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}