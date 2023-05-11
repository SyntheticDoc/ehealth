package ehealth.group1.backend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class RequestLastHealthStatus {
    private String userName;
    private String password;
    private String deviceIdentifier;
}
