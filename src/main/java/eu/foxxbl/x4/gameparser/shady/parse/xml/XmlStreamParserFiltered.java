package eu.foxxbl.x4.gameparser.shady.parse.xml;

import static eu.foxxbl.x4.gameparser.shady.parse.xml.IXmlStreamParser.PROPERTY_FILTERED_ENABLED;

import eu.foxxbl.x4.gameparser.shady.model.entity.MapSectorEntity;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.Component;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.ComponentClass;
import eu.foxxbl.x4.gameparser.shady.model.result.BlackMarketeer;
import eu.foxxbl.x4.gameparser.shady.parse.xml.filter.ComponentFilter;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = PROPERTY_FILTERED_ENABLED)
@RequiredArgsConstructor
@Slf4j
public class XmlStreamParserFiltered implements IXmlStreamParser {

  private final ShadyGuyParser componentParser;

  @Override
  public List<BlackMarketeer> parseFileStreamBuffered(BufferedReader buffered, MapSectorEntity searchedSector) throws XMLStreamException, JAXBException {
    List<BlackMarketeer> blackMarketeerList = new ArrayList<>();
    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

    XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(buffered);
    ComponentFilter sectorZoneFilter = new ComponentFilter(ComponentClass.SECTOR, searchedSector.getSectorMacro());

    xmlStreamReader = xmlInputFactory.createFilteredReader(xmlStreamReader, sectorZoneFilter);
    JAXBContext jc = JAXBContext.newInstance(Component.class);
    Unmarshaller unmarshaller = jc.createUnmarshaller();

    while (xmlStreamReader.hasNext()) {
      int xmlEvent = xmlStreamReader.next();
      //Process start element.
      if (xmlEvent == XMLStreamConstants.START_ELEMENT && xmlStreamReader.getLocalName().equals(Component.TAG_NAME) && ComponentFilterUtils.isSector(xmlStreamReader)) {
        log.info("Found searched sector: {}", searchedSector);
        JAXBElement<Component> jb = unmarshaller.unmarshal(xmlStreamReader, Component.class);
        Component component = jb.getValue();
        blackMarketeerList = componentParser.findShadyGuys(component, searchedSector.getSectorName());
        break;
      }
    }
    return blackMarketeerList;
  }
}
