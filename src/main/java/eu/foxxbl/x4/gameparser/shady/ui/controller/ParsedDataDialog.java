package eu.foxxbl.x4.gameparser.shady.ui.controller;

import static eu.foxxbl.x4.gameparser.shady.ui.application.PrimaryStageInitializer.SHADY_SEARCH_ICO;

import eu.foxxbl.x4.gameparser.shady.model.result.ShadyGuy;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;


@Component
@FxmlView
public class ParsedDataDialog {

  private Stage stage;

  @FXML
  private Button closeButton;
  @FXML
  private VBox dialog;

  @FXML
  public TableView<ShadyGuy> blackMarketeerTable;

  @FXML
  private TableColumn<ShadyGuy, String> stationCode;
  @FXML
  private TableColumn<ShadyGuy, String> stationMacro;

  @FXML
  private TableColumn<ShadyGuy, String> blackMarketeerName;

  @FXML
  private TableColumn<ShadyGuy, Number> voiceLeaks;

  @FXML
  private TableColumn<ShadyGuy, String> status;

  @FXML
  private Label sectorName;


  @FXML
  public void initialize(List<ShadyGuy> shadyGuyList, String sector) {
    if (stage == null) {
      stage = new Stage();
      stage.setScene(new Scene(dialog));
      stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(SHADY_SEARCH_ICO))));
      stage.setTitle("Found Black Marketeers and voice leaks for sector");
      stage.setAlwaysOnTop(true);
      closeButton.setOnAction(
          actionEvent -> stage.close()
      );
    }
    updateAndShow(shadyGuyList, sector);
  }

  public void updateAndShow(List<ShadyGuy> shadyGuyList, String sector) {

    sectorName.setText(sector);
    initializeBlackMarketeerTable(shadyGuyList);
    stage.show();
  }

  private void initializeBlackMarketeerTable(List<ShadyGuy> shadyGuyList) {
    stationCode.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStationCode()));
    stationMacro.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStationMacro()));
    blackMarketeerName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
    voiceLeaks.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getVoiceLeaks()));
    status.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().getValue()));
    ObservableList<ShadyGuy> observableMapSectorList = FXCollections.observableArrayList(shadyGuyList);
    blackMarketeerTable.setItems(observableMapSectorList);
    blackMarketeerTable.setRowFactory(tv -> new TableRow<>() {
      @Override
      protected void updateItem(ShadyGuy shadyGuy, boolean empty) {
        super.updateItem(shadyGuy, empty);
        if (shadyGuy == null || empty) {
          setStyle("");
        } else {
          setStyle(switch (shadyGuy.getStatus()) {
            case ACTIVE -> "-fx-background-color: lightcyan;";
            case INACTIVE -> "-fx-background-color: lightgreen;";
            case NONE -> "-fx-background-color: lightgray;";
          });
        }
      }
    });

  }


  public void show() {
    stage.show();
  }

}
