package org.example.routes;

import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

import static org.apache.camel.builder.Builder.constant;

/**
 * Unit tests for the RESTful routes defined in the {@link RestRoutes} class.
 * <p>
 * This test class uses Apache Camel and Spring Boot testing tools to simulate HTTP requests
 * and verify the responses returned by the routes. The tests ensure that random pet image requests
 * (for both dogs and cats) are processed correctly, with the appropriate transformation applied to the API responses.
 * Mock endpoints are used to intercept HTTP requests and validate the expected behavior.
 * </p>
 *
 * @author Steven Dürrenmatt
 * @see <a href="https://camel.apache.org/manual/testing.html">Apache Camel Testing</a>
 * @see <a href="https://camel.apache.org/components/mock-component.html">Mock Component</a>
 */
@CamelSpringBootTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpointsAndSkip("http:*")
class RestRoutesTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @EndpointInject("mock:http:dogApi")
    private MockEndpoint mockDogApi;

    @EndpointInject("mock:http:catApi")
    private MockEndpoint mockCatApi;

    @Value("classpath:data/pets/dog.json")
    private Resource dogJsonResource;

    @Value("classpath:data/pets/pet-dog.json")
    private Resource petDogJsonResource;

    @Value("classpath:data/pets/cat.json")
    private Resource catJsonResource;

    @Value("classpath:data/pets/pet-cat.json")
    private Resource petCatJsonResource;

    @Test
    void testGetRandomPetDog() throws Exception {
        String dogApiResponse = StreamUtils.copyToString(dogJsonResource.getInputStream(), StandardCharsets.UTF_8);
        String petDogExpectedResponse = StreamUtils.copyToString(petDogJsonResource.getInputStream(), StandardCharsets.UTF_8);

        mockDogApi.expectedMessageCount(1);
        mockDogApi.returnReplyBody(constant(dogApiResponse));

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/pets/surprise?type=dog", String.class);

        mockDogApi.assertIsSatisfied();
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(petDogExpectedResponse, response.getBody(), true);
    }

    @Test
    void testGetRandomPetCat() throws Exception {
        String catApiResponse = StreamUtils.copyToString(catJsonResource.getInputStream(), StandardCharsets.UTF_8);
        String petCatExpectedResponse = StreamUtils.copyToString(petCatJsonResource.getInputStream(), StandardCharsets.UTF_8);

        mockCatApi.expectedMessageCount(1);
        mockCatApi.returnReplyBody(constant(catApiResponse));

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/pets/surprise?type=cat", String.class);

        mockCatApi.assertIsSatisfied();
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(petCatExpectedResponse, response.getBody(), true);
    }
}