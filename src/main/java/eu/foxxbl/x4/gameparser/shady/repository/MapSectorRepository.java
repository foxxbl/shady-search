package eu.foxxbl.x4.gameparser.shady.repository;

import eu.foxxbl.x4.gameparser.shady.model.entity.MapSectorEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MapSectorRepository extends ListCrudRepository<MapSectorEntity, String> {

}
