package eu.foxxbl.x4.gameparser.shady.model.gamesave;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = Connections.TAG_NAME)
@Data
public class Connections {

  public static final String TAG_NAME = "connections";
  private List<Connection> connection = new ArrayList<>();
}
