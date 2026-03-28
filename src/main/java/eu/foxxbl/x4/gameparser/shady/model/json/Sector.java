package eu.foxxbl.x4.gameparser.shady.model.json;

/**
 * Sector data deserialized from sectors JSON.
 * Matches the x4-game-parser output format.
 *
 * @param sectorMacro sector macro identifier (e.g., "Cluster_01_Sector001_macro")
 * @param sun         sunlight value
 * @param extension   optional DLC extension name (e.g., "TERRAN", "PIRATE"), null for base game
 * @param sectorName  translated sector name
 */
public record Sector(String sectorMacro, double sun, String extension, String sectorName) {

}
