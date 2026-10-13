CREATE TABLE users
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(100)        NOT NULL,
    email      VARCHAR(150) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE diagnosis_sessions
(
    id              SERIAL PRIMARY KEY,
    user_id         INT REFERENCES users (id),
    final_diagnosis VARCHAR(200),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE session_facts
(
    id         SERIAL PRIMARY KEY,
    session_id INT REFERENCES diagnosis_sessions (id),
    fact_name  VARCHAR(100) NOT NULL,
    fact_value VARCHAR(100) NOT NULL
);

CREATE TABLE rules
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    priority    INT     DEFAULT 3,
    enabled     BOOLEAN DEFAULT TRUE
);

CREATE TABLE rule_conditions
(
    id             SERIAL PRIMARY KEY,
    rule_id        INT REFERENCES rules (id) ON DELETE CASCADE,
    fact_name      VARCHAR(100) NOT NULL,
    operator       VARCHAR(20)  NOT NULL,
    expected_value VARCHAR(100) NOT NULL
);

CREATE TABLE rule_conclusions
(
    id               SERIAL PRIMARY KEY,
    rule_id          INT REFERENCES rules (id) ON DELETE CASCADE,
    conclusion_name  VARCHAR(100) NOT NULL,
    conclusion_value VARCHAR(200) NOT NULL
);

CREATE TABLE matched_rules
(
    id         SERIAL PRIMARY KEY,
    session_id INT REFERENCES diagnosis_sessions (id),
    rule_id    INT REFERENCES rules (id),
    matched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


WITH new_rule AS (
    INSERT INTO rules (name, description, priority, enabled)
        VALUES (
                   'WiFi Connected But No Internet',
                   'The Wi-Fi is connected, but internet access is unavailable.',
                   1,
                   TRUE
               )
        RETURNING id
),
     new_conditions AS (
         INSERT INTO rule_conditions
             (rule_id, fact_name, operator, expected_value)
             SELECT id, 'wifi_connected', 'EQUALS', 'true'
             FROM new_rule

             UNION ALL

             SELECT id, 'internet_working', 'EQUALS', 'false'
             FROM new_rule

             RETURNING id
     ),
     new_conclusion AS (
         INSERT INTO rule_conclusions
             (rule_id, conclusion_name, conclusion_value)
             SELECT
                 id,
                 'diagnosis',
                 'Possible router or ISP connectivity issue'
             FROM new_rule
             RETURNING id
     )
SELECT
    (SELECT id FROM new_rule) AS rule_id,
    (SELECT COUNT(*) FROM new_conditions) AS conditions_added,
    (SELECT COUNT(*) FROM new_conclusion) AS conclusions_added;


BEGIN;

WITH new_rules AS (
    INSERT INTO rules (name, description, priority, enabled)
        VALUES
            (
                'WiFi Not Connected',
                'The device is not connected to WiFi.',
                2,
                TRUE
            ),
            (
                'Router Is Powered Off',
                'The router is not powered on.',
                3,
                TRUE
            ),
            (
                'Device Did Not Obtain IP',
                'The device did not obtain an IP address.',
                4,
                TRUE
            ),
            (
                'DNS Resolution Failure',
                'DNS is not working correctly.',
                5,
                TRUE
            )
        RETURNING id, name
),
     conditions AS (
         INSERT INTO rule_conditions
             (rule_id, fact_name, operator, expected_value)
             SELECT id, 'wifi_connected', 'EQUALS', 'false'
             FROM new_rules
             WHERE name = 'WiFi Not Connected'

             UNION ALL

             SELECT id, 'router_powered_on', 'EQUALS', 'false'
             FROM new_rules
             WHERE name = 'Router Is Powered Off'

             UNION ALL

             SELECT id, 'ip_obtained', 'EQUALS', 'false'
             FROM new_rules
             WHERE name = 'Device Did Not Obtain IP'

             UNION ALL

             SELECT id, 'dns_working', 'EQUALS', 'false'
             FROM new_rules
             WHERE name = 'DNS Resolution Failure'

             RETURNING id
     ),
     conclusions AS (
         INSERT INTO rule_conclusions
             (rule_id, conclusion_name, conclusion_value)
             SELECT id, 'diagnosis',
                    'Possible WiFi connection problem'
             FROM new_rules
             WHERE name = 'WiFi Not Connected'

             UNION ALL

             SELECT id, 'diagnosis',
                    'Check router power supply and power connection'
             FROM new_rules
             WHERE name = 'Router Is Powered Off'

             UNION ALL

             SELECT id, 'diagnosis',
                    'Possible DHCP or IP configuration problem'
             FROM new_rules
             WHERE name = 'Device Did Not Obtain IP'

             UNION ALL

             SELECT id, 'diagnosis',
                    'Possible DNS configuration or resolution problem'
             FROM new_rules
             WHERE name = 'DNS Resolution Failure'

             RETURNING id
     )
SELECT
    (SELECT COUNT(*) FROM new_rules) AS rules_added,
    (SELECT COUNT(*) FROM conditions) AS conditions_added,
    (SELECT COUNT(*) FROM conclusions) AS conclusions_added;

COMMIT;


SELECT id, name, priority, enabled
FROM rules
ORDER BY priority, id;