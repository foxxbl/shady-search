package eu.foxxbl.x4.gameparser.shady.store;


import eu.foxxbl.x4.gameparser.shady.model.entity.MapSectorEntity;
import eu.foxxbl.x4.gameparser.shady.repository.MapSectorRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MapSectorStoreService {

  private final MapSectorRepository mapSectorRepository;

  public List<MapSectorEntity> getAllMapSectors() {
    return mapSectorRepository.findAll();
  }


}
