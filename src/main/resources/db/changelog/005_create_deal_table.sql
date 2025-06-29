CREATE TABLE IF NOT EXISTS deal (
    id UUID PRIMARY KEY NOT NULL,
    description TEXT,
    agreement_number TEXT,
    agreement_date DATE,
    agreement_start_dt TIMESTAMP,
    availability_date DATE,
    type_id VARCHAR(30),
    status_id VARCHAR(30) NOT NULL,
    close_dt TIMESTAMP,
    create_date TIMESTAMP NOT NULL DEFAULT NOW(),
    modify_date TIMESTAMP,
    create_user_id TEXT,
    modify_user_id TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    FOREIGN KEY (type_id) REFERENCES deal_type(id),
    FOREIGN KEY (status_id) REFERENCES deal_status(id)
);