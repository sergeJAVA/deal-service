CREATE TABLE IF NOT EXISTS contractor_to_role (
    contractor_id UUID NOT NULL,
    role_id VARCHAR(30) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    PRIMARY KEY (contractor_id, role_id),

    CONSTRAINT fk_ctr_contractor
            FOREIGN KEY (contractor_id)
            REFERENCES deal_contractor (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_ctr_role
            FOREIGN KEY (role_id)
            REFERENCES contractor_role (id)
            ON DELETE RESTRICT
);