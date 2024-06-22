package eu.foxxbl.x4.gameparser.shady.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MapType {
  DEFAULT("X4: Foundations", false),
  SPLIT("X4: Split Vendetta", true),
  TERRAN("X4: Cradle of Humanity", true),
  PIRATE("X4: Tides of Avarice", true),
  BORON("X4: Kingdom End", true),
  TIMELINES("X4: Timelines", true);

  private final String fullName;
  private final boolean isDLC;

}

