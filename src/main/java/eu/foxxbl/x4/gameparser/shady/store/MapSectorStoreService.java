package eu.foxxbl.x4.gameparser.shady.store;


import eu.foxxbl.x4.gameparser.shady.model.entity.MapSectorEntity;
import eu.foxxbl.x4.gameparser.shady.repository.MapSectorRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MapSectorStoreService {

  @Autowired
  private MapSectorRepository mapSectorRepository;

  public List<MapSectorEntity> getAllMapSectors() {
    return mapSectorRepository.findAll();
  }


}
