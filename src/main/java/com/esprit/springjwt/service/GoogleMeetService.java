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
import java.util.stream.Collectors;

@Service
public class GoogleMeetService {

    @Value("${google.service.account.json:}")
    private String serviceAccountJson;

    private Calendar buildCalendarService() throws Exception {
        GoogleCredentials credentials = GoogleCredentials
            .fromStream(new ByteArrayInputStream(serviceAccountJson.getBytes(StandardCharsets.UTF_8)))
            .createScoped(Collections.singleton(CalendarScopes.CALENDAR));

        return new Calendar.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JacksonFactory.getDefaultInstance(),
            new HttpCredentialsAdapter(credentials)
        ).setApplicationName("9antra-platform").build();
    }

    /**
     * Creates a Google Calendar event with a Meet link and invites all attendees.
     * Returns a map with "meetLink" and "calendarEventId".
     */
    public Map<String, String> createSessionEvent(String sessionName, String description,
                                                   Date startDate, Date endDate,
                                                   List<String> attendeeEmails) {
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

            // Add Meet conference
            event.setConferenceData(new ConferenceData().setCreateRequest(
                new CreateConferenceRequest()
                    .setRequestId(UUID.randomUUID().toString())
                    .setConferenceSolutionKey(new ConferenceSolutionKey().setType("hangoutsMeet"))
            ));

            // Add attendees (coach + students)
            if (attendeeEmails != null && !attendeeEmails.isEmpty()) {
                List<EventAttendee> attendees = attendeeEmails.stream()
                    .filter(e -> e != null && !e.isBlank())
                    .distinct()
                    .map(email -> new EventAttendee().setEmail(email))
                    .collect(Collectors.toList());
                event.setAttendees(attendees);
            }

            Event created = service.events()
                .insert("primary", event)
                .setConferenceDataVersion(1)
                .setSendUpdates("all") // sends invites to attendees
                .execute();

            // Extract Meet link
            List<EntryPoint> entryPoints = created.getConferenceData().getEntryPoints();
            if (entryPoints != null) {
                entryPoints.stream()
                    .filter(e -> "video".equals(e.getEntryPointType()))
                    .findFirst()
                    .ifPresent(e -> result.put("meetLink", e.getUri()));
            }

            result.put("calendarEventId", created.getId());
            System.out.println("[GoogleMeet] Event created: " + created.getId() + " | Meet: " + result.get("meetLink"));

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
            Event event = service.events().get("primary", eventId).execute();
            event.setSummary(sessionName);
            if (description != null) event.setDescription(description);
            event.setStart(new EventDateTime().setDateTime(new DateTime(startDate)));
            event.setEnd(new EventDateTime().setDateTime(new DateTime(endDate)));
            service.events().update("primary", eventId, event).setSendUpdates("all").execute();
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
            service.events().delete("primary", eventId).setSendUpdates("all").execute();
            System.out.println("[GoogleMeet] Event deleted: " + eventId);
        } catch (Exception e) {
            System.err.println("[GoogleMeet] Failed to delete event " + eventId + ": " + e.getMessage());
        }
    }

    /** Backward-compatible method used by old code paths. */
    public String createMeetLink(String sessionName, Date startDate, Date endDate) {
        Map<String, String> result = createSessionEvent(sessionName, null, startDate, endDate, null);
        return result.get("meetLink");
    }
}
