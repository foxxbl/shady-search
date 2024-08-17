package eu.foxxbl.x4.gameparser.shady.ui.controller;

import static eu.foxxbl.x4.gameparser.shady.ui.application.PrimaryStageInitializer.SHADY_SEARCH_ICO;

import eu.foxxbl.x4.gameparser.shady.model.parse.BlackMarketeer;
import eu.foxxbl.x4.gameparser.shady.model.ui.MapSector;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableRow;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;


@Component
@FxmlView
public class SectorDialog {

  private Stage stage;

  @FXML
  private MFXButton closeButton;
  @FXML
  private VBox dialog;

  @FXML
  public MFXTableView<BlackMarketeer> blackMarketeerTable;

  @FXML
  private MFXTableColumn<BlackMarketeer> stationCode;
  @FXML
  private MFXTableColumn<BlackMarketeer> stationMacro;

  @FXML
  private MFXTableColumn<BlackMarketeer> blackMarketeerName;

  @FXML
  private MFXTableColumn<BlackMarketeer> voiceLeaks;

  @FXML
  private MFXTableColumn<BlackMarketeer> status;

  @FXML
  private MFXTextField sectorName;


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
    sectorName.setBorder(Border.EMPTY);
    initializeBlackMarketeerTable(mapSector.blackMarketeerList());
    stage.show();
  }

  private void initializeBlackMarketeerTable(List<BlackMarketeer> blackMarketeerList) {
    stationCode.setComparator(Comparator.comparing(BlackMarketeer::getStationCode));
    stationMacro.setComparator(Comparator.comparing(BlackMarketeer::getStationMacro));
    blackMarketeerName.setComparator(Comparator.comparing(BlackMarketeer::getName));
    voiceLeaks.setComparator(Comparator.comparing(BlackMarketeer::getVoiceLeaks));
    status.setComparator(Comparator.comparing(BlackMarketeer::getStatus));

    stationCode.setRowCellFactory(mapSector -> new MFXTableRowCell<>(BlackMarketeer::getStationCode));
    stationMacro.setRowCellFactory(mapSector -> new MFXTableRowCell<>(BlackMarketeer::getStationMacro));
    blackMarketeerName.setRowCellFactory(mapSector -> new MFXTableRowCell<>(BlackMarketeer::getName));
    voiceLeaks.setRowCellFactory(mapSector -> new MFXTableRowCell<>(BlackMarketeer::getVoiceLeaks));
    status.setRowCellFactory(mapSector -> new MFXTableRowCell<>(BlackMarketeer::getStatusName));

    ObservableList<BlackMarketeer> observableMapSectorList = FXCollections.observableArrayList(blackMarketeerList);
    blackMarketeerTable.setItems(observableMapSectorList);

    blackMarketeerTable.setTableRowFactory(shadyGuy -> new MFXTableRow<>(blackMarketeerTable, shadyGuy) {
      @Override
      protected void updateRow(BlackMarketeer blackMarketeer) {
        super.updateRow(blackMarketeer);
        if (blackMarketeer == null) {
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
