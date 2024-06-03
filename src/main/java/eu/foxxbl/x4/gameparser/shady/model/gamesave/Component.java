package eu.foxxbl.x4.gameparser.shady.model.gamesave;

import static eu.foxxbl.x4.gameparser.shady.model.gamesave.Component.TAG_NAME;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = TAG_NAME)
@Data
public class Component implements Serializable, Comparable<Component> {


  @Serial
  private static final long serialVersionUID = 8784437586007639649L;

  public static final String TAG_NAME = "component";
  public static final String CONNECTION_ATTRIBUTE_NAME = "component";
  public static final String MACRO_ATTRIBUTE_NAME = "macro";
  public static final String CLASS_ATTRIBUTE_NAME = "class";
  private Connections connections;

  private Control control;

  @XmlAttribute(name = ComponentClass.CLASS, required = true)
  @XmlJavaTypeAdapter(ComponentClassAdapter.class)
  private ComponentClass clazz;

  @XmlAttribute(name = Component.MACRO_ATTRIBUTE_NAME)
  private String macro;

  @XmlAttribute(name = Component.CONNECTION_ATTRIBUTE_NAME)
  private String localConnection;

  @XmlAttribute
  private String code;

  @XmlAttribute
  private String owner;

  @XmlAttribute(name = "knownto")
  private String knownTo;

  /**
   * NPC Class attributes
   */
  @XmlAttribute(name = "name")
  private String name;

  /* NPC Class attributes */
  @XmlAttribute
  private String type;

  @XmlAttribute(required = true)
  private String id;

  /* Station additional attributes */
  private Source source;

  private Traits traits;

  @Override
  public int compareTo(Component o) {
    if (this.getClazz().equals(o.getClazz())) {
      return this.getId().compareTo(o.getId());
    } else {
      return this.getClazz().compareTo(o.getClazz());
    }
  }

}