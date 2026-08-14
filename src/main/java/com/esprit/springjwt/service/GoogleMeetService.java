package com.esprit.springjwt.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class GoogleMeetService {

    @Value("${google.service.account.json:}")
    private String serviceAccountJson;

    @Value("${google.calendar.id:primary}")
    private String calendarId;

    private Calendar buildCalendarService() throws Exception {
        String cleanJson = serviceAccountJson.replace("\\n", "\n");
        GoogleCredentials credentials = GoogleCredentials
            .fromStream(new ByteArrayInputStream(cleanJson.getBytes(StandardCharsets.UTF_8)))
            .createScoped(Collections.singleton(CalendarScopes.CALENDAR));

        return new Calendar.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JacksonFactory.getDefaultInstance(),
            new HttpCredentialsAdapter(credentials)
        ).setApplicationName("9antra-platform").build();
    }

    /**
     * Creates a Google Calendar event. If manualMeetLink is provided, it's attached as the event
     * location. Returns a map with "calendarEventId" (and "meetLink" echoing manualMeetLink, if any).
     *
     * Note: this does NOT request an auto-generated Google Meet conference, and does NOT add
     * attendees — both are Workspace-only capabilities (domain-wide delegation) when performed by
     * a plain service account; attempting either causes the entire event insert to fail with the
     * free-tier setup this project currently uses. Attendee invites are instead sent separately
     * via EmailService's ICS invite email.
     */
    public Map<String, String> createSessionEvent(String sessionName, String description,
                                                   Date startDate, Date endDate, String manualMeetLink) {
        Map<String, String> result = new HashMap<>();

        if (serviceAccountJson == null || serviceAccountJson.isBlank()) {
            System.err.println("[GoogleMeet] No service account JSON configured — skipping");
            return result;
        }

        try {
            Calendar service = buildCalendarService();

            Event event = new Event()
                .setSummary(sessionName)
                .setDescription(description != null ? description : "")
                .setStart(new EventDateTime().setDateTime(new DateTime(startDate)))
                .setEnd(new EventDateTime().setDateTime(new DateTime(endDate)));

            if (manualMeetLink != null && !manualMeetLink.isBlank()) {
                event.setLocation(manualMeetLink);
            }

            Event created = service.events().insert(calendarId, event).execute();

            result.put("calendarEventId", created.getId());
            if (manualMeetLink != null && !manualMeetLink.isBlank()) {
                result.put("meetLink", manualMeetLink);
            }
            System.out.println("[GoogleMeet] Event created: " + created.getId());

        } catch (Exception e) {
            System.err.println("[GoogleMeet] Failed to create event: " + e.getMessage());
        }

        return result;
    }

    /**
     * Updates title, description, and time of an existing calendar event.
     */
    public void updateCalendarEvent(String eventId, String sessionName, String description,
                                     Date startDate, Date endDate) {
        if (serviceAccountJson == null || serviceAccountJson.isBlank() || eventId == null) return;
        try {
            Calendar service = buildCalendarService();
            Event event = service.events().get(calendarId, eventId).execute();
            event.setSummary(sessionName);
            if (description != null) event.setDescription(description);
            event.setStart(new EventDateTime().setDateTime(new DateTime(startDate)));
            event.setEnd(new EventDateTime().setDateTime(new DateTime(endDate)));
            service.events().update(calendarId, eventId, event).execute();
            System.out.println("[GoogleMeet] Event updated: " + eventId);
        } catch (Exception e) {
            System.err.println("[GoogleMeet] Failed to update event " + eventId + ": " + e.getMessage());
        }
    }

    /**
     * Deletes a calendar event (called when a session is deleted).
     */
    public void deleteCalendarEvent(String eventId) {
        if (serviceAccountJson == null || serviceAccountJson.isBlank() || eventId == null) return;
        try {
            Calendar service = buildCalendarService();
            service.events().delete(calendarId, eventId).execute();
            System.out.println("[GoogleMeet] Event deleted: " + eventId);
        } catch (Exception e) {
            System.err.println("[GoogleMeet] Failed to delete event " + eventId + ": " + e.getMessage());
        }
    }
}
