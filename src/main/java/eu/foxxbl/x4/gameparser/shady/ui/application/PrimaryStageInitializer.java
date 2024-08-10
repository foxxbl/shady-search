package eu.foxxbl.x4.gameparser.shady.ui.application;

import atlantafx.base.theme.PrimerLight;
import eu.foxxbl.x4.gameparser.shady.config.ShadySearchConfig;
import eu.foxxbl.x4.gameparser.shady.ui.controller.MainWindow;
import java.util.Objects;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PrimaryStageInitializer implements ApplicationListener<SpringBootJavaFxApplication.StageReadyEvent> {

  public static final String SHADY_SEARCH_ICO = "shady-search.png";
  private final Image programIcon;
  private final String applicationTitle;
  private final FxWeaver fxWeaver;
  private final int width;
  private final int height;

  public PrimaryStageInitializer(ShadySearchConfig shadySearchConfig, FxWeaver fxWeaver) {
    this.applicationTitle = shadySearchConfig.ui().title();
    this.width = shadySearchConfig.ui().width();
    this.height = shadySearchConfig.ui().height();
    this.fxWeaver = fxWeaver;
    this.programIcon = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(SHADY_SEARCH_ICO)));
  }

  @Override
  public void onApplicationEvent(SpringBootJavaFxApplication.StageReadyEvent event) {
    Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

    Stage stage = event.getStage();
    Parent root = fxWeaver.loadView(MainWindow.class);
    Scene scene = new Scene(root, width, height);

    stage.setScene(scene);
    stage.getIcons().add(programIcon);
    stage.setTitle(applicationTitle);
    stage.centerOnScreen();

    stage.show();
    stage.setAlwaysOnTop(true);
    stage.setAlwaysOnTop(false);

  }
}
