package eu.foxxbl.x4.gameparser.shady.ui.controller;

import eu.foxxbl.x4.gameparser.shady.config.ConfigReader;
import eu.foxxbl.x4.gameparser.shady.model.map.MapType;
import eu.foxxbl.x4.gameparser.shady.model.map.Sector;
import eu.foxxbl.x4.gameparser.shady.model.result.ShadyGuy;
import eu.foxxbl.x4.gameparser.shady.parse.GameParsingService;
import java.io.File;
import java.io.IOException;
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
  private final List<Sector> allMapSectors;
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
  public TableView<Sector> sectorTable;
  @FXML
  private TableColumn<Sector, String> sectorName;
  @FXML
  private TableColumn<Sector, String> mapType;
  @FXML
  private CheckBox defaultCb;
  @FXML
  private CheckBox splitCb;
  @FXML
  private CheckBox terranCb;

  @FXML
  private CheckBox pirateCb;
  @FXML
  private CheckBox boronCb;

  private File selectedFile = null;
  private Sector selectedMapSector = null;


  public MainWindow(FxControllerAndView<ParsedDataDialog, VBox> parsedDataDialog, ConfigReader configReader, GameParsingService gameParsingService) throws IOException {
    this.parsedDataDialog = parsedDataDialog;
    this.allMapSectors = configReader.loadSectors();
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
      parsedDataDialog.getController().initialize(blackMarketeersList, selectedMapSector.sectorName());
      parsedDataDialog.getController().show();
    });
  }

  private void initializeMapSectorTable() {

    sectorName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().sectorName()));
    mapType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().mapType().getFullName()));
    ObservableList<Sector> observableMapSectorList = FXCollections.observableArrayList(allMapSectors);
    sectorTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    ObservableList<Sector> selectedItems = sectorTable.getSelectionModel().getSelectedItems();

    selectedItems.addListener((ListChangeListener<Sector>) change -> {
      ObservableList<? extends Sector>  sectorList = change.getList();
      if (!sectorList.isEmpty()) {
        selectedMapSector = change.getList().get(0);
      }
      if (selectedFile != null) {
        parseSaveGame.setDisable(false);
      }
    });

    defaultCb.setSelected(true);
    splitCb.setSelected(false);
    terranCb.setSelected(false);
    pirateCb.setSelected(false);
    boronCb.setSelected(false);
    filterData(observableMapSectorList);
    defaultCb.setOnAction(event -> filterData(observableMapSectorList));
    splitCb.setOnAction(event -> filterData(observableMapSectorList));
    terranCb.setOnAction(event -> filterData(observableMapSectorList));
    pirateCb.setOnAction(event -> filterData(observableMapSectorList));
    boronCb.setOnAction(event -> filterData(observableMapSectorList));
  }

  private void filterData(ObservableList<Sector> observableMapSectorList) {
    Set<MapType> allowedMapTypes = new HashSet<>();
    if (defaultCb.isSelected()) {
      allowedMapTypes.add(MapType.DEFAULT);
    }
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
    SortedList<Sector> sectorSortedList = observableMapSectorList.filtered(Sector -> allowedMapTypes.contains(Sector.mapType())).sorted();
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
