package org.example.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MyRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("timer:foo?period=1000")
            .setBody(constant("Hello world!"))
            .to("log:org.example.routes?level=INFO");
    }
}