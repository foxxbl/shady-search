package eu.foxxbl.x4.gameparser.shady.ui.controller;

import eu.foxxbl.x4.gameparser.shady.model.parse.BlackMarketeers;
import eu.foxxbl.x4.gameparser.shady.parse.GameParsingService;
import java.io.File;
import java.util.Map;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ParseSaveGameTask extends Task<Map<String, BlackMarketeers>> {

  private final GameParsingService gameParsingService;

  private final File fileToParse;

  private final int maxSectors;

  @Override
  protected Map<String, BlackMarketeers> call() {
    return gameParsingService.parseGameForBlackMarketeers(this);
  }

  public void externalUpdateProgress(int workDone) {
    updateProgress(workDone, maxSectors);
  }


}
