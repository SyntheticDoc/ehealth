package ehealth.group1.backend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter @Setter
public class RequestDatasets {
    private String userName;
    private String password;
    private String deviceIdentifier;
    private LocalDateTime end;
    private int seconds;
}
