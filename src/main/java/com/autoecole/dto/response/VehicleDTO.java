package com.autoecole.dto.response;

import com.autoecole.model.Vehicle;
import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class VehicleDTO {
    private String immatriculation;
    private int quota;
    private String vehicleBrand;

    public static VehicleDTO fromEntity(Vehicle vehicle) {
        return VehicleDTO.builder()
                .immatriculation(vehicle.getImmatriculation())
                .quota(vehicle.getQuota())
                .vehicleBrand(vehicle.getVehicleBrand())
                .build();
    }
}

