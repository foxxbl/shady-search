package eu.foxxbl.x4.gameparser.shady.model.gamesave;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = Post.TAG_NAME)
@Data
public class Post {

  private static final String SHADY_GUY = "shadyguy";
  public static final String TAG_NAME = "post";

  @XmlAttribute(required = true)
  private String id;

  @XmlAttribute(name = "component")
  private String postComponent;

  public boolean isShadyGuy() {
    return id.equals(SHADY_GUY);
  }

}
