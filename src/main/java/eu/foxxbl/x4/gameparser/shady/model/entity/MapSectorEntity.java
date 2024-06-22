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
public class MapSectorEntity implements Comparable<MapSectorEntity> {


  //Sector macro
  @Id
  private String sectorMacro;

  //Sector mapType
  private MapType mapType;

  //Translated Sector Name
  private String sectorName;

  @Override
  public int compareTo(MapSectorEntity mapSectorEntity) {
    return this.sectorMacro.compareTo(mapSectorEntity.sectorMacro);
  }
}
