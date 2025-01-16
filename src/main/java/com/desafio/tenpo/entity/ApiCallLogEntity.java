package com.desafio.tenpo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("api_call_log_entity")
public class ApiCallLogEntity {
    @Id
    private Long id;
    private LocalDateTime timestamp;
    private String endpoint;
    private String parameters;
    private String response;
    private String error;
}

