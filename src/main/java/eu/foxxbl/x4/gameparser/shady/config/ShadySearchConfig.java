package eu.foxxbl.x4.gameparser.shady.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "shady-search")
public record ShadySearchConfig(boolean filteredStreamSearchEnabled, String uiTitle) {


}
