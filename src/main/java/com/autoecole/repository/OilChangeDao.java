package com.autoecole.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.autoecole.model.OilChange;
import com.autoecole.model.Vehicle;

@Repository
@Transactional
public interface OilChangeDao extends CrudRepository<OilChange, Long> {

	Long removeById(Long id);

	List<OilChange> findByVehicle(Vehicle vehicle);

	List<OilChange> findByNextOperationDateBefore(LocalDate date);
}