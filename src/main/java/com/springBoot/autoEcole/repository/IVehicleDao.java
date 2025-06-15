package com.springBoot.autoEcole.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.Vehicle;

@Repository
@Transactional
public interface IVehicleDao extends CrudRepository<Vehicle, String> {

	public Vehicle findByImmatriculation(String immat);

	public Long removeByImmatriculation(String immat);
	
}
