package eu.foxxbl.x4.gameparser.shady.model.gamesave;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = Control.TAG_NAME)
@Data
public class Control implements Serializable {

  public static final String TAG_NAME = "control";
  private List<Post> post = new ArrayList<>();
}
