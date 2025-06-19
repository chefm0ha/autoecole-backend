package com.springBoot.autoEcole.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.Insurance;
import com.springBoot.autoEcole.model.Vehicle;

@Repository
@Transactional
public interface InsuranceDao extends CrudRepository<Insurance, Long> {

	Long removeById(Long id);

	List<Insurance> findByVehicle(Vehicle vehicle);

}