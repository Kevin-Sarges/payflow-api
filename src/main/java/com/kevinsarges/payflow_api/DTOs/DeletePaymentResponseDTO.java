package com.kevinsarges.payflow_api.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class DeletePaymentResponseDTO {
    private Integer statusCode;
    private String message;
    private LocalDateTime timestamp;
}
