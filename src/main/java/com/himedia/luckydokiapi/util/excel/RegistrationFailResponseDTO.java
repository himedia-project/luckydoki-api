package com.himedia.luckydokiapi.util.excel;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RegistrationFailResponseDTO {
    private final int row;
    private final String errorMessage;

    public RegistrationFailResponseDTO(int row, String errorMessage) {
        this.row = row;
        this.errorMessage = errorMessage;
    }
}
