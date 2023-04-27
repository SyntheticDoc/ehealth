package ehealth.group1.backend.service;

import ehealth.group1.backend.entity.ECGDevice;
import ehealth.group1.backend.entity.ECGDeviceComponent;
import ehealth.group1.backend.entity.FrontendDevice;
import ehealth.group1.backend.generators.IDStringGenerator;
import ehealth.group1.backend.persistence.DataDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class ConnectionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    DataDao dataDao;

    public ConnectionService(DataDao dataDao) {
        this.dataDao = dataDao;
    }

    public ECGDevice registerECGDevice(ECGDevice ecgDevice) {
        ecgDevice.setIdentifier(IDStringGenerator.getNewIDString());

        for(ECGDeviceComponent c : ecgDevice.getComponents()) {
            c.setIdentifier(IDStringGenerator.getNewIDString());
        }

        System.out.println("\n\nMock registering: " + ecgDevice + "\n");
        return ecgDevice;
    }

    public FrontendDevice registerFrontendDevice(FrontendDevice frontendDevice) {
        frontendDevice.setIdentifier(IDStringGenerator.getNewIDString());

        System.out.println("\n\nMock registering: " + frontendDevice + "\n");
        return frontendDevice;
    }
}
