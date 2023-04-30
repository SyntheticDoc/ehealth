package ehealth.group1.backend.service;

import ehealth.group1.backend.entity.*;
import ehealth.group1.backend.generators.IDStringGenerator;
import ehealth.group1.backend.persistence.DataDao;
import ehealth.group1.backend.persistence.DeviceDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class ConnectionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    DataDao dataDao;
    DeviceDao deviceDao;

    public ConnectionService(DataDao dataDao, DeviceDao deviceDao) {
        this.dataDao = dataDao;
        this.deviceDao = deviceDao;
    }

    public ECGDevice registerECGDevice(ECGDevice ecgDevice) {
        ecgDevice.setIdentifier(IDStringGenerator.getNewIDString());

        for(ECGDeviceComponent c : ecgDevice.getComponents()) {
            c.setIdentifier(IDStringGenerator.getNewIDString());
        }

        deviceDao.createECGDevice(ecgDevice);

        System.out.println("\n\nMock registering: " + ecgDevice + "\n");
        return ecgDevice;
    }

    public FrontendDevice registerFrontendDevice(FrontendDevice frontendDevice) {
        frontendDevice.setIdentifier(IDStringGenerator.getNewIDString());

        System.out.println("\n\nMock registering: " + frontendDevice + "\n");
        return frontendDevice;
    }

    public void registerUser(User user) {
        dataDao.createUser(user);
    }

    public void connectECGDeviceToUser(ConnectDeviceData data) {
        ECGDevice device = deviceDao.getDeviceByNameAndPin(data.getRegDeviceName(), data.getRegDevicePin()).get(0);
        LOGGER.warn("ConnectionService.connectECGDeviceToUser(): " + device);
    }
}
