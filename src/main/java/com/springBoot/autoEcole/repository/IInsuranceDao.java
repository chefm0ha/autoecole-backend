package com.springBoot.autoEcole.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.Insurance;
import com.springBoot.autoEcole.model.Vehicle;

@Repository
@Transactional
public interface IInsuranceDao extends CrudRepository<Insurance, Long> {

	Long removeById(Long id);

	List<Insurance> findByVehicle(Vehicle vehicle);

	List<Insurance> findByNextOperationDateBefore(LocalDate date);
}