package ehealth.group1.backend.helper.dataloaders;

import ehealth.group1.backend.entity.*;
import ehealth.group1.backend.generators.IDStringGenerator;
import ehealth.group1.backend.helper.argon2crypto.Argon2ParameterChecker;
import ehealth.group1.backend.helper.argon2crypto.Argon2PasswordEncoderWithParams;
import ehealth.group1.backend.helper.security.SecurityHelper;
import ehealth.group1.backend.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.awt.*;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class DefaultDataLoader implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final double maxDeviation = 0.1;
    private final double maxDeviationNum = 5.0;
    private final int iterations_transition = 3;
    private final int iterations_emergency = 5;

    private final Argon2ParametersRepository argon2ParametersRepository;
    private final DataRepository dataRepository;
    private final ECGAnalysisRepository ecgAnalysisRepository;
    private final DeviceRepository deviceRepository;
    private final SecurityDataRepository securityDataRepository;
    private final SettingsRepository settingsRepository;
    private final UserRepository userRepository;
    private Argon2PasswordEncoderWithParams argon2PasswordEncoderWithParams;
    private PlatformTransactionManager transactionManager;
    private Environment env;
    private SecurityHelper securityHelper;

    public DefaultDataLoader(Argon2ParametersRepository argon2ParametersRepository, DataRepository dataRepository,
                             ECGAnalysisRepository ecgAnalysisRepository, DeviceRepository deviceRepository,
                             SecurityDataRepository securityDataRepository, SettingsRepository settingsRepository,
                             UserRepository userRepository, Argon2PasswordEncoderWithParams argon2PasswordEncoderWithParams,
                             PlatformTransactionManager transactionManager, Environment env, SecurityHelper securityHelper) {
        this.argon2ParametersRepository = argon2ParametersRepository;
        this.dataRepository = dataRepository;
        this.ecgAnalysisRepository = ecgAnalysisRepository;
        this.deviceRepository = deviceRepository;
        this.securityDataRepository = securityDataRepository;
        this.settingsRepository = settingsRepository;
        this.userRepository = userRepository;
        this.argon2PasswordEncoderWithParams = argon2PasswordEncoderWithParams;
        this.transactionManager = transactionManager;
        this.env = env;
        this.securityHelper = securityHelper;
    }

    /**
     * This code is executed when the Spring Boot application starts up. It ensures that default values are injected
     * into the database before any other components are built.
     *
     * @param args Application arguments
     * @throws Exception when some error happens
     */
    @Override
    public void run(String... args) throws Exception {
        LOGGER.debug("\n\nPre component initialization run() called!\n");
        boolean isDev = false;

        for(String profile : env.getActiveProfiles()) {
            if(profile.equalsIgnoreCase("dev")) {
                isDev = true;
            }
        }

        if(isDev) {
            devSettings();
        } else {
            defaultSettings();
        }
    }

    public void defaultSettings() {
        LOGGER.info("Loading default settings.");
        Settings settings = settingsRepository.findByUserId(0L);
        if(settings != null) {
            new TransactionTemplate(transactionManager).execute((TransactionCallback) transactionStatus -> {

                settingsRepository.delete(settings);

                return null;
            });
        }

        ECGAnalysisSettings analysisSettings = new ECGAnalysisSettings(0L, maxDeviation, maxDeviationNum);
        ECGStateHolderSettings stateHolderSettings = new ECGStateHolderSettings(0L, iterations_transition, iterations_emergency);
        Settings s = new Settings(0L, stateHolderSettings, analysisSettings);
        settingsRepository.save(s);
    }

    public void devSettings() {
        LOGGER.info("Loading dev settings. Deleting all data in database...");

        new TransactionTemplate(transactionManager).execute((TransactionCallback) transactionStatus -> {

            argon2ParametersRepository.deleteAll();
            dataRepository.deleteAll();
            ecgAnalysisRepository.deleteAll();
            deviceRepository.deleteAll();
            securityDataRepository.deleteAll();
            settingsRepository.deleteAll();
            userRepository.deleteAll();
            // Todo: Check if it is really necessary to call flush() on all repos after deleteAll()
            argon2ParametersRepository.flush();
            dataRepository.flush();
            ecgAnalysisRepository.flush();
            deviceRepository.flush();
            securityDataRepository.flush();
            settingsRepository.flush();
            userRepository.flush();

            return null;
        });

        LOGGER.info("Dev settings: All database data successfully deleted.");
        LOGGER.info("Generating default user...");
        User defaultUser = new User();
        defaultUser.setId(0L);
        defaultUser.setName("DefaultUser");
        defaultUser.setAddress("DefaultUserAddress");
        defaultUser.setPassword("");
        defaultUser.setEmergency(true);
        defaultUser.setPhone(987654321L);
        defaultUser = userRepository.saveAndFlush(defaultUser);

        LOGGER.info("defaultUser id=" + defaultUser.getId());

        LOGGER.info("Generating new Settings...");

        ECGAnalysisSettings analysisSettings = new ECGAnalysisSettings(0L, maxDeviation, maxDeviationNum);
        ECGStateHolderSettings stateHolderSettings = new ECGStateHolderSettings(0L, iterations_transition, iterations_emergency);
        Settings s = new Settings(0L, stateHolderSettings, analysisSettings);
        settingsRepository.save(s);

        LOGGER.info("Getting new Argon2 parameters...");
        securityHelper.checkArgon2Parameters();
        LOGGER.info("Generating devices for example users...");
        ECGDeviceComponent c1 = new ECGDeviceComponent(), c2 = new ECGDeviceComponent(), c3 = new ECGDeviceComponent();
        c1.setSelfID("c1");
        c1.setName("Lead c1");
        c1.setIdentifier(IDStringGenerator.getNewIDString());
        c2.setSelfID("c2");
        c2.setName("Lead c2");
        c2.setIdentifier(IDStringGenerator.getNewIDString());
        c3.setSelfID("c3");
        c3.setName("Lead c3");
        c3.setIdentifier(IDStringGenerator.getNewIDString());

        List<ECGDeviceComponent> d1Comps = new ArrayList<>();
        List<ECGDeviceComponent> d2Comps = new ArrayList<>();

        d1Comps.add(c1);
        d2Comps.add(c2);
        d2Comps.add(c3);

        ECGDevice user1Device = new ECGDevice(), user2Device = new ECGDevice();
        user1Device.setSelfID("user1DeviceSelfID");
        user1Device.setIdentifier(IDStringGenerator.getNewIDString());
        user1Device.setName("user1Device");
        user1Device.setLeads(1);
        user1Device.setComponents(d1Comps);
        user1Device.setPin("123");
        user2Device.setSelfID("user2DeviceSelfID");
        user2Device.setIdentifier(IDStringGenerator.getNewIDString());
        user2Device.setName("user2Device");
        user2Device.setLeads(2);
        user2Device.setComponents(d2Comps);
        user2Device.setPin("123");

        List<ECGDevice> user1Devices = new ArrayList<>();
        List<ECGDevice> user2Devices = new ArrayList<>();
        user1Devices.add(user1Device);
        user2Devices.add(user2Device);

        Argon2Parameters params = new Argon2Parameters();

        params.setType(Argon2ParameterChecker.getParamNameSlow());
        params.setArgonType("Argon2id");
        params.setSaltLength(128);
        params.setHashLength(128);
        params.setParallelism(1);
        params.setMemoryCost(256);
        params.setIterations(1);

        String salt = "w4NqRcIeIfMBbM1KYGz4abkPepmf0K1pJPD6K/0KcpmdUz+1lMm89woNAs42Y9BuQmFS0JZbr9gyyr/jeuPcc9z36ZchYpI2+"
                + "+tDlSwmhPi2e1UrYt4gtQUHf5E0CfyeF60lLGFrpcinDWmRtdOCNYLIJJvNyFoVMjVfdXoUEaY=";

        LOGGER.info("Generating example users...");
        User user1 = new User();
        user1.setName("User Userman1");
        user1.setAddress("Userstraße 42/Stiege -5/Top 0, 1404 Wien");
        user1.setPassword(argon2PasswordEncoderWithParams.encode("pwd", params, salt));
        user1.setEmergency(true);
        user1.setPhone(1234L);
        user1.setDevices(user1Devices);

        User user2 = new User();
        user2.setName("User Userman2");
        user2.setAddress("Nutzerstraße 23/Stiege 2001/Top inf, 1404 Wien");
        user2.setPassword(argon2PasswordEncoderWithParams.encode("pwd2", params, salt));
        user2.setEmergency(true);
        user2.setPhone(6789L);
        user2.setDevices(user2Devices);

        User user3 = new User();
        user3.setName("User Userman3");
        user3.setAddress("ESPStraße 32/Stiege 32/Top 32, 1404 Wien");
        user3.setPassword(argon2PasswordEncoderWithParams.encode("pwd3", params, salt));
        user3.setEmergency(true);
        user3.setPhone(3223L);

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);
        user3 = userRepository.save(user3);
        userRepository.flush();

        // LOGGER.info("(User1 id=" + user1.getId() + ", User2 id=" + user2.getId() +")");

        testDevData();
    }

    public GraphicsSettings getGraphicsSettings() {
        int titleBarSize = 30;
        int canvas_x_size = 1600;
        int canvas_y_size = 800;
        double lineThickness_dividerLines = 0.005;
        double lineThickness_ecgGraph = 0.005;
        Color background = Color.WHITE;
        Color titleBar = Color.LIGHT_GRAY;
        Color base = Color.BLACK;
        Color ecgGraph = Color.RED;
        Color text = Color.BLACK;
        Font leadName = new Font("Arial", Font.BOLD, 18);
        Font timestamp = new Font("Arial", Font.BOLD, 14);

        boolean useDoubleBuffering = true;

        GraphicsSettings settings = new GraphicsSettings();

        settings.setTitleBarSize(titleBarSize);
        settings.setCanvas_x_size(canvas_x_size);
        settings.setCanvas_y_size(canvas_y_size);
        settings.setLineThickness_dividerLines(lineThickness_dividerLines);
        settings.setLineThickness_ecgGraph(lineThickness_ecgGraph);
        settings.setBackground(background);
        settings.setTitleBar(titleBar);
        settings.setBase(base);
        settings.setEcgGraph(ecgGraph);
        settings.setText(text);
        settings.setFont_leadName(leadName);
        settings.setFont_timestamp(timestamp);
        settings.setUseDoubleBuffering(useDoubleBuffering);

        return settings;
    }

    private void testDevData() {
        User user1 = userRepository.findByName("User Userman1");
        User user2 = userRepository.findByName("User Userman2");
        Settings settings = settingsRepository.findByUserId(0L);

        LOGGER.info("Test retrieving dev users:\n" + user1 + "\n\n" + user2 + "\n");
        LOGGER.info("Test retrieving default settings:\n" + settings.toString());
    }
}
