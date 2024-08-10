package eu.foxxbl.x4.gameparser.shady.parse.xml;

import eu.foxxbl.x4.gameparser.shady.model.gamesave.Component;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.ComponentClass;
import eu.foxxbl.x4.gameparser.shady.model.parse.ParsedMapSector;
import eu.foxxbl.x4.gameparser.shady.parse.xml.filter.SectorFilter;
import eu.foxxbl.x4.gameparser.shady.ui.controller.ParseSaveGameTask;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class X4SaveGameParser {

  private final ShadyGuyParser shadyGuyParser;

  public List<ParsedMapSector> parseFileStream(ParseSaveGameTask parsingTask) throws XMLStreamException, JAXBException, IOException {
    File fileToParse = parsingTask.getFileToParse();
    log.info("Parsing file: {}, ", fileToParse);
    try (InputStream gzipStream = new GZIPInputStream(new FileInputStream(fileToParse))) {
      Reader decoder = new InputStreamReader(gzipStream, StandardCharsets.UTF_8);
      BufferedReader buffered = new BufferedReader(decoder);
      return parseFileStreamBuffered(buffered, parsingTask);
    }
  }

  public List<ParsedMapSector> parseFileStreamBuffered(BufferedReader buffered, ParseSaveGameTask parsingTask) throws XMLStreamException, JAXBException {
    int sectorNumber = 0;
    List<ParsedMapSector> parsedMapSectorList = new ArrayList<>();

    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

    XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(buffered);

    xmlStreamReader = xmlInputFactory.createFilteredReader(xmlStreamReader, new SectorFilter());
    JAXBContext jc = JAXBContext.newInstance(Component.class);
    Unmarshaller unmarshaller = jc.createUnmarshaller();

    while (xmlStreamReader.hasNext()) {
      int xmlEvent = xmlStreamReader.next();
      if (xmlEvent == XMLStreamConstants.START_ELEMENT && isComponent(xmlStreamReader) && isSector(xmlStreamReader)) {
        sectorNumber++;
        JAXBElement<Component> jb = unmarshaller.unmarshal(xmlStreamReader, Component.class);
        Component component = jb.getValue();
        parsedMapSectorList.add(shadyGuyParser.findShadyGuys(component, component.getMacro(), component.getOwner()));
        parsingTask.externalUpdateProgress(sectorNumber);
      }
    }
    return parsedMapSectorList;
  }

  public static boolean isComponent(XMLStreamReader xmlStreamReader) {
    return Component.TAG_NAME.equalsIgnoreCase(xmlStreamReader.getLocalName());
  }

  public boolean isSector(XMLStreamReader xmlStreamReader) {

    boolean isSector = false;

    for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++) {
      QName name = xmlStreamReader.getAttributeName(i);
      String value = xmlStreamReader.getAttributeValue(i);
      if (name.getLocalPart().equals(ComponentClass.CLASS)) {
        if (value.equalsIgnoreCase(ComponentClass.SECTOR.getName())) {
          isSector = true;
          break;
        }
      }
    }
    return isSector;
  }

}
