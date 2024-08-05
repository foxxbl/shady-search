package eu.foxxbl.x4.gameparser.shady.model.parse;

import java.util.List;
import lombok.Builder;

/**
 * Parsed Black marketeer data per sector
 * @param sectorMacro - sector macro
 * @param stationTotal - number of stations in sector
 * @param blackMarketeersTotal - number of black marketeers per sector
 * @param blackMarketeersUnlocked - number of unlocked black marketeers per sector
 * @param blackMarketeerList - list of stations and black marketeers
 */
@Builder
public record BlackMarketeers(String sectorMacro, int stationTotal, int blackMarketeersTotal, int blackMarketeersUnlocked, List<BlackMarketeer> blackMarketeerList) {

}