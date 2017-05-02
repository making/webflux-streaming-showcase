package io.spring.demo.streaming;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

@Configuration
@Profile("cloud")
public class CloudConfig extends AbstractCloudConfig {

	@Bean
	public MongoDbFactory mongoDbFactory() {
		return connectionFactory().mongoDbFactory();
	}

	@Bean
	public ConnectionString connectionString() throws Exception {
		String connectionString = connectionString(System.getenv("VCAP_SERVICES"));
		return new ConnectionString(connectionString);
	}

	@Bean(destroyMethod = "close")
	public MongoClient reactiveStreamsMongoClient() throws Exception {
		return MongoClients.create(connectionString());
	}

	@Bean
	public SimpleReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory(
			MongoClient mongo) throws Exception {
		return new SimpleReactiveMongoDatabaseFactory(mongo,
				connectionString().getDatabase());
	}

	private String connectionString(String vcapServices) throws Exception {
		return new ObjectMapper().readValue(vcapServices, JsonNode.class)
				.findParents("credentials").stream()
				.flatMap(n -> StreamSupport.stream(
						Spliterators.spliteratorUnknownSize(
								n.get("credentials").elements(), Spliterator.ORDERED),
						false))
				.filter(n -> n.asText().startsWith("mongodb://")).map(JsonNode::asText)
				.findFirst().orElseThrow(
						() -> new IllegalStateException("No mongodb service is bound!"));
	}
}
