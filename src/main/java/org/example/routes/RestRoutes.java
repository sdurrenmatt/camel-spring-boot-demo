package org.example.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * This class demonstrates how to set up RESTful routes with Apache Camel.
 * <p>
 * It handles HTTP requests to fetch random pet images based on the type (dog or cat).
 * The routes transform the API responses using the Jolt transformation library to match a predefined pet format.
 * </p>
 *
 * <p>
 * The configuration also includes OpenAPI documentation setup for the "Adopt a Pet" API
 * and handles missing operations by using "mock" to simulate a successful response.
 * </p>
 *
 * @author Steven Dürrenmatt
 * @see <a href="https://camel.apache.org/manual/rest-dsl-openapi.html">REST DSL</a>
 * @see <a href="https://camel.apache.org/components/4.8.x/eips/choice-eip.html">Choice EIP</a>
 * @see <a href="https://camel.apache.org/components/http-component.html">HTTP Component</a>
 * @see <a href="https://camel.apache.org/components/jolt-component.html">Jolt Component</a>
 */
@Component
public class RestRoutes extends RouteBuilder {

    @Override
    public void configure() {
        restConfiguration()
            .clientRequestValidation(true);

        rest()
            .openApi()
            .specification("openapi/adopt-a-pet-api.yml")
            .missingOperation("mock");

        from("direct:getRandomPet")
            .choice()
                .when(header("type").isEqualTo("dog"))
                    .setHeader(Exchange.HTTP_URI, constant("https://dog.ceo/api/breeds/image/random?bridgeEndpoint=true"))
                    .to("http:dogApi")
                    .to("jolt:jolt/dog-to-pet.json?inputType=JsonString&outputType=JsonString")
                .when(header("type").isEqualTo("cat"))
                    .setHeader(Exchange.HTTP_URI, constant("https://api.thecatapi.com/v1/images/search"))
                    .to("http:catApi")
                    .to("jolt:jolt/cat-to-pet.json?inputType=JsonString&outputType=JsonString");
    }
}
