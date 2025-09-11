package com.autoecole.mapper;

import org.springframework.stereotype.Component;
import com.autoecole.model.TechnicalVisit;
import com.autoecole.model.Vehicle;

@Component
public class TechnicalVisitMapper {

    public TechnicalVisit toEntity(TechnicalVisit source, Vehicle vehicle) {
        if (source == null || vehicle == null) {
            return null;
        }

        TechnicalVisit target = new TechnicalVisit();
        target.setAmount(source.getAmount());
        target.setCompany(source.getCompany());
        target.setNextOperationDate(source.getNextOperationDate());
        target.setOperationDate(source.getOperationDate());
        target.setVehicle(vehicle);

        return target;
    }

    public void updateEntity(TechnicalVisit target, TechnicalVisit source) {
        if (source == null || target == null) {
            return;
        }

        target.setAmount(source.getAmount());
        target.setCompany(source.getCompany());
        target.setNextOperationDate(source.getNextOperationDate());
        target.setOperationDate(source.getOperationDate());
        // Note: vehicle is ignored in update
    }
}