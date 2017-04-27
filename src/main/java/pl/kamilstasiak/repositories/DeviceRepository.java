package pl.kamilstasiak.repositories;

/**
 * Created by Kamil on 2016-10-29.
 */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kamilstasiak.entities.Device;


@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
}
