package com.desafio.tenpo.adapter;

import com.desafio.tenpo.domain.ApiCallLogDTO;
import com.desafio.tenpo.entity.ApiCallLogEntity;
import org.springframework.stereotype.Component;

@Component
public class ApiCallLogAdapter {

    public ApiCallLogEntity toEntity(ApiCallLogDTO apiCallLogDTO){
        return ApiCallLogEntity.builder()
                .id(apiCallLogDTO.getId())
                .timestamp(apiCallLogDTO.getTimestamp())
                .endpoint(apiCallLogDTO.getEndpoint())
                .parameters(apiCallLogDTO.getParameters())
                .response(apiCallLogDTO.getResponse())
                .error(apiCallLogDTO.getError())
                .build();
    }

}
