package ehealth.group1.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Contains information to connect a registered ECGDevice to a registered User.
 *
 */
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class ConnectDeviceData {
    String userName;
    String password;
    String regDeviceName;
    String regDevicePin;

    @Override
    public String toString() {
        return "ConnectDeviceData[" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", regDeviceName='" + regDeviceName + '\'' +
                ", regDevicePin='" + regDevicePin + '\'' +
                ']';
    }
}
