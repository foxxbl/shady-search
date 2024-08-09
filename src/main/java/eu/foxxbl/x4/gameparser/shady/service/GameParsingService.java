package eu.foxxbl.x4.gameparser.shady.service;

import eu.foxxbl.x4.gameparser.shady.model.parse.ParsedMapSector;
import eu.foxxbl.x4.gameparser.shady.parse.xml.X4SaveGameParser;
import eu.foxxbl.x4.gameparser.shady.ui.controller.ParseSaveGameTask;
import jakarta.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class GameParsingService {

  private final X4SaveGameParser saveGameParser;

  /**
   * Parses a gamesave for black marketeers and returns result as the list of sector parsed data with black marketeers
   *
   * @param parsingTask {@link ParseSaveGameTask} - task containing the reference to the full path to the game save
   * @return List<ParsedMapSector> - list of parsed sectors and black marketeer data per sector
   */
  public List<ParsedMapSector> parseGameForBlackMarketeers(ParseSaveGameTask parsingTask) throws XMLStreamException, JAXBException, IOException {
    File gameSaveFileToParse = parsingTask.getFileToParse();
    if (!gameSaveFileToParse.exists()) {
      throw new IllegalArgumentException("File " + gameSaveFileToParse.getName() + " doesn't exist!");
    }
    parsingTask.externalUpdateProgress(0);

    log.info("Starting parsing file: {}", gameSaveFileToParse.getAbsoluteFile());
    Instant start = Instant.now();
    List<ParsedMapSector> parsedMapSectorList = saveGameParser.parseFileStream(parsingTask);
    Duration duration = Duration.between(start, Instant.now());
    log.info("Finished parsing file:{}, number of sectors:{},  time: {} sec {} ms", gameSaveFileToParse.getAbsoluteFile(), parsedMapSectorList.size(), duration.toSeconds(),
        duration.toMillisPart());
    parsingTask.externalUpdateProgress(parsingTask.getMaxSectors());

    return parsedMapSectorList;
  }

}

