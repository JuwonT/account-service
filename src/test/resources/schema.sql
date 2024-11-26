create TABLE events (
    account_id VARCHAR(36) NOT NULL,
    account_version INT NOT NULL,
    event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    event_data JSON,
    event_type VARCHAR(50),

    CONSTRAINT unique_account_version UNIQUE (account_id, account_version)
);