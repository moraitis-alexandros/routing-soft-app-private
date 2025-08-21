package org.routing.software.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseMessageDto {

    private String code;
    private String description;

    public ResponseMessageDto(String code) {
        this.code = code;
        this.description = "";
    }
}

