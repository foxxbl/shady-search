package eu.foxxbl.x4.gameparser.shady.ui.application;

import eu.foxxbl.x4.gameparser.shady.ShadySearchApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringBootJavaFxApplication extends Application {


  private ConfigurableApplicationContext applicationContext;

  @Override
  public void init() {

    this.applicationContext = new SpringApplicationBuilder()
        .sources(ShadySearchApplication.class)
        .run(getParameters().getRaw().toArray(new String[0]));
  }

  @Override
  public void start(Stage stage) {
    this.applicationContext.publishEvent(new StageReadyEvent(stage));
  }

  @Override
  public void stop() {
    this.applicationContext.close();
    Platform.exit();
  }

  public static class StageReadyEvent extends ApplicationEvent {

    public StageReadyEvent(Stage stage) {
      super(stage);
    }

    public Stage getStage() {
      return ((Stage) getSource());
    }
  }
}
