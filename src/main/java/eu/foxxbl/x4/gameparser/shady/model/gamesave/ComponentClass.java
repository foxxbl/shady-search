package eu.foxxbl.x4.gameparser.shady.model.gamesave;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@RequiredArgsConstructor
@Slf4j
public enum ComponentClass {

  GALAXY("galaxy", 0), CLUSTER("cluster", 1), SECTOR("sector", 2), ZONE("zone", 3), STATION("station", 4), NPC("npc", 8), SIGNALLEAK("signalleak", 10), OTHER("other", 99);

  public static final String CLASS = "class";
  private final String name;

  private final int order;

  private static final Map<String, ComponentClass> lookup = EnumSet.allOf(ComponentClass.class).stream().collect(Collectors.toMap(ComponentClass::getName, Function.identity()));
  private static final List<ComponentClass> classesOrdered = EnumSet.allOf(ComponentClass.class).stream().sorted(ComponentClass::compare).toList();
  private static final Set<String> unknownClasses = new HashSet<>();


  public static ComponentClass fromString(String name) {
    /*
    For debugging unknown component classes:


    ComponentClass componentClass = lookup.get(name);
    if (componentClass == null) {
      if (!unknownClasses.contains(name)) {
        log.warn("Cannot find the component class: {}", name);
        unknownClasses.add(name);
      }
      componentClass= OTHER;
    }
    return componentClass;
    */
    return lookup.getOrDefault(name, OTHER);
  }

  public static int compare(ComponentClass o1, ComponentClass o2) {
    return Integer.compare(o1.getOrder(), o2.getOrder());
  }
}
