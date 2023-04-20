create table test(
    id int
);

CREATE TABLE IF NOT EXISTS users
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255)
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