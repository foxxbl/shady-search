package eu.foxxbl.x4.gameparser.shady.model.gamesave;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class ComponentClassAdapter extends XmlAdapter<String, ComponentClass> {

  @Override
  public ComponentClass unmarshal(String name) {
    return ComponentClass.fromString(name);
  }

  @Override
  public String marshal(ComponentClass componentClass) {
    return componentClass.getName();
  }
}
