package eu.foxxbl.x4.gameparser.shady.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FactionEntity {

  @Id
  private String id;


  /**
   * Translated sector name
   */
  private String name;

  /**
   * Translated sector shortName
   */
  private String shortName;

  private MapType mapType;


}
