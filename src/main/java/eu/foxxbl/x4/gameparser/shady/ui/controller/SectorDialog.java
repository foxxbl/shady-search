package eu.foxxbl.x4.gameparser.shady.ui.controller;

import static eu.foxxbl.x4.gameparser.shady.ui.application.PrimaryStageInitializer.SHADY_SEARCH_ICO;

import eu.foxxbl.x4.gameparser.shady.model.parse.BlackMarketeer;
import eu.foxxbl.x4.gameparser.shady.model.ui.MapSector;
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
public class SectorDialog {

  private Stage stage;

  @FXML
  private Button closeButton;
  @FXML
  private VBox dialog;

  @FXML
  public TableView<BlackMarketeer> blackMarketeerTable;

  @FXML
  private TableColumn<BlackMarketeer, String> stationCode;
  @FXML
  private TableColumn<BlackMarketeer, String> stationMacro;

  @FXML
  private TableColumn<BlackMarketeer, String> blackMarketeerName;

  @FXML
  private TableColumn<BlackMarketeer, Number> voiceLeaks;

  @FXML
  private TableColumn<BlackMarketeer, String> status;

  @FXML
  private Label sectorName;


  @FXML
  public void initialize(MapSector mapSector) {
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
    updateAndShow(mapSector);
  }

  public void updateAndShow(MapSector mapSector) {

    sectorName.setText(mapSector.sectorName());
    initializeBlackMarketeerTable(mapSector.blackMarketeerList());
    stage.show();
  }

  private void initializeBlackMarketeerTable(List<BlackMarketeer> blackMarketeerList) {
    stationCode.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStationCode()));
    stationMacro.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStationMacro()));
    blackMarketeerName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
    voiceLeaks.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getVoiceLeaks()));
    status.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().getValue()));
    ObservableList<BlackMarketeer> observableMapSectorList = FXCollections.observableArrayList(blackMarketeerList);
    blackMarketeerTable.setItems(observableMapSectorList);
    blackMarketeerTable.setRowFactory(tv -> new TableRow<>() {
      @Override
      protected void updateItem(BlackMarketeer blackMarketeer, boolean empty) {
        super.updateItem(blackMarketeer, empty);
        if (blackMarketeer == null || empty) {
          setStyle("");
        } else {
          setStyle(switch (blackMarketeer.getStatus()) {
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
