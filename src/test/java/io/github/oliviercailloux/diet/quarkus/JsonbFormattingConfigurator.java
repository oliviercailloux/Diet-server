package io.github.oliviercailloux.diet.quarkus;

import io.quarkus.jsonb.JsonbConfigCustomizer;
import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.inject.Singleton;
import javax.json.bind.JsonbConfig;

@Alternative
@Priority(1)
@Singleton
public class JsonbFormattingConfigurator implements JsonbConfigCustomizer {

	@Override
	public void customize(JsonbConfig config) {
		config.withFormatting(true);
	}
}