package eu.foxxbl.x4.gameparser.shady.model.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ShadyGuyStatus {
  ACTIVE("Active"), INACTIVE("Inactive"), NONE("None");
  private final String value;
}
