package com.griddynamics.jagger.jaas.rest;

import com.griddynamics.jagger.jaas.exceptions.ResourceAlreadyExistsException;
import com.griddynamics.jagger.jaas.exceptions.ResourceNotFoundException;
import com.griddynamics.jagger.jaas.service.TestEnvironmentService;
import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping(value = "/envs")
public class TestEnvironmentRestController extends AbstractController {

    //private static final Logger LOGGER = LoggerFactory.getLogger(TestEnvironmentRestController.class);

    private TestEnvironmentService testEnvService;

    @Autowired
    public TestEnvironmentRestController(TestEnvironmentService testEnvironmentService) {
        this.testEnvService = testEnvironmentService;
    }

    @GetMapping(value = "/{envId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TestEnvironmentEntity> getTestEnvironment(@PathVariable String envId) {
        return produceGetResponse(testEnvService, function -> testEnvService.read(envId));
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TestEnvironmentEntity>> getTestEnvironments() {
        return produceGetResponse(testEnvService, function -> testEnvService.readAll());
    }

    @PutMapping(value = "/{envId}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateTestEnvironment(@PathVariable String envId, @RequestBody TestEnvironmentEntity testEnv) {
        if (!testEnvService.exists(envId))
            throw ResourceNotFoundException.getTestEnvResourceNfe();

        testEnv.setEnvironmentId(envId);
        testEnvService.update(testEnv);
        return ResponseEntity.accepted().build();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createTestEnvironment(@RequestBody TestEnvironmentEntity testEnv) {
        if (testEnvService.exists(testEnv.getEnvironmentId()))
            throw new ResourceAlreadyExistsException("Test Environment", testEnv.getEnvironmentId());

        testEnvService.create(testEnv);
        return ResponseEntity.created(fromCurrentRequest().path("/{envId}").buildAndExpand(testEnv.getEnvironmentId()).toUri()).build();
    }
}
