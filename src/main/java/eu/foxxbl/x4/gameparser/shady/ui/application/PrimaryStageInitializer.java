package eu.foxxbl.x4.gameparser.shady.ui.application;

import eu.foxxbl.x4.gameparser.shady.config.ShadySearchConfig;
import eu.foxxbl.x4.gameparser.shady.ui.controller.MainWindow;
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

  public PrimaryStageInitializer(ShadySearchConfig shadySearchConfig, FxWeaver fxWeaver) {
    this.applicationTitle = shadySearchConfig.uiTitle();
    this.fxWeaver = fxWeaver;
    this.programIcon =  new Image(getClass().getClassLoader().getResourceAsStream(SHADY_SEARCH_ICO));
  }

  @Override
  public void onApplicationEvent(SpringBootJavaFxApplication.StageReadyEvent event) {
    Stage stage = event.getStage();
    Parent root = fxWeaver.loadView(MainWindow.class);
    Scene scene = new Scene(root, 1200, 1080);

    stage.setScene(scene);
    stage.getIcons().add(programIcon);
    stage.setTitle(applicationTitle);
    stage.centerOnScreen();

    stage.show();
    stage.setAlwaysOnTop(true);
    stage.setAlwaysOnTop(false);

  }
}
