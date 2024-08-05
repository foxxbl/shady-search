package eu.foxxbl.x4.gameparser.shady.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "shady-search")
public record ShadySearchConfig(@NestedConfigurationProperty UiConfig ui, int maxSectors) {


}
