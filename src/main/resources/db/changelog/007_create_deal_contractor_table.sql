CREATE TABLE IF NOT EXISTS deal_contractor (
    id UUID PRIMARY KEY NOT NULL,
    deal_id UUID NOT NULL,
    contractor_id VARCHAR(12) NOT NULL,
    name TEXT NOT NULL,
    inn TEXT,
    main BOOLEAN NOT NULL DEFAULT FALSE,
    create_date TIMESTAMP NOT NULL DEFAULT NOW(),
    modify_date TIMESTAMP,
    create_user_id TEXT,
    modify_user_id TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    FOREIGN KEY (deal_id) REFERENCES deal(id)
);