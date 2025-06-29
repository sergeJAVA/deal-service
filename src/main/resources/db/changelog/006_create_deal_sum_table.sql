CREATE TABLE IF NOT EXISTS deal_sum (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    deal_id UUID NOT NULL,
    sum NUMERIC(100,2) NOT NULL,
    currency_id VARCHAR(3) NOT NULL,
    is_main BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    FOREIGN KEY (deal_id) REFERENCES deal(id),
    FOREIGN KEY (currency_id) REFERENCES currency(id)
);