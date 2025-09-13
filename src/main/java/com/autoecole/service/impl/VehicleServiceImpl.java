package com.autoecole.service.impl;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.autoecole.dto.response.VehicleDTO;
import com.autoecole.model.Category;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autoecole.model.ApplicationFile;
import com.autoecole.model.Vehicle;
import com.autoecole.repository.ApplicationFileDao;
import com.autoecole.repository.VehicleDao;
import com.autoecole.service.VehicleService;

@Service
@Transactional
@AllArgsConstructor
public class VehicleServiceImpl implements VehicleService {

	private static final Logger logger = LoggerFactory.getLogger(VehicleServiceImpl.class);

	private final VehicleDao vehicleDao;
	private final ApplicationFileDao applicationFileDao;

	@Override
	public Collection<Vehicle> findAllVehicle() {
		return (Collection<Vehicle>) vehicleDao.findAll();
	}

	@Override
	public Vehicle saveVehicle(Vehicle vehicle) {
		return vehicleDao.save(vehicle);
	}

	@Override
	public Vehicle findByImmat(String immat) {
		return vehicleDao.findByImmatriculation(immat);
	}

	@Override
	public Long deleteVehicle(String immat) {
		return vehicleDao.removeByImmatriculation(immat);
	}

	@Override
	@Scheduled(cron = "0 0 0 1 * *") // Execute at midnight on the 1st day of every month
	public void updateVehiclesQuotaMonthly() {
		logger.info("Starting monthly vehicle quota update...");

		try {
			vehicleDao.updateMonthlyQuota();
			logger.info("Monthly vehicle quota update completed successfully. All vehicles reset to quota of 12.");
		} catch (Exception e) {
			logger.error("Failed to update monthly vehicle quota", e);
		}
	}

	@Override
	public Collection<VehicleDTO> findVehicleByCode(Long AppFileId) {
		Category category = applicationFileDao.findById(AppFileId)
				.map(ApplicationFile::getCategory)
				.orElseThrow(() -> new EntityNotFoundException("ApplicationFile not found with id: " + AppFileId));

		Collection<Vehicle> vehicles = vehicleDao.findVehicleByCategory(category.getCode());

		return vehicles.stream()
				.map(VehicleDTO::fromEntity)
				.collect(Collectors.toList());
	}
}