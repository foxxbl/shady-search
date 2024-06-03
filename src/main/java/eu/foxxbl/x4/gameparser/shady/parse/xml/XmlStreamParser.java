package eu.foxxbl.x4.gameparser.shady.parse.xml;

import static eu.foxxbl.x4.gameparser.shady.parse.xml.IXmlStreamParser.PROPERTY_FILTERED_ENABLED;

import eu.foxxbl.x4.gameparser.shady.model.gamesave.Component;
import eu.foxxbl.x4.gameparser.shady.model.map.Sector;
import eu.foxxbl.x4.gameparser.shady.model.result.ShadyGuy;
import eu.foxxbl.x4.gameparser.shady.parse.xml.filter.ComponentFilterUtils;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnExpression("'${" + PROPERTY_FILTERED_ENABLED + "}' == 'false'")
@RequiredArgsConstructor
@Slf4j
public class XmlStreamParser implements IXmlStreamParser {

  private final ShadyGuyParser componentParser;

  @Override
  public List<ShadyGuy> parseFileStreamBuffered(BufferedReader buffered, Sector searchedSector) throws JAXBException, XMLStreamException {
    List<ShadyGuy> shadyGuyList = new ArrayList<>();
    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(buffered);
    JAXBContext jc = JAXBContext.newInstance(Component.class);
    Unmarshaller unmarshaller = jc.createUnmarshaller();
    while (xmlStreamReader.hasNext()) {
      int xmlEvent = xmlStreamReader.next();
      //Process start element.
      if (xmlEvent == XMLStreamConstants.START_ELEMENT
          && xmlStreamReader.getLocalName().equals(Component.TAG_NAME)
          && ComponentFilterUtils.isSector(xmlStreamReader) && ComponentFilterUtils.isSearchedSector(xmlStreamReader, searchedSector.sectorMacro())) {
        log.info("Found searched sector macro: {}", searchedSector);
        JAXBElement<Component> jb = unmarshaller.unmarshal(xmlStreamReader, Component.class);
        Component component = jb.getValue();
        shadyGuyList = componentParser.findShadyGuys(component, searchedSector.sectorName());
        break;
      }
    }
    return shadyGuyList;
  }


}
