package eu.foxxbl.x4.gameparser.shady.model.gamesave;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = Connection.TAG_NAME)
@Data
public class Connection {

  public static final String TAG_NAME = "connection";
  public static final String CONNECTION_ATTRIBUTE_NAME = "connection";

  @XmlJavaTypeAdapter(ConnectionTypeAdapter.class)
  private ConnectionType connectionType;

  @XmlAttribute(name = Connection.CONNECTION_ATTRIBUTE_NAME)
  private String connectionName;

  @XmlAttribute
  private String macro;

  private List<Component> component = new ArrayList<>();


}
