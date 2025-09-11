package com.autoecole.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.autoecole.model.TechnicalVisit;
import com.autoecole.model.Vehicle;

@Repository
@Transactional
public interface TechnicalVisitDao extends CrudRepository<TechnicalVisit, Long> {

	Long removeById(Long id);

	List<TechnicalVisit> findByVehicle(Vehicle vehicle);

	List<TechnicalVisit> findByNextOperationDateBefore(LocalDate date);
}