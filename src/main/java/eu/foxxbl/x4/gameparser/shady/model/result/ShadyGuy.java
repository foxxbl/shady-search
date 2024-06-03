package eu.foxxbl.x4.gameparser.shady.model.result;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class ShadyGuy {

  private final String stationCode;
  private final String stationMacro;
  private String componentId;
  private ShadyGuyStatus status;
  private int voiceLeaks;
  private String name;


}
