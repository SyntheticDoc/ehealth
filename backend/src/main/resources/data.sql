insert into test (id) values (1);
insert into test (id) values (2);
insert into test (id) values (3);

-- TODO: Remove deletion lines
DELETE FROM users where id=0;
DELETE FROM settings_ecgstateholder where id=0;
DELETE FROM settings_ecganalysis where id=0;
DELETE FROM settings where id=0;

INSERT INTO users (id, name)
VALUES (0, 'TestUser')
;

INSERT INTO settings_ecgstateholder(id, user_id, iterations_transition, iterations_emergency)
VALUES (0, 0, 3, 5)
;

INSERT INTO settings_ecganalysis(id, user_id, maxDeviation, maxDeviation_num)
VALUES (0, 0, 5, 10)
;

INSERT INTO settings(id, user_id, ecgstateholder_settings, ecganalysis_settings)
VALUES (0, 0, 0, 0)
;