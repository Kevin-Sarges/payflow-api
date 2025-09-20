package com.kevinsarges.payflow_api.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusUpdateDTO {
    @NotNull
    private String novoStatus;
}
