package eu.foxxbl.x4.gameparser.shady.ui.controller;

import eu.foxxbl.x4.gameparser.shady.model.parse.ParsedMapSector;
import eu.foxxbl.x4.gameparser.shady.model.ui.MapSector;
import eu.foxxbl.x4.gameparser.shady.service.GameParsingService;
import eu.foxxbl.x4.gameparser.shady.service.MapSectorService;
import java.io.File;
import java.util.List;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ParseSaveGameTask extends Task<List<MapSector>> {

  private final GameParsingService gameParsingService;
  private final MapSectorService mapSectorService;

  private final File fileToParse;

  private final int maxSectors;

  @Override
  protected List<MapSector> call() {
    try {
      List<ParsedMapSector> parsedMapSectorList = gameParsingService.parseGameForBlackMarketeers(this);
      return mapSectorService.populateSectors(parsedMapSectorList);
    } catch (Exception ex) {
      throw new RuntimeException("Error '" + ex.getLocalizedMessage() + "' happened during parsing " + fileToParse, ex);
    }
  }

  public void externalUpdateProgress(int workDone) {
    updateProgress(workDone, maxSectors);
  }


}
