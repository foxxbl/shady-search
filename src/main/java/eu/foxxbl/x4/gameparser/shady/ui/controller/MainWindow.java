package eu.foxxbl.x4.gameparser.shady.ui.controller;

import eu.foxxbl.x4.gameparser.shady.config.ShadySearchConfig;
import eu.foxxbl.x4.gameparser.shady.model.entity.MapType;
import eu.foxxbl.x4.gameparser.shady.model.ui.MapSector;
import eu.foxxbl.x4.gameparser.shady.service.GameParsingService;
import eu.foxxbl.x4.gameparser.shady.service.MapSectorService;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableRow;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXListCell;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
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
  public MFXButton loadFileButton;
  @FXML
  public MFXButton showSector;
  @FXML
  public MFXProgressBar progressBar;
  @FXML
  public Window window;
  @FXML
  public MFXTextField selectedFilePathLabel;
  @FXML
  public MFXTableView<MapSector> sectorTableView;
  @FXML
  private MFXTableColumn<MapSector> sectorNameTableCol;
  @FXML
  private MFXTableColumn<MapSector> sectorOwnerTableCol;
  @FXML
  private MFXTableColumn<MapSector> mapTypeTableCol;
  @FXML
  private MFXTableColumn<MapSector> stationNrTableCol;
  @FXML
  private MFXTableColumn<MapSector> shadyGuysTotalTableCol;
  @FXML
  private MFXTableColumn<MapSector> shadyGuysUnlockedTableCol;
  @FXML
  private MFXCheckbox splitCb;
  @FXML
  private MFXCheckbox terranCb;
  @FXML
  private MFXCheckbox pirateCb;
  @FXML
  private MFXCheckbox boronCb;
  @FXML
  private MFXCheckbox timelinesCb;

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

    sectorNameTableCol.setComparator(Comparator.comparing(MapSector::sectorName));
    sectorOwnerTableCol.setComparator(Comparator.comparing(MapSector::sectorOwnerName));
    mapTypeTableCol.setComparator(Comparator.comparing(MapSector::getMapTypeFullName));
    stationNrTableCol.setComparator(Comparator.comparing(MapSector::stationTotal));
    shadyGuysTotalTableCol.setComparator(Comparator.comparing(MapSector::blackMarketeersTotal));
    shadyGuysUnlockedTableCol.setComparator(Comparator.comparing(MapSector::blackMarketeersUnlocked));

    sectorNameTableCol.setRowCellFactory(mapSector -> new MFXTableRowCell<>(MapSector::sectorName));
    sectorOwnerTableCol.setRowCellFactory(mapSector -> new MFXTableRowCell<>(MapSector::sectorOwnerName));
    mapTypeTableCol.setRowCellFactory(mapSector -> new MFXTableRowCell<>(MapSector::getMapTypeFullName));
    stationNrTableCol.setRowCellFactory(mapSector -> new MFXTableRowCell<>(MapSector::stationTotal));
    shadyGuysTotalTableCol.setRowCellFactory(mapSector -> new MFXTableRowCell<>(MapSector::blackMarketeersTotal));
    shadyGuysUnlockedTableCol.setRowCellFactory(mapSector -> new MFXTableRowCell<>(MapSector::blackMarketeersUnlocked));


    observableMapSectorList = FXCollections.observableArrayList(mapSectorList);
    sectorTableView.getSelectionModel().setAllowsMultipleSelection(false);
    sectorTableView.getSelectionModel().selectionProperty().addListener((observable, oldValue, newValue) -> {
      showSector.setDisable(newValue == null);
      if (newValue != null && !newValue.isEmpty()) {
        selectedMapSector = newValue.values().stream().findFirst().get();
      }
    });
    sectorTableView.setFooterVisible(false);
    sectorTableView.getFilters().addAll(
        new StringFilter<>("Sector name", MapSector::sectorName),
        new StringFilter<>("Sector owner", MapSector::sectorOwnerName),
        new IntegerFilter<>("Nr of stations", MapSector::stationTotal),
        new IntegerFilter<>("Nr of black marketeers", MapSector::blackMarketeersTotal),
        new IntegerFilter<>("Unlocked black marketeers", MapSector::blackMarketeersUnlocked)

    );

    sectorTableView.setTableRowFactory( mapSector -> {
      MFXTableRow<MapSector> row = new MFXTableRow<>(sectorTableView, mapSector) {
        @Override
        public void updateItem(MapSector mapSector) {
          super.updateItem(mapSector);
          if (mapSector == null) {
            setStyle("");
          } else {
            setStyle(switch (mapSector.mapType()) {

              case DEFAULT -> "-fx-text-fill: black;";
              case SPLIT -> "-fx-text-fill: red;";
              case TERRAN -> "-fx-text-fill: blue;";
              case PIRATE -> "-fx-text-fill: goldenrod;";
              case BORON -> "-fx-text-fill: lightseagreen;";
              case TIMELINES -> "-fx-text-fill: darkred;";
            });
          }
        }

         @Override
        protected void updateRow(MapSector mapSector) {
          super.updateRow(mapSector);

          if (mapSector == null) {
            setStyle("");
          } else {
            setStyle(switch (mapSector.mapType()) {

              case DEFAULT -> "-fx-text-fill: black;";
              case SPLIT -> "-fx-text-fill: red;";
              case TERRAN -> "-fx-text-fill: blue;";
              case PIRATE -> "-fx-text-fill: goldenrod;";
              case BORON -> "-fx-text-fill: lightseagreen;";
              case TIMELINES -> "-fx-text-fill: darkred;";
            });
          }
        }
      };

      row.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
        if (event.getClickCount() == 2) {
          selectedMapSector = row.getData();
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
    sectorTableView.setFooterVisible(!sectorSortedList.isEmpty());
    sectorTableView.update();
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
        selectedFilePathLabel.setBorder(Border.EMPTY);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
      } else {
        selectedFilePathLabel.setText("No file selected");
        selectedFilePathLabel.setBorder(Border.EMPTY);
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

  private static class MapSectorCellFactory extends MFXListCell<MapSector> {
    private final MFXFontIcon userIcon;

    public MapSectorCellFactory(MFXListView<MapSector> listView, MapSector data) {
      super(listView, data);

      userIcon = new MFXFontIcon("fas-user", 18);
      userIcon.getStyleClass().add("user-icon");
      render(data);
    }

    @Override
    protected void render(MapSector data) {
      super.render(data);
      if (userIcon != null) getChildren().add(0, userIcon);
    }
  }

}
