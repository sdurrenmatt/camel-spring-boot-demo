package org.example.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Example of file processing with Apache Camel.
 * <p>
 * This route reads files from the input directory, logs the file content,
 * and is designed to be easily extended for more complex file processing
 * logic (e.g., transformation, moving files, etc.).
 * </p>
 * <p>
 * Configuration: The directory paths are read from the application properties
 * (file.in.dir, file.out.dir) and are expected to be configured as part of
 * the application's setup. The noop=true option ensures that files are not
 * deleted after being processed.
 * </p>
 *
 * @author Steven Dürrenmatt
 */
@Component
public class FileRoutes extends RouteBuilder {

    @Override
    public void configure() {
        from("file:{{file.in.dir}}?fileName=prices.json")
            .log("${body}");
    }
}