package eu.foxxbl.x4.gameparser.shady.ui.controller;

import eu.foxxbl.x4.gameparser.shady.config.ShadySearchConfig;
import eu.foxxbl.x4.gameparser.shady.model.entity.MapType;
import eu.foxxbl.x4.gameparser.shady.model.ui.MapSector;
import eu.foxxbl.x4.gameparser.shady.service.GameParsingService;
import eu.foxxbl.x4.gameparser.shady.service.MapSectorService;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView // equal to: @FxmlView("MainWindow.fxml")
@RequiredArgsConstructor
public class MainWindow {

  private final FxControllerAndView<SectorDialog, VBox> parsedDataDialog;

  private final GameParsingService gameParsingService;

  private final MapSectorService mapSectorService;

  private final ShadySearchConfig shadySearchConfig;

  private List<MapSector> mapSectorList = new ArrayList<>();

  private File selectedFile = null;

  private MapSector selectedMapSector = null;

  @FXML
  public Button loadFileButton;
  @FXML
  public Button showSector;
  @FXML
  public ProgressBar progressBar;
  @FXML
  public Window window;
  @FXML
  public Label selectedFilePathLabel;
  @FXML
  public TableView<MapSector> sectorTableView;
  @FXML
  private TableColumn<MapSector, String> sectorNameTableCol;
  @FXML
  private TableColumn<MapSector, String> sectorOwnerTableCol;
  @FXML
  private TableColumn<MapSector, String> mapTypeTableCol;
  @FXML
  private TableColumn<MapSector, Integer> stationNrTableCol;
  @FXML
  private TableColumn<MapSector, Integer> shadyGuysTotalTableCol;
  @FXML
  private TableColumn<MapSector, Integer> shadyGuysUnlockedTableCol;
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

  private ObservableList<MapSector> observableMapSectorList;

  @FXML
  public void initialize() {
    initializeLoadFileButton();

    initializeMapSectorTable();

    initializeShowSectorButton();
  }

  private void initializeShowSectorButton() {
    showSector.setDisable(true);
    showSector.setOnAction(e -> showSectorDialog());
  }

  private void initializeMapSectorTable() {

    sectorNameTableCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().sectorName()));
    sectorOwnerTableCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().sectorOwnerName()));
    mapTypeTableCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().mapType().getFullName()));
    stationNrTableCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().stationTotal()).asObject());
    shadyGuysTotalTableCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().blackMarketeersTotal()).asObject());
    shadyGuysUnlockedTableCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().blackMarketeersUnlocked()).asObject());

    observableMapSectorList = FXCollections.observableArrayList(mapSectorList);
    sectorTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    sectorTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      showSector.setDisable(newValue == null);
      selectedMapSector = newValue;
    });

    sectorTableView.setRowFactory(param -> {
      TableRow<MapSector> row = new TableRow<>();
      row.setOnMouseClicked(event -> {
        if (event.getClickCount() == 2 && (!row.isEmpty())) {
          selectedMapSector = row.getItem();
          showSectorDialog();
        }
      });
      return row;
    });

    splitCb.setSelected(false);
    terranCb.setSelected(false);
    pirateCb.setSelected(false);
    boronCb.setSelected(false);
    timelinesCb.setSelected(false);
    filterData();
    splitCb.setOnAction(event -> filterData());
    terranCb.setOnAction(event -> filterData());
    pirateCb.setOnAction(event -> filterData());
    boronCb.setOnAction(event -> filterData());
    timelinesCb.setOnAction(event -> filterData());
    progressBar.setVisible(false);

  }

  private void filterData() {
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

    SortedList<MapSector> sectorSortedList = observableMapSectorList.filtered(mapSector -> allowedMapTypes.contains(mapSector.mapType())).sorted();
    sectorTableView.setItems(sectorSortedList);
    sectorSortedList.comparatorProperty().bind(sectorTableView.comparatorProperty());
    sectorTableView.refresh();
  }

  private void initializeLoadFileButton() {

    loadFileButton.setOnAction(e -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Open Save Game File");
      fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("X4 Game Save", "*.xml.gz"));
      this.selectedFile = fileChooser.showOpenDialog(window);
      if (selectedFile != null) {
        ParseSaveGameTask task = createParseSaveGameTask(selectedFile);
        progressBar.progressProperty().bind(task.progressProperty());
        progressBar.setVisible(true);
        selectedFilePathLabel.setText(" Game save: " + selectedFile.getAbsolutePath());
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
      } else {
        selectedFilePathLabel.setText("No file selected");
      }
    });
  }

  private ParseSaveGameTask createParseSaveGameTask(File selectedFile) {
    ParseSaveGameTask parseSaveGameTask = new ParseSaveGameTask(gameParsingService, mapSectorService, selectedFile, shadySearchConfig.maxSectors());

    parseSaveGameTask.setOnSucceeded(e -> {
      mapSectorList = parseSaveGameTask.getValue();
      observableMapSectorList = FXCollections.observableArrayList(mapSectorList);
      filterData();
      showSector.setDisable(true);
      progressBar.setVisible(false);
    });

    parseSaveGameTask.setOnFailed(e -> {
      progressBar.setVisible(false);
      showSector.setDisable(true);
      showErrorDialog(parseSaveGameTask.getException());
    });
    return parseSaveGameTask;
  }

  private void showSectorDialog() {
    parsedDataDialog.getController().initialize(selectedMapSector);
    parsedDataDialog.getController().show();
  }

  private void showErrorDialog(Throwable throwable) {
    // Create an error dialog
    Alert errorAlert = new Alert(AlertType.ERROR);
    errorAlert.setTitle("Error happened");
    errorAlert.setHeaderText("An error occurred while parsing game save");
    errorAlert.setContentText("Something went wrong: " + throwable.getLocalizedMessage());

    // Display the error dialog and wait for a response
    errorAlert.showAndWait();
  }


}
