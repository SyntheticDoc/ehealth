package ehealth.group1.backend.entity;

/** Contains information to connect a registered ECGDevice to a registered User.
 *
 */
public class ConnectDeviceData {
    String userName;
    String password;
    String regDeviceName;
    String regDevicePin;

    public ConnectDeviceData() {}

    public ConnectDeviceData(String userName, String password, String regDeviceName, String regDevicePin) {
        this.userName = userName;
        this.password = password;
        this.regDeviceName = regDeviceName;
        this.regDevicePin = regDevicePin;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegDeviceName() {
        return regDeviceName;
    }

    public void setRegDeviceName(String regDeviceName) {
        this.regDeviceName = regDeviceName;
    }

    public String getRegDevicePin() {
        return regDevicePin;
    }

    public void setRegDevicePin(String regDevicePin) {
        this.regDevicePin = regDevicePin;
    }

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
