package eu.foxxbl.x4.gameparser.shady.model.gamesave;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class ConnectionTypeAdapter extends XmlAdapter<String, ConnectionType> {

  @Override
  public ConnectionType unmarshal(String name) {
    return ConnectionType.fromString(name);
  }

  @Override
  public String marshal(ConnectionType connectionType) {
    return connectionType.getName();
  }
}
