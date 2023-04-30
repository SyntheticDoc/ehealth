create table test(
    id int
);

CREATE TABLE IF NOT EXISTS users
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255),
    address     VARCHAR(5000),
    phone       BIGINT,
    emergency   BOOLEAN,
    password    VARCHAR(512)
);

CREATE TABLE IF NOT EXISTS settings_ecgstateholder
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE,
    user_id     BIGINT,
    iterations_transition   INT,
    iterations_emergency    INT
);

CREATE TABLE IF NOT EXISTS settings_ecganalysis
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE,
    user_id     BIGINT,
    maxDeviation    INT,
    maxDeviation_num INT
);

CREATE TABLE IF NOT EXISTS settings
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE,
    user_id     BIGINT,
    FOREIGN KEY (ecgstateholder_settings) REFERENCES settings_ecgstateholder ON DELETE SET NULL ON UPDATE CASCADE,
    ecgstateholder_settings BIGINT,
    FOREIGN KEY (ecganalysis_settings) REFERENCES settings_ecganalysis ON DELETE SET NULL ON UPDATE CASCADE,
    ecganalysis_settings BIGINT
);

CREATE TABLE IF NOT EXISTS ECGComponent
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    identifier  VARCHAR(64),
    name        VARCHAR(1000)
);

CREATE TABLE IF NOT EXISTS ECGComponentData
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    test        VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS ECGDevice
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    identifier  VARCHAR(64),
    name        VARCHAR(1000),
    leads       TINYINT,
    pin         VARCHAR(15),
    components  BIGINT ARRAY
);

CREATE TABLE IF NOT EXISTS ECGData
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    timestamp   TIMESTAMP NOT NULL,
    deviceName  VARCHAR(5000),
    -- FOREIGN KEY (componentIds) REFERENCES ECGComponent(id) ON DELETE SET NULL ON UPDATE CASCADE,
    componentIds    BIGINT ARRAY,
    -- FOREIGN KEY (dataIds) REFERENCES ECGComponentData(id) ON DELETE SET NULL ON UPDATE CASCADE,
    dataIds    BIGINT ARRAY
);