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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

    public String createMeetLink(String sessionName, Date startDate, Date endDate) {
        if (serviceAccountJson == null || serviceAccountJson.isBlank()) {
            System.err.println("[GoogleMeet] No service account JSON configured — skipping Meet link generation");
            return null;
        }
        try {
            Calendar service = buildCalendarService();

            Event event = new Event()
                .setSummary(sessionName)
                .setStart(new EventDateTime().setDateTime(new DateTime(startDate)))
                .setEnd(new EventDateTime().setDateTime(new DateTime(endDate)));

            ConferenceSolutionKey solutionKey = new ConferenceSolutionKey().setType("hangoutsMeet");
            CreateConferenceRequest conferenceRequest = new CreateConferenceRequest()
                .setRequestId(UUID.randomUUID().toString())
                .setConferenceSolutionKey(solutionKey);
            event.setConferenceData(new ConferenceData().setCreateRequest(conferenceRequest));

            Event created = service.events()
                .insert("primary", event)
                .setConferenceDataVersion(1)
                .execute();

            List<EntryPoint> entryPoints = created.getConferenceData().getEntryPoints();
            if (entryPoints != null) {
                return entryPoints.stream()
                    .filter(e -> "video".equals(e.getEntryPointType()))
                    .findFirst()
                    .map(EntryPoint::getUri)
                    .orElse(null);
            }
        } catch (Exception e) {
            System.err.println("[GoogleMeet] Failed to create Meet link: " + e.getMessage());
        }
        return null;
    }
}
