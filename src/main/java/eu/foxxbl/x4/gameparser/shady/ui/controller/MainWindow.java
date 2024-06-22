package eu.foxxbl.x4.gameparser.shady.ui.controller;

import eu.foxxbl.x4.gameparser.shady.model.entity.MapSectorEntity;
import eu.foxxbl.x4.gameparser.shady.model.entity.MapType;
import eu.foxxbl.x4.gameparser.shady.model.result.ShadyGuy;
import eu.foxxbl.x4.gameparser.shady.parse.GameParsingService;
import eu.foxxbl.x4.gameparser.shady.store.MapSectorStoreService;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView // equal to: @FxmlView("MainWindow.fxml")
public class MainWindow {

  private final FxControllerAndView<ParsedDataDialog, VBox> parsedDataDialog;
  private final List<MapSectorEntity> allMapSectors;
  private final GameParsingService gameParsingService;
  @FXML
  public Button loadFileButton;
  @FXML
  public Button parseSaveGame;
  @FXML
  public Window window;
  @FXML
  public Label selectedFilePathLabel;
  @FXML
  public TableView<MapSectorEntity> sectorTable;
  @FXML
  private TableColumn<MapSectorEntity, String> sectorName;
  @FXML
  private TableColumn<MapSectorEntity, String> mapType;
  @FXML
  private CheckBox splitCb;
  @FXML
  private CheckBox terranCb;

  @FXML
  private CheckBox pirateCb;
  @FXML
  private CheckBox boronCb;
  @FXML
  private CheckBox timelinesCb;

  private File selectedFile = null;
  private MapSectorEntity selectedMapSector = null;


  public MainWindow(FxControllerAndView<ParsedDataDialog, VBox> parsedDataDialog, MapSectorStoreService mapSectorStoreService, GameParsingService gameParsingService)  {
    this.parsedDataDialog = parsedDataDialog;
    this.allMapSectors = mapSectorStoreService.getAllMapSectors();
    this.gameParsingService = gameParsingService;

  }

  @FXML
  public void initialize() {
    initializeLoadFileButton();

    initializeMapSectorTable();

    initializeParseSaveGameButton();
  }

  private void initializeParseSaveGameButton() {
    parseSaveGame.setDisable(true);
    parseSaveGame.setOnAction(e -> {
      List<ShadyGuy> blackMarketeersList = gameParsingService.parseGameForBlackMarketeers(selectedFile, selectedMapSector);
      parsedDataDialog.getController().initialize(blackMarketeersList, selectedMapSector.getSectorName());
      parsedDataDialog.getController().show();
    });
  }

  private void initializeMapSectorTable() {

    sectorName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSectorName()));
    mapType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMapType().getFullName()));
    ObservableList<MapSectorEntity> observableMapSectorList = FXCollections.observableArrayList(allMapSectors);
    sectorTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    ObservableList<MapSectorEntity> selectedItems = sectorTable.getSelectionModel().getSelectedItems();

    selectedItems.addListener((ListChangeListener<MapSectorEntity>) change -> {
      ObservableList<? extends MapSectorEntity>  sectorList = change.getList();
      if (!sectorList.isEmpty()) {
        selectedMapSector = change.getList().getFirst();
      }
      if (selectedFile != null) {
        parseSaveGame.setDisable(false);
      }
    });

    splitCb.setSelected(false);
    terranCb.setSelected(false);
    pirateCb.setSelected(false);
    boronCb.setSelected(false);
    timelinesCb.setSelected(false);
    filterData(observableMapSectorList);
    splitCb.setOnAction(event -> filterData(observableMapSectorList));
    terranCb.setOnAction(event -> filterData(observableMapSectorList));
    pirateCb.setOnAction(event -> filterData(observableMapSectorList));
    boronCb.setOnAction(event -> filterData(observableMapSectorList));
    timelinesCb.setOnAction(event -> filterData(observableMapSectorList));
  }

  private void filterData(ObservableList<MapSectorEntity> observableMapSectorList) {
    Set<MapType> allowedMapTypes = new HashSet<>();
    allowedMapTypes.add(MapType.DEFAULT);
    if (splitCb.isSelected()) {
      allowedMapTypes.add(MapType.SPLIT);
    }
    if (terranCb.isSelected()) {
      allowedMapTypes.add(MapType.TERRAN);
    }
    if (pirateCb.isSelected()) {
      allowedMapTypes.add(MapType.PIRATE);
    }
    if (boronCb.isSelected()) {
      allowedMapTypes.add(MapType.BORON);
    }
    if (timelinesCb.isSelected()) {
      allowedMapTypes.add(MapType.TIMELINES);
    }

    SortedList<MapSectorEntity> sectorSortedList = observableMapSectorList.filtered(mapSector -> allowedMapTypes.contains(mapSector.getMapType())).sorted();
    sectorTable.setItems(sectorSortedList);
    sectorSortedList.comparatorProperty().bind(sectorTable.comparatorProperty());
    sectorTable.refresh();
  }

  private void initializeLoadFileButton() {
    loadFileButton.setOnAction(e -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Open Save Game File");
      fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("X4 Game Save", "*.xml.gz"));
      this.selectedFile = fileChooser.showOpenDialog(window);
      if (selectedFile != null) {
        selectedFilePathLabel.setText("Selected File: " + selectedFile.getAbsolutePath());
        if (selectedMapSector != null) {
          parseSaveGame.setDisable(false);
        }
      } else {
        selectedFilePathLabel.setText("No file selected");
      }
    });
  }

}
