CREATE TABLE users(
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE diagnosis_sessions(
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id),
    final_diagnosis VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE session_facts(
    id SERIAL PRIMARY KEY,
    session_id INT REFERENCES diagnosis_sessions(id),
    fact_name VARCHAR(100)NOT NULL,
    fact_value VARCHAR(100)NOT NULL
);

CREATE TABLE rules(
    id SERIAL PRIMARY KEY ,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    priority INT DEFAULT 3,
    enabled BOOLEAN DEFAULT TRUE
);

CREATE TABLE rule_conditions(
    id SERIAL PRIMARY KEY,
    rule_id INT REFERENCES rules(id) ON DELETE CASCADE,
    fact_name VARCHAR(100) NOT NULL,
    operator VARCHAR(20) NOT NULL ,
    expected_value VARCHAR(100) NOT NULL
);

CREATE TABLE rule_conclusions(
    id SERIAL PRIMARY KEY,
    rule_id INT REFERENCES rules(id) ON DELETE CASCADE,
    conclusion_name VARCHAR(100) NOT NULL ,
    conclusion_value VARCHAR(200) NOT NULL
);

CREATE TABLE matched_rules (
    id SERIAL PRIMARY KEY,
    session_id INT REFERENCES diagnosis_sessions(id),
    rule_id INT REFERENCES rules(id),
    matched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);