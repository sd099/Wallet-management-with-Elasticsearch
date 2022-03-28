package org.paytm.milestone2._Milestone2.flink;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.elasticsearch.ActionRequestFailureHandler;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;

import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;

import java.util.*;

@Component
public class FlinkMessage {

        @EventListener(ApplicationReadyEvent.class)
        public static void kafkaFlinkElasticsearch() throws Exception {

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);

        SourceFunction<String> producer = readFromKafka();

        DataStream<String> stream = env.addSource(producer);
        stream.print();

        ElasticsearchSink<String> consumer = writeToElastic();

        stream.addSink(consumer);

        env.execute();
    }

    public static FlinkKafkaConsumer<String> readFromKafka() {

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("group.id", "test");

        FlinkKafkaConsumer<String> flinkKafkaConsumer = new FlinkKafkaConsumer<>("testtopic", new SimpleStringSchema(), properties);
        return  flinkKafkaConsumer;
    }

    public static ElasticsearchSink<String> writeToElastic() {

        List<HttpHost> httpHosts = new ArrayList<>();
        httpHosts.add(new HttpHost("127.0.0.1", 9200, "http"));

        ElasticsearchSink.Builder<String> esSinkBuilder = new ElasticsearchSink.Builder<>(
                httpHosts,
                new ElasticsearchSinkFunction<String>() {
                    public IndexRequest createIndexRequest(String element) {

                        String arr[] = element.split(" ",7);
                        String amount = arr[0];
                        String payerNumber = arr[4];
                        String payeeNumber = arr[6];

                        Map<String, String> json = new HashMap<>();
                        json.put("amount", amount);
                        json.put("payerNumber", payerNumber);
                        json.put("payeeNumber", payeeNumber);

                        return Requests.indexRequest()
                                .index("testing2")
                                .type("testing")
                                .source(json);
                    }

                    @Override
                    public void process(String element, RuntimeContext ctx, RequestIndexer indexer) {
                        indexer.add(createIndexRequest(element));
                    }
                }
        );

        esSinkBuilder.setFailureHandler((ActionRequestFailureHandler) (actionRequest, throwable, i, requestIndexer) -> System.out.println("Error!"));

        esSinkBuilder.setBulkFlushMaxActions(1); //Instruct sink to emit after every element

        esSinkBuilder.setBulkFlushInterval(1000);


        return esSinkBuilder.build();
    }

}




