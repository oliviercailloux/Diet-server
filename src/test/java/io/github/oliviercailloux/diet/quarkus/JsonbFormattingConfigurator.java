package io.github.oliviercailloux.diet.quarkus;

import io.quarkus.jsonb.JsonbConfigCustomizer;
import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Singleton;
import jakarta.json.bind.JsonbConfig;

@Alternative
@Priority(1)
@Singleton
public class JsonbFormattingConfigurator implements JsonbConfigCustomizer {

	@Override
	public void customize(JsonbConfig config) {
		config.withFormatting(true);
	}
}