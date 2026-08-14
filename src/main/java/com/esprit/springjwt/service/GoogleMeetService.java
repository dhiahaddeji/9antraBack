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
     * Creates a Google Calendar event and invites all attendees. If manualMeetLink is provided,
     * it's attached as the event location so attendees see it in the invite.
     * Returns a map with "calendarEventId" (and "meetLink" echoing manualMeetLink, if any).
     *
     * Note: this does NOT request an auto-generated Google Meet conference — Meet creation via
     * the Calendar API is a Workspace-only capability and requesting it on a free-tier calendar
     * causes the entire event insert to fail (not just the Meet link).
     */
    public Map<String, String> createSessionEvent(String sessionName, String description,
                                                   Date startDate, Date endDate,
                                                   List<String> attendeeEmails, String manualMeetLink) {
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

            // Add attendees (admin + coach + students)
            if (attendeeEmails != null && !attendeeEmails.isEmpty()) {
                List<EventAttendee> attendees = attendeeEmails.stream()
                    .filter(e -> e != null && !e.isBlank())
                    .distinct()
                    .map(email -> new EventAttendee().setEmail(email))
                    .collect(Collectors.toList());
                event.setAttendees(attendees);
            }

            Event created = service.events()
                .insert(calendarId, event)
                .setSendUpdates("all") // sends invites to attendees
                .execute();

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
            service.events().update(calendarId, eventId, event).setSendUpdates("all").execute();
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
            service.events().delete(calendarId, eventId).setSendUpdates("all").execute();
            System.out.println("[GoogleMeet] Event deleted: " + eventId);
        } catch (Exception e) {
            System.err.println("[GoogleMeet] Failed to delete event " + eventId + ": " + e.getMessage());
        }
    }

    /** Backward-compatible method used by old code paths. */
    public String createMeetLink(String sessionName, Date startDate, Date endDate) {
        Map<String, String> result = createSessionEvent(sessionName, null, startDate, endDate, null, null);
        return result.get("meetLink");
    }
}
