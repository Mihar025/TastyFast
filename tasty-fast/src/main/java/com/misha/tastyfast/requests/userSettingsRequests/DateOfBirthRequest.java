package com.misha.tastyfast.requests.userSettingsRequests;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DateOfBirthRequest {
    @JsonFormat(pattern = "yyyy-MM:dd")
    private LocalDateTime dateOfBirth;

}
