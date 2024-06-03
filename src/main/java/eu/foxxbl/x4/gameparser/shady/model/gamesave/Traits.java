package eu.foxxbl.x4.gameparser.shady.model.gamesave;

import static eu.foxxbl.x4.gameparser.shady.model.gamesave.Traits.TAG_NAME;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = TAG_NAME)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Traits {

  public static final String TAG_NAME = "traits";

  @XmlAttribute
  private String flags;
}
