package eu.foxxbl.x4.gameparser.shady.model.parse;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Station and black marketeer name and status
 */
@RequiredArgsConstructor
@Data
public class BlackMarketeer {

  private final String stationCode;
  private final String stationMacro;
  private String componentId;
  private ShadyGuyStatus status;
  private int voiceLeaks;
  private String name;

  public String getStatusName() {
    return status.getValue();
  }

}
