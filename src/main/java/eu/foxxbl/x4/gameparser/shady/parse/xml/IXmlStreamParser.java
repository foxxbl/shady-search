package eu.foxxbl.x4.gameparser.shady.parse.xml;

import eu.foxxbl.x4.gameparser.shady.model.entity.MapSectorEntity;
import eu.foxxbl.x4.gameparser.shady.model.result.ShadyGuy;
import jakarta.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.GZIPInputStream;
import javax.xml.stream.XMLStreamException;

public interface IXmlStreamParser {

  org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(IXmlStreamParser.class);
  String PROPERTY_FILTERED_ENABLED = "shady-search.filtered-stream-search-enabled";

  default List<ShadyGuy> parseFileStream(File fileToParse, MapSectorEntity searchedSector) {
    log.info("Parsing file: {}, searchedSector: {}", fileToParse, searchedSector);
    try (InputStream gzipStream = new GZIPInputStream(new FileInputStream(fileToParse))) {
      Reader decoder = new InputStreamReader(gzipStream, StandardCharsets.UTF_8);
      BufferedReader buffered = new BufferedReader(decoder);
      return parseFileStreamBuffered(buffered, searchedSector);
    } catch (IOException | XMLStreamException | JAXBException e) {
      throw new RuntimeException("Exception happened during parsing " + fileToParse, e);
    }
  }

  List<ShadyGuy> parseFileStreamBuffered(BufferedReader buffered, MapSectorEntity searchedSector) throws XMLStreamException, JAXBException;
}
