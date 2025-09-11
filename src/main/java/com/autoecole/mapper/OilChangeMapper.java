package com.autoecole.mapper;

import org.springframework.stereotype.Component;
import com.autoecole.model.OilChange;
import com.autoecole.model.Vehicle;

@Component
public class OilChangeMapper {

    public OilChange toEntity(OilChange source, Vehicle vehicle) {
        if (source == null || vehicle == null) {
            return null;
        }

        OilChange target = new OilChange();
        target.setAmount(source.getAmount());
        target.setCompany(source.getCompany());
        target.setNextOperationDate(source.getNextOperationDate());
        target.setOperationDate(source.getOperationDate());
        target.setVehicle(vehicle);

        return target;
    }

    public void updateEntity(OilChange target, OilChange source) {
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