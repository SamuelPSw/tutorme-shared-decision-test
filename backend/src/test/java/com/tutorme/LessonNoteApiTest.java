package com.tutorme;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LessonNoteApiTest {
    @Autowired
    private TestRestTemplate http;

    @Test
    void createsListsAndApprovesNotesWithoutMixingFamilies() {
        JsonNode first = create(1, "Algebra");
        JsonNode second = create(2, "Geometry");
        long id = first.path("id").asLong();

        assertThat(id).isPositive();
        assertThat(first.path("familyId").asInt()).isEqualTo(1);
        assertThat(first.path("title").asText()).isEqualTo("Algebra");
        assertThat(first.path("content").asText()).isEqualTo("Lesson content");
        assertThat(first.path("approved").isBoolean()).isTrue();
        assertThat(first.path("approved").asBoolean()).isFalse();
        assertThat(Instant.parse(first.path("createdAt").asText())).isBeforeOrEqualTo(Instant.now());

        assertThat(http.getForObject("/api/notes", JsonNode.class)).containsExactly(first, second);
        assertThat(http.getForObject("/api/families/1/notes", JsonNode.class)).containsExactly(first);
        assertThat(http.getForObject("/api/families/2/notes", JsonNode.class)).containsExactly(second);
        assertThat(http.getForObject("/api/families/99/notes", JsonNode.class).isEmpty()).isTrue();

        String approvalPath = "/api/notes/" + id + "/approve";
        var approval = http.exchange(approvalPath, HttpMethod.PUT, HttpEntity.EMPTY, JsonNode.class);
        assertThat(approval.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode approved = approval.getBody();
        assertThat(approved.path("approved").asBoolean()).isTrue();
        assertThat(approved.path("id")).isEqualTo(first.path("id"));
        assertThat(http.getForObject("/api/families/1/notes", JsonNode.class)).containsExactly(approved);
        assertThat(http.exchange(approvalPath, HttpMethod.PUT, HttpEntity.EMPTY, JsonNode.class)
                .getBody()).isEqualTo(approved);
    }

    @Test
    void rejectsInvalidNotesAndMissingApprovalIds() {
        assertThat(http.postForEntity("/api/notes", Map.of("familyId", 3, "title", "Lesson",
                "content", "Text"), JsonNode.class).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(http.postForEntity("/api/notes", Map.of("familyId", 1, "title", " ",
                "content", "Text"), JsonNode.class).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(http.exchange("/api/notes/99999/approve", HttpMethod.PUT, HttpEntity.EMPTY,
                JsonNode.class).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void allowsFrontendCorsPreflightForApproval() {
        HttpHeaders headers = new HttpHeaders();
        headers.setOrigin("http://localhost:5173");
        headers.setAccessControlRequestMethod(HttpMethod.PUT);

        var response = http.exchange("/api/notes/1/approve", HttpMethod.OPTIONS,
                new HttpEntity<>(headers), Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getAccessControlAllowOrigin()).isEqualTo("http://localhost:5173");
        assertThat(response.getHeaders().getAccessControlAllowMethods()).contains(HttpMethod.PUT);
    }

    private JsonNode create(int familyId, String title) {
        var response = http.postForEntity("/api/notes", Map.of("familyId", familyId, "title", title,
                "content", "Lesson content"), JsonNode.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getBody();
    }
}
