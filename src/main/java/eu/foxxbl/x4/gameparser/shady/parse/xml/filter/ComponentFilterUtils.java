package eu.foxxbl.x4.gameparser.shady.parse.xml.filter;

import eu.foxxbl.x4.gameparser.shady.model.gamesave.Component;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.ComponentClass;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ComponentFilterUtils {

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


  public boolean isSearchedSector(XMLStreamReader xmlStreamReader, String searchedSectorMacro) {
    boolean isSearchedSector = false;
    for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++) {
      QName name = xmlStreamReader.getAttributeName(i);
      String value = xmlStreamReader.getAttributeValue(i);
      if (name.getLocalPart().equals(Component.MACRO_ATTRIBUTE_NAME)) {
        if (value.equalsIgnoreCase(searchedSectorMacro)) {
          isSearchedSector = true;
          break;
        }

      }

    }
    return isSearchedSector;
  }

}
