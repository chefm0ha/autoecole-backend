package com.autoecole.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.autoecole.model.Vehicle;

import java.util.Collection;

@Repository
@Transactional
public interface VehicleDao extends CrudRepository<Vehicle, String> {

	Vehicle findByImmatriculation(String immatriculation);
	Collection<Vehicle> findVehicleByCategory(String category);

	Long removeByImmatriculation(String immatriculation);

	@Modifying
	@Query("UPDATE Vehicle v SET v.quota = v.quota - 1 WHERE v.immatriculation = :imm AND v.quota > 0")
	int decrementQuotaIfPositive(@Param("imm") String immatriculation);  // returns the number of rows updated

	@Modifying
	@Transactional
	@Query("UPDATE Vehicle v SET v.quota = 12")
	void updateMonthlyQuota();


}