package org.example.routes;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.apache.camel.util.FileUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * Unit tests for file processing routes defined in the {@link FileRoutes} class.
 * <p>
 * This test class uses Apache Camel and Spring Boot testing tools to simulate file processing.
 * The tests make sure input files are processed and transformed as expected, with results sent to the output directory.
 * Mock endpoints are used to intercept messages and validate the expected outputs.
 * </p>
 *
 * @author Steven Dürrenmatt
 * @see <a href="https://camel.apache.org/manual/testing.html">Apache Camel Testing</a>
 * @see <a href="https://camel.apache.org/components/mock-component.html">Mock Component</a>
 */
@CamelSpringBootTest
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpoints("file:*")
class FileRoutesTest {

    @Value("${file.in.dir}")
    private String inputDir;

    @Value("${file.out.dir}")
    private String outputDir;

    @EndpointInject("mock:file:{{file.out.dir}}")
    private MockEndpoint mockFileOutput;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Value("classpath:data/prices.json")
    private Resource pricesJson;

    @Value("classpath:data/altcoin-prices.json")
    private Resource altcoinPricesJson;

    @Value("classpath:data/prices.yml")
    private Resource pricesYml;

    @AfterEach
    void cleanUp() {
        FileUtil.removeDir(new File(inputDir));
        FileUtil.removeDir(new File(outputDir));
    }

    @Test
    void testFilterAltcoinPricesRoute() throws Exception {
        // Read the content of 'prices.json' and 'altcoin-prices.json' as strings
        String pricesJsonStr = StreamUtils.copyToString(pricesJson.getInputStream(), StandardCharsets.UTF_8);
        String altcoinPricesJsonStr = StreamUtils.copyToString(altcoinPricesJson.getInputStream(), StandardCharsets.UTF_8);

        // Set expectations for the mock endpoint
        mockFileOutput.expectedMessageCount(1);

        // Send the contents of 'prices.json' to the route for processing
        producerTemplate.sendBody("file:{{file.in.dir}}?fileName=prices.json", pricesJsonStr);

        // Assert that the mock endpoint received the expected message
        mockFileOutput.assertIsSatisfied();

        // Compare the transformed output with the expected JSON content
        JSONAssert.assertEquals(altcoinPricesJsonStr, mockFileOutput.getExchanges().getFirst().getMessage().getBody(String.class), true);
    }

    @Test
    void testYmlToJsonRoute() throws Exception {
        // Read the content of 'prices.yml' and 'prices.json' as strings
        String pricesYmlStr = StreamUtils.copyToString(pricesYml.getInputStream(), StandardCharsets.UTF_8);
        String pricesJsonStr = StreamUtils.copyToString(pricesJson.getInputStream(), StandardCharsets.UTF_8);

        // Set expectations for the mock endpoint
        mockFileOutput.expectedMessageCount(1);
        mockFileOutput.expectedHeaderReceived(Exchange.FILE_NAME, "prices.json");

        // Send the contents of 'prices.yml' to the route for processing
        producerTemplate.sendBody("file:{{file.in.dir}}?fileName=prices.yml", pricesYmlStr);

        // Assert that the mock endpoint received the expected message and header
        mockFileOutput.assertIsSatisfied();

        // Compare the transformed output with the expected JSON content
        JSONAssert.assertEquals(pricesJsonStr, mockFileOutput.getExchanges().getFirst().getMessage().getBody(String.class), true);
    }

}