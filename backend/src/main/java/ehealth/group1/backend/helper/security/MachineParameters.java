package ehealth.group1.backend.helper.security;

import ehealth.group1.backend.entity.SecurityData;
import ehealth.group1.backend.repositories.SecurityDataRepository;
import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

import java.lang.invoke.MethodHandles;

@Getter
public class MachineParameters {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Long availableMem;
    private String osVersion;
    private String hwUUID;
    private String cpuIdentifier;
    private Integer cpuLogicalThreads;

    // Set Getter access level to none for this to prevent auto-generation of isHasDbInfo() and use own Getter instead
    @Getter(AccessLevel.NONE)
    private boolean hasDBInfo;

    @Getter(AccessLevel.NONE)
    private final SecurityDataRepository dataRepository;

    public MachineParameters(SecurityDataRepository dataRepository) {
        this(dataRepository, false);
    }

    public MachineParameters(SecurityDataRepository dataRepository, boolean fillFromSystem) {
        this.dataRepository = dataRepository;

        if(fillFromSystem) {
            fillFromSystem();
        } else {
            fillFromRepo();
        }
    }

    public boolean matches(MachineParameters p) {
        return availableMem.equals(p.availableMem) && osVersion.equals(p.osVersion) && hwUUID.equals(p.hwUUID) &&
                cpuIdentifier.equals(p.cpuIdentifier) && cpuLogicalThreads.equals(p.cpuLogicalThreads);
    }

    private void fillFromRepo() {
        LOGGER.debug("Trying to get machine parameters from database...");

        try {
            availableMem = Long.parseLong(dataRepository.findByType("SYS_availableMem").getVal());
            osVersion = dataRepository.findByType("SYS_osVersion").getVal();
            hwUUID = dataRepository.findByType("SYS_hardwareUUID").getVal();
            cpuIdentifier = dataRepository.findByType("SYS_cpuIdentifier").getVal();
            cpuLogicalThreads = Integer.parseInt(dataRepository.findByType("SYS_cpuLogicalThreads").getVal());
            hasDBInfo = true;
        } catch(NullPointerException e) {
            LOGGER.warn("MachineParameters.fillFromRepo(): Could not find any machine parameters in database.");
            hasDBInfo = false;
        }
    }

    public void fillFromSystem() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        CentralProcessor cpu = hal.getProcessor();

        availableMem = (long) (Runtime.getRuntime().maxMemory() * 0.001);
        osVersion = systemInfo.getOperatingSystem().getManufacturer() + " " + systemInfo.getOperatingSystem().getFamily() + " " +
                systemInfo.getOperatingSystem().getVersionInfo().toString();
        hwUUID = systemInfo.getHardware().getComputerSystem().getHardwareUUID();
        cpuIdentifier =  cpu.getProcessorIdentifier().toString() + " " + cpu.getProcessorIdentifier().getName();
        cpuLogicalThreads = cpu.getLogicalProcessorCount();
    }

    public void saveToRepo() {
        if(isEmpty()) {
            LOGGER.error("Can't save current machine parameters since they contain null values!");
            printToLogger();
            return;
        }

        printToLogger();

        LOGGER.info("Saving machine parameters used for current Argon2 parameter settings...");

        SecurityData[] secData = new SecurityData[5];
        secData[0] = new SecurityData("SYS_availableMem", String.valueOf(availableMem));
        secData[1] = new SecurityData("SYS_osVersion", osVersion);
        secData[2] = new SecurityData("SYS_hardwareUUID", hwUUID);
        secData[3] = new SecurityData("SYS_cpuIdentifier", cpuIdentifier);
        secData[4] = new SecurityData("SYS_cpuLogicalThreads", String.valueOf(cpuLogicalThreads));

        for(SecurityData s : secData) {
            dataRepository.save(s);
        }

        LOGGER.info("Machine parameters successfully saved!");
    }

    public void printToLogger() {
        String s1 = String.format("%34s: %d\n", "Available Java VM-memory in KiBi", availableMem);
        String s2 = String.format("%34s: %s\n", "OS version", osVersion);
        String s3 = String.format("%34s: %s\n", "Hardware UUID", hwUUID);
        String s4 = String.format("%34s: %s\n", "CPU identifier", cpuIdentifier);
        String s5 = String.format("%34s: %d\n", "CPU Logical Threads", cpuLogicalThreads);

        LOGGER.info("Current machine specs:\n\n" + s1 + s2 + s3 + s4 + s5);
    }

    public boolean isEmpty() {
        return availableMem == null || osVersion == null || hwUUID == null || cpuIdentifier == null || cpuLogicalThreads == null;
    }

    public boolean hasDBInfo() {
        return hasDBInfo;
    }
}
