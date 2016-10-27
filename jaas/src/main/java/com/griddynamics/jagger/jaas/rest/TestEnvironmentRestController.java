package com.griddynamics.jagger.jaas.rest;

import com.griddynamics.jagger.jaas.exceptions.ResourceAlreadyExistsException;
import com.griddynamics.jagger.jaas.exceptions.ResourceNotFoundException;
import com.griddynamics.jagger.jaas.exceptions.TestEnvironmentNoSessionException;
import com.griddynamics.jagger.jaas.exceptions.TestEnvironmentSessionNotFoundException;
import com.griddynamics.jagger.jaas.service.TestEnvironmentService;
import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping(value = "/envs")
public class TestEnvironmentRestController extends AbstractController {

    public static final String ENVIRONMENT_SESSION_COOKIE = "Environment-Session";

    @Value("${environments.ttl.minutes}")
    private int environmentsTtlMinutes;

    private TestEnvironmentService testEnvService;

    @Autowired
    public TestEnvironmentRestController(TestEnvironmentService testEnvironmentService) {
        this.testEnvService = testEnvironmentService;
    }

    @GetMapping(value = "/{envId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TestEnvironmentEntity> getTestEnvironment(@PathVariable String envId, HttpServletResponse response) {
        ResponseEntity<TestEnvironmentEntity> responseEntity = produceGetResponse(testEnvService, function -> testEnvService.read(envId));
        if (responseEntity.getStatusCode() == OK)
            setExpiresHeader(response, responseEntity.getBody());
        return responseEntity;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TestEnvironmentEntity>> getTestEnvironments() {
        return produceGetResponse(testEnvService, function -> testEnvService.readAll());
    }

    @PutMapping(value = "/{envId}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateTestEnvironment(@PathVariable String envId, @RequestBody TestEnvironmentEntity testEnv,
                                                   HttpServletRequest request, HttpServletResponse response) {
        if (!testEnvService.exists(envId))
            throw ResourceNotFoundException.getTestEnvResourceNfe();

        String sessionId = getSessionCookie(request);
        if (sessionId == null)
            throw new TestEnvironmentNoSessionException();
        if (!testEnvService.existsWithSessionId(envId, sessionId))
            throw new TestEnvironmentSessionNotFoundException(envId, sessionId);

        testEnv.setEnvironmentId(envId);
        TestEnvironmentEntity updated = testEnvService.update(testEnv);
        setExpiresHeader(response, updated);
        setSessionCookie(response, updated, environmentsTtlMinutes * 60);
        return ResponseEntity.accepted().build();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createTestEnvironment(@RequestBody TestEnvironmentEntity testEnv, HttpServletResponse response) {
        if (testEnvService.exists(testEnv.getEnvironmentId()))
            throw new ResourceAlreadyExistsException("Test Environment", testEnv.getEnvironmentId());

        TestEnvironmentEntity created = testEnvService.create(testEnv);
        setExpiresHeader(response, created);
        setSessionCookie(response, created, environmentsTtlMinutes * 60);
        return ResponseEntity.created(fromCurrentRequest().path("/{envId}")
                .buildAndExpand(testEnv.getEnvironmentId()).toUri()).build();
    }

    private void setExpiresHeader(HttpServletResponse response, TestEnvironmentEntity testEnv) {
        response.addHeader("Environment-Expires", getFormattedExpirationDate(testEnv));
    }

    private void setSessionCookie(HttpServletResponse response, TestEnvironmentEntity testEnv, int cookieMaxAgeSeconds) {
        Cookie cookie = new Cookie(ENVIRONMENT_SESSION_COOKIE, testEnv.getSessionId());
        cookie.setMaxAge(cookieMaxAgeSeconds);
        response.addCookie(cookie);
    }

    private void updateSessionCookie(HttpServletRequest request, HttpServletResponse response, TestEnvironmentEntity testEnv,
                                     int cookieMaxAgeSeconds) {

        Cookie cookie = new Cookie(ENVIRONMENT_SESSION_COOKIE, testEnv.getSessionId());
        cookie.setMaxAge(cookieMaxAgeSeconds);
        response.addCookie(cookie);
    }

    private String getSessionCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(ENVIRONMENT_SESSION_COOKIE)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String getFormattedExpirationDate(TestEnvironmentEntity testEnv) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(testEnv.getExpirationTimestamp()), ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss O"));
    }
}
