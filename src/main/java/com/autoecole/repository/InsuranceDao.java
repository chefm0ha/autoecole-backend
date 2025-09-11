package com.autoecole.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.autoecole.model.Insurance;
import com.autoecole.model.Vehicle;

@Repository
@Transactional
public interface InsuranceDao extends CrudRepository<Insurance, Long> {

	Long removeById(Long id);

	List<Insurance> findByVehicle(Vehicle vehicle);

}