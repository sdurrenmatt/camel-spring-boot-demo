package org.example.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.YAMLLibrary;
import org.springframework.stereotype.Component;

/**
 * Example of file processing with Apache Camel.
 * <p>
 * This class demonstrates how to read files from a directory, transform their content (e.g., filter JSON data or convert YAML to JSON),
 * and write the transformed content to a new file. The routes within this class can be easily extended to handle more complex
 * processing scenarios, such as moving or archiving files, adding additional transformations, or handling different data formats.
 * </p>
 *
 * <p>
 * The directory paths are read from the application properties
 * (file.in.dir, file.out.dir) and are expected to be configured as part of the application's setup.
 * </p>
 *
 * @author Steven Dürrenmatt
 * @see <a href="https://camel.apache.org/components/file-component.html">File Component</a>
 * @see <a href="https://camel.apache.org/components/eips/marshal-eip.html">Marshal EIP</a>
 * @see <a href="https://camel.apache.org/components/dataformats/jackson-dataformat.html">Jackson Data Format</a>
 * @see <a href="https://camel.apache.org/components/dataformats/snakeYaml-dataformat.html">SnakeYAML Data Format</a>
 * @see <a href="https://camel.apache.org/components/languages/simple-language.html">Simple Language</a>
 * @see <a href="https://camel.apache.org/components/languages/jsonpath-language.html">JSONPath Language</a>
 */
@Component
public class FileRoutes extends RouteBuilder {

    @Override
    public void configure() {
        // This route filters a JSON file by excluding entries with the symbol 'BTCUSDT'
        // and writes the filtered data to a new file named 'altcoins-prices.json'.
        from("file:{{file.in.dir}}?fileName=prices.json&delete=true")
            .setBody(jsonpath("$[?(@.symbol != 'BTCUSDT')]"))
            .marshal().json(true)
            .to("file:{{file.out.dir}}?fileName=altcoins-prices.json");

        // This route converts all YAML files in the input directory to JSON files
        // and saves them to the output directory with a '.json' extension.
        from("file:{{file.in.dir}}?includeExt=yaml,yml&delete=true")
            .unmarshal().yaml(YAMLLibrary.SnakeYAML)
            .marshal().json(true)
            .setHeader(Exchange.FILE_NAME, simple("${file:name.noext}.json"))
            .to("file:{{file.out.dir}}");
    }
}