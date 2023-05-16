package ehealth.group1.backend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class holds all important information describing a frontend request to get the last health status.
 *
 * userName and password is necessary to ensure that a user can get only his/her own data.
 * deviceIdentifier is necessary to fetch the data of a specific ECGDevice of the user, in case he/she uses multiple
 * ECGDevices.
 */
@NoArgsConstructor
@Getter @Setter
public class RequestLastHealthStatus {
    private String userName;
    private String password;
    private String deviceIdentifier;
}
