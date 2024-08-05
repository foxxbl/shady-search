package eu.foxxbl.x4.gameparser.shady.parse;

import eu.foxxbl.x4.gameparser.shady.model.parse.BlackMarketeers;
import eu.foxxbl.x4.gameparser.shady.parse.xml.X4SaveGameParser;
import eu.foxxbl.x4.gameparser.shady.ui.controller.ParseSaveGameTask;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class GameParsingService {

  private final X4SaveGameParser saveGameParser;

  /**
   * Parses a gamesave for black marketeers and returns result  as the map of the sector macros and list of Black Marketeers per sector
   *
   * @param parsingTask {@link ParseSaveGameTask} - task containing the reference to the full path to the game save
   * @return Map<String, SectorBlackMarketeers> - map of sectors and black marketeer data per sector
   */
  public Map<String, BlackMarketeers> parseGameForBlackMarketeers(ParseSaveGameTask parsingTask) {
    File gameSaveFileToParse = parsingTask.getFileToParse();
    try {
      if (!gameSaveFileToParse.exists()) {
        throw new IllegalArgumentException("File " + gameSaveFileToParse.getName() + " doesn't exist!");
      }
      parsingTask.externalUpdateProgress(0);

      log.info("Starting parsing file: {}", gameSaveFileToParse.getAbsoluteFile());
      Instant start = Instant.now();
      Map<String, BlackMarketeers> blackMarketeerMap = saveGameParser.parseFileStream(parsingTask);
      Duration duration = Duration.between(start, Instant.now());
      log.info("Finished parsing file:{}, number of sectors:{},  time: {} sec {} ms", gameSaveFileToParse.getAbsoluteFile(), blackMarketeerMap.keySet().size(),
          duration.toSeconds(), duration.toMillisPart());
      parsingTask.externalUpdateProgress(parsingTask.getMaxSectors());

      return blackMarketeerMap;
    } catch (Exception e) {
      log.error("Exception {} happened during game parsing! GameSaveFile: {}", e.getLocalizedMessage(), gameSaveFileToParse);
      throw e;
    }

  }

}
