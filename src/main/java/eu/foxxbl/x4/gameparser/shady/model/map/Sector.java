package eu.foxxbl.x4.gameparser.shady.model.map;


public record Sector(String sectorMacro, String sectorName, MapType mapType) implements Comparable<Sector> {

  @Override
  public int compareTo(Sector mapSector) {
    return this.sectorName.compareTo(mapSector.sectorName);
  }
}
