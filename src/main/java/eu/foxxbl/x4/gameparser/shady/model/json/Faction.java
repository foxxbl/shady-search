package eu.foxxbl.x4.gameparser.shady.model.json;

/**
 * Faction data deserialized from factions JSON.
 * Matches the x4-game-parser output format.
 *
 * @param id        faction identifier (e.g., "argon", "teladi")
 * @param name      translated faction name
 * @param shortName short faction abbreviation (e.g., "ARG", "TEL")
 * @param extension optional DLC extension name (e.g., "TERRAN", "PIRATE"), null for base game
 */
public record Faction(String id, String name, String shortName, String extension) {

}
