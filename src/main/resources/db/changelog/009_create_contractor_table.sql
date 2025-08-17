CREATE TABLE IF NOT EXISTS contractors (
    id VARCHAR(255) PRIMARY KEY NOT NULL,
    parent_id VARCHAR(255),
    name TEXT NOT NULL,
    name_full TEXT,
    inn VARCHAR(20),
    ogrn VARCHAR(20),
    country_id VARCHAR(10),
    industry_id INT,
    org_form_id INT,
    create_date TIMESTAMP,
    modify_date TIMESTAMP,
    create_user_id VARCHAR(255),
    modify_user_id VARCHAR(255),
    is_active BOOLEAN,
    country_name TEXT,
    industry_name TEXT,
    org_form_name TEXT
);