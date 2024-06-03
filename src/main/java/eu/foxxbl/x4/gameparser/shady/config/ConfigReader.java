package eu.foxxbl.x4.gameparser.shady.config;

import eu.foxxbl.x4.gameparser.shady.model.map.MapType;
import eu.foxxbl.x4.gameparser.shady.model.map.Sector;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ConfigReader {

  private static final String PROPERTIES_FILE = "sectors.properties";
  public static final String EQUALS_SEPARATOR = "=";

  public List<Sector> loadSectors() throws IOException {
    List<Sector> mapSectorList = new ArrayList<>();

    InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
    if (inputStream == null) {
      throw new RuntimeException("Unable to find properties file" + PROPERTIES_FILE);
    }

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
      String line;
      while ((line = reader.readLine()) != null) {
        // Process each line as needed
        String[] parsed = line.split(EQUALS_SEPARATOR);
        if (parsed.length != 3) {
          log.error("Invalid sector configuration line: {}", line);
        } else {
          String sectorName = parsed[0];
          String macroName = parsed[1];
          MapType mapType = MapType.valueOf(parsed[2]);
          mapSectorList.add(new Sector(macroName, sectorName, mapType));
        }
      }
    }
    return mapSectorList;
  }
}
