package eu.foxxbl.x4.gameparser.shady;

import eu.foxxbl.x4.gameparser.shady.config.ShadySearchConfig;
import eu.foxxbl.x4.gameparser.shady.ui.application.SpringBootJavaFxApplication;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ShadySearchConfig.class)
public class ShadySearchApplication {


  public static void main(String[] args) {
    Application.launch(SpringBootJavaFxApplication.class, args);
  }

}
