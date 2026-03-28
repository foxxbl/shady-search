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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class X4SaveGameParser {

  private static final int BUFFER_SIZE = 65_536;

  private static final JAXBContext JAXB_CONTEXT;

  static {
    try {
      JAXB_CONTEXT = JAXBContext.newInstance(Component.class);
    } catch (JAXBException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newInstance();

  private final ShadyGuyParser shadyGuyParser;

  public List<ParsedMapSector> parseFileStream(ParseSaveGameTask parsingTask) throws XMLStreamException, JAXBException, IOException {
    File fileToParse = parsingTask.getFileToParse();
    log.info("Parsing file: {}, ", fileToParse);
    try (var gzipStream = new GZIPInputStream(new BufferedInputStream(new FileInputStream(fileToParse), BUFFER_SIZE), BUFFER_SIZE)) {
      var decoder = new InputStreamReader(gzipStream, StandardCharsets.UTF_8);
      var buffered = new BufferedReader(decoder, BUFFER_SIZE);
      return parseFileStreamBuffered(buffered, parsingTask);
    }
  }

  public List<ParsedMapSector> parseFileStreamBuffered(BufferedReader buffered, ParseSaveGameTask parsingTask) throws XMLStreamException, JAXBException {
    int sectorNumber = 0;
    List<ParsedMapSector> parsedMapSectorList = new ArrayList<>();

    XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(buffered);
    xmlStreamReader = XML_INPUT_FACTORY.createFilteredReader(xmlStreamReader, new SectorFilter());
    Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();

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
    for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++) {
      QName name = xmlStreamReader.getAttributeName(i);
      String value = xmlStreamReader.getAttributeValue(i);
      if (name.getLocalPart().equals(ComponentClass.CLASS)) {
        return value.equalsIgnoreCase(ComponentClass.SECTOR.getName());
      }
    }
    return false;
  }

}
