package io.spring.demo.streaming;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "streaming")
public class StreamingServiceConfigProps {
	private String quoteServiceUrl;

	public String getQuoteServiceUrl() {
		return quoteServiceUrl;
	}

	public void setQuoteServiceUrl(String quoteServiceUrl) {
		this.quoteServiceUrl = quoteServiceUrl;
	}
}