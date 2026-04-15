package org.example.servicioemployee.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "clientId",
        "name",
        "surname",
        "mailPlexus",
        "mailClient",
        "phoneSn",
        "phoneNumber"
})

public class Response {
    private Integer id;
    private String name;
    private String surname;
    private String mailPlexus;
    private String clientId;
    private String mailClient;
    private String phoneNumber;
    private String phoneSn;
}