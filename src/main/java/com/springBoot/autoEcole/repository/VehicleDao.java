package com.springBoot.autoEcole.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.Vehicle;

@Repository
@Transactional
public interface VehicleDao extends CrudRepository<Vehicle, String> {

	Vehicle findByImmatriculation(String immatriculation);

	Long removeByImmatriculation(String immatriculation);
}