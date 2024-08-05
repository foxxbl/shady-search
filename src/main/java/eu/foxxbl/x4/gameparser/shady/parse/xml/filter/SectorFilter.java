package eu.foxxbl.x4.gameparser.shady.parse.xml.filter;

import eu.foxxbl.x4.gameparser.shady.model.gamesave.Component;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.ComponentClass;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.Connection;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.ConnectionType;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.Connections;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.Control;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.Post;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.Source;
import eu.foxxbl.x4.gameparser.shady.model.gamesave.Traits;
import java.util.Optional;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamReader;

public record SectorFilter() implements StreamFilter {

  private static final Set<String> ALWAYS_ALLOWED_TAGS = Set.of(Component.TAG_NAME, Connections.TAG_NAME, Connection.TAG_NAME, Control.TAG_NAME, Post.TAG_NAME, Source.TAG_NAME,
      Traits.TAG_NAME);

  @Override
  public boolean accept(XMLStreamReader xmlStreamReader) {
    String tagName;
    boolean accept = false;
    if (xmlStreamReader.isStartElement()) {
      tagName = xmlStreamReader.getLocalName();
      if (ALWAYS_ALLOWED_TAGS.contains(tagName)) {
        accept = true;
     /* } else if (Component.TAG_NAME.equalsIgnoreCase(tagName) && allowedComponent(xmlStreamReader)) {
        accept = true;
      } else if (Connection.TAG_NAME.equalsIgnoreCase(tagName) && allowedConnection(xmlStreamReader)) {
        accept = true;*/
      }
    } else if (xmlStreamReader.isEndElement()) {
      tagName = xmlStreamReader.getLocalName();
      if (ALWAYS_ALLOWED_TAGS.contains(tagName)) {
        accept = true;
     /* } else if (Component.TAG_NAME.equalsIgnoreCase(tagName)) {
        accept = true;
      } else if (Connection.TAG_NAME.equalsIgnoreCase(tagName)) {
        accept = true;*/
      }
    }
    return accept;
  }


  private boolean allowedConnection(XMLStreamReader xmlStreamReader) {
    boolean allowed = false;
    Optional<String> connectionName = getConnectionName(xmlStreamReader);
    if (connectionName.isEmpty()) {
      allowed = true;
    } else {
      ConnectionType connectionType = ConnectionType.fromString(connectionName.get());
      if (connectionType == ConnectionType.OTHER) {
        if (connectionName.get().contains(ComponentClass.CLUSTER.getName()) ||
            connectionName.get().contains(ComponentClass.SECTOR.getName()) ||
            connectionName.get().contains(ComponentClass.ZONE.getName())) {
          allowed = true;
        }
      } else {
        allowed = !connectionType.isIgnore();
      }


    }
    return allowed;
  }

  /**
   * Somehow this doesn't work at all - cannot find stations if filter "OTHERS"
   */
  private boolean allowedComponent(XMLStreamReader xmlStreamReader) {
    return !ComponentClass.OTHER.equals(getComponentClass(xmlStreamReader));
  }

  private Optional<String> getConnectionName(XMLStreamReader xmlStreamReader) {
    Optional<String> connectionName = Optional.empty();
    for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++) {
      QName name = xmlStreamReader.getAttributeName(i);
      String value = xmlStreamReader.getAttributeValue(i);
      if (name.getLocalPart().equals(Connection.CONNECTION_ATTRIBUTE_NAME)) {
        connectionName = Optional.of(value);
        break;
      }

    }
    return connectionName;
  }

  private ComponentClass getComponentClass(XMLStreamReader xmlStreamReader) {
    ComponentClass componentClass = ComponentClass.OTHER;
    for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++) {
      QName name = xmlStreamReader.getAttributeName(i);
      String value = xmlStreamReader.getAttributeValue(i);
      if (name.getLocalPart().equals(Component.CLASS_ATTRIBUTE_NAME)) {
        componentClass = ComponentClass.fromString(value);
        break;
      }

    }
    return componentClass;
  }

  /**
   * need better filtering - doesn't work so far
   */
  private String getComponentMacro(XMLStreamReader xmlStreamReader) {
    String componentMacro = "";
    for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++) {
      QName name = xmlStreamReader.getAttributeName(i);
      String value = xmlStreamReader.getAttributeValue(i);
      if (name.getLocalPart().equals(Component.MACRO_ATTRIBUTE_NAME)) {
        componentMacro = value;
        break;
      }

    }
    return componentMacro;
  }
}
