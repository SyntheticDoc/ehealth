package ehealth.group1.backend.helper.security;

import ehealth.group1.backend.entity.Argon2Parameters;
import ehealth.group1.backend.entity.SecurityData;
import ehealth.group1.backend.helper.argon2crypto.Argon2ParameterChecker;
import ehealth.group1.backend.repositories.Argon2ParametersRepository;
import ehealth.group1.backend.repositories.SecurityDataRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Base64;

@Component
@Transactional
public class SecurityHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final Argon2ParameterChecker argon2ParameterChecker;
    private final Argon2ParametersRepository argon2ParametersRepository;
    private final SecurityDataRepository securityDataRepository;
    private final Environment env;
    private PlatformTransactionManager transactionManager;

    private final int saltLength = 128;

    public SecurityHelper(Argon2ParameterChecker argon2ParameterChecker, Argon2ParametersRepository argon2ParametersRepository,
                          SecurityDataRepository securityDataRepository, Environment env, PlatformTransactionManager transactionManager) {
        this.argon2ParameterChecker = argon2ParameterChecker;
        this.argon2ParametersRepository = argon2ParametersRepository;
        this.securityDataRepository = securityDataRepository;
        this.env = env;
        this.transactionManager = transactionManager;
    }

    @SuppressWarnings("unchecked")
    @PostConstruct
    @Transactional
    public void checkArgon2Parameters() {
        LOGGER.info("Checking Argon2 parameters...");

        // If in development mode, use only a reduced set of parameters
        if (Arrays.asList(env.getActiveProfiles()).contains("dev")) {
            useReducedArgon2Parameters();
            return;
        }

        if(false) {
            securityDataRepository.deleteAll();
        }

        // Check if the parameters of the current machine have changed or are not yet created
        MachineParameters mParamsFromRepo = new MachineParameters(securityDataRepository);
        MachineParameters mParamsFromSystem = new MachineParameters(securityDataRepository, true);
        boolean hasNewMachineParams = false;

        if(!mParamsFromRepo.hasDBInfo()) {
            LOGGER.debug("No machine parameters found in database, saving current parameters.");
            mParamsFromSystem.saveToRepo();
            hasNewMachineParams = true;
        } else {
            // Check if system changed since parameters were last saved
            if(!mParamsFromSystem.matches(mParamsFromRepo)) {
                LOGGER.warn("Machine parameters seem to have changed! Saving new machine parameters...");

                // Delete old machineParams, need TransactionManager to open new hibernate session, devil knows why...
                new TransactionTemplate(transactionManager).execute((TransactionCallback) transactionStatus -> {

                    securityDataRepository.deleteByType("SYS_availableMem");
                    securityDataRepository.deleteByType("SYS_osVersion");
                    securityDataRepository.deleteByType("SYS_hardwareUUID");
                    securityDataRepository.deleteByType("SYS_cpuIdentifier");
                    securityDataRepository.deleteByType("SYS_cpuLogicalThreads");

                    return null;
                });

                // Delete old machineParams
                /*securityDataRepository.deleteByType("SYS_availableMem");
                securityDataRepository.deleteByType("SYS_osVersion");
                securityDataRepository.deleteByType("SYS_hardwareUUID");
                securityDataRepository.deleteByType("SYS_cpuIdentifier");
                securityDataRepository.deleteByType("SYS_cpuLogicalThreads");*/

                mParamsFromSystem.saveToRepo();
                hasNewMachineParams = true;
            } else {
                LOGGER.debug("Machine parameters in database match current machine parameters, no change needed.");
                hasNewMachineParams = false;
            }
        }

        if (argon2ParametersRepository.count() != 2) {
            LOGGER.info("Required parameters do not exist");
            getNewArgon2Parameters();
        } else if (hasNewMachineParams) {
            LOGGER.info("Generating new parameters for changed machine parameters");
            getNewArgon2Parameters();
        } else {
            LOGGER.info("Argon2 fast parameters found: " + argon2ParametersRepository.findByType("fast"));
            LOGGER.info("Argon2 default parameters found: " + argon2ParametersRepository.findByType("default"));
        }

        LOGGER.info("Checking cryptographic salts...");

        if (securityDataRepository.findByType("saltFast") == null) {
            LOGGER.info("No fast salt value found. A new salt will now be generated. If you believe this is an error, please "
                    + "shut down the server immediately and call your server administrator!");
            generateNewSalt("saltFast");
        } else {
            LOGGER.debug("\n\nSALT VALUE (fast): " + securityDataRepository.findByType("saltFast").getVal() + "\n");
        }

        if (securityDataRepository.findByType("saltDefault") == null) {
            LOGGER.info("No default salt value found. A new salt will now be generated. If you believe this is an error, please "
                    + "shut down the server immediately and call your server administrator!");
            generateNewSalt("saltDefault");
        } else {
            LOGGER.debug("\n\nSALT VALUE (default): " + securityDataRepository.findByType("saltDefault").getVal() + "\n");
        }
    }

    public void useReducedArgon2Parameters() {
        LOGGER.info("Creating reduced Argon2 parameters...");
        argon2ParametersRepository.deleteAll();
        argon2ParametersRepository.flush();
        argon2ParametersRepository.save(getStandardParameters("fast"));
        argon2ParametersRepository.save(getStandardParameters("default"));
        argon2ParametersRepository.flush();

        String salt = "w4NqRcIeIfMBbM1KYGz4abkPepmf0K1pJPD6K/0KcpmdUz+1lMm89woNAs42Y9BuQmFS0JZbr9gyyr/jeuPcc9z36ZchYpI2+"
                + "+tDlSwmhPi2e1UrYt4gtQUHf5E0CfyeF60lLGFrpcinDWmRtdOCNYLIJJvNyFoVMjVfdXoUEaY=";

        SecurityData saltData1 = new SecurityData();
        saltData1.setType("saltFast");
        saltData1.setVal(salt);

        SecurityData saltData2 = new SecurityData();
        saltData2.setType("saltDefault");
        saltData2.setVal(salt);

        securityDataRepository.deleteAll();
        securityDataRepository.flush();
        securityDataRepository.save(saltData1);
        securityDataRepository.save(saltData2);
        securityDataRepository.flush();

        LOGGER.info("Argon2 fast parameters found: " + argon2ParametersRepository.findByType("fast"));
        LOGGER.info("Argon2 default parameters found: " + argon2ParametersRepository.findByType("default"));
        LOGGER.info("Fast salt: " + securityDataRepository.findByType("fast").getVal());
        LOGGER.info("Default salt: " + securityDataRepository.findByType("default").getVal());
    }

    private void getNewArgon2Parameters() {
        LOGGER.info("Creating new Argon2 parameters...");

        argon2ParametersRepository.deleteAll();
        argon2ParametersRepository.flush();

        Argon2Parameters fastParams;

        LOGGER.info("Calculating Argon2 fast parameters...");
        fastParams = argon2ParameterChecker.getParametersForExecutionTime(300, saltLength, 128,
                3, 4, 2);

        Argon2Parameters defaultParams;

        LOGGER.info("Calculating Argon2 default parameters...");
        defaultParams = argon2ParameterChecker.getParametersForExecutionTime(1000, saltLength, 128,
                3, 4, 2);

        if (fastParams != null) {
            fastParams.setType("fast");
            argon2ParametersRepository.save(fastParams);
        } else {
            LOGGER.error("Could not get new argon2 parameters for fast computations! Setting standard parameters. WARNING: This is unsafe, "
                    + "please try again as soon as possible!");
            argon2ParametersRepository.save(getStandardParameters("default"));
        }

        if (defaultParams != null) {
            defaultParams.setType("default");
            argon2ParametersRepository.save(defaultParams);
        } else {
            LOGGER.error("Could not get new default argon2 parameters! Setting standard parameters. WARNING: This is unsafe, "
                    + "please try again as soon as possible!");
            argon2ParametersRepository.save(getStandardParameters("default"));
        }

        argon2ParametersRepository.flush();
    }

    private Argon2Parameters getStandardParameters(String type) {
        Argon2Parameters params = new Argon2Parameters();

        params.setType(type);
        params.setArgonType("Argon2id");
        params.setSaltLength(saltLength);
        params.setHashLength(128);
        params.setParallelism(1);
        params.setMemoryCost(256);
        params.setIterations(1);

        return params;
    }

    private void generateNewSalt(String saltType) {
        LOGGER.info("Generating new salt of type " + saltType + "...");
        byte[] salt = argon2ParameterChecker.getNewSalt(saltLength);

        String saltBase64 = Base64.getEncoder().encodeToString(salt);

        SecurityData saltData = new SecurityData();
        saltData.setType(saltType);
        saltData.setVal(saltBase64);

        LOGGER.warn("\n#########################################\n\nYour salt value: " + saltBase64 + "\n\nPlease "
                + "write this value down at a very secure place. Without this value, all hashed data in your database will be lost forever "
                + "if you lose it!\n\n#########################################");

        securityDataRepository.saveAndFlush(saltData);
    }
}
