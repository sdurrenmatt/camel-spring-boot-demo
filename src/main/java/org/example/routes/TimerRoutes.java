package org.example.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Example of timer-based processing with Apache Camel.
 * <p>
 * This class demonstrates how to use Camel's Timer component to initiate actions at specific intervals or repetitions.
 * The routes within this class showcase a simple "Hello world" message logging and a random number generation
 * scenario. These examples can serve as the basis for more complex timer-based integrations.
 * </p>
 *
 * <p>
 * The Timer component allows specifying various parameters like repeat count, delay, and metadata inclusion. These
 * parameters are configured directly in the route URIs.
 * </p>
 *
 * @author Steven Dürrenmatt
 * @see <a href="https://camel.apache.org/components/timer-component.html">Timer Component</a>
 * @see <a href="https://camel.apache.org/components/log-component.html">Log Component</a>
 * @see <a href="https://camel.apache.org/components/languages/simple-language.html">Simple Language</a>
 */
@Component
public class TimerRoutes extends RouteBuilder {

    @Override
    public void configure() {
        // This route triggers once and logs "Hello world!" with detailed exchange information.
        from("timer:hello?repeatCount=1&delay=-1")
            .setBody(constant("Hello world!"))
            .to("log:org.example.routes?showAll=true");

        // This route triggers 10 times, generating a random number (0-9) for each invocation,
        // and logs the iteration count alongside the random number.
        from("timer:prng?repeatCount=10&includeMetadata=true")
            .log("i=${exchangeProperty.CamelTimerCounter} n=${random(10)}");
    }
}
