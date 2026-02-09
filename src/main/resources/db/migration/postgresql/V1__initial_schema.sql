-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    plan_type VARCHAR(50) NOT NULL DEFAULT 'FREE',
    credits INT NOT NULL DEFAULT 0,
    stripe_customer_id VARCHAR(255),
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Scan results table
CREATE TABLE scan_results (
    id BIGSERIAL PRIMARY KEY,
    url VARCHAR(2048) NOT NULL,
    score INT NOT NULL,
    issues_json TEXT NOT NULL,
    details_json TEXT,
    jurisdiction VARCHAR(50),
    user_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Generated documents table
CREATE TABLE generated_documents (
    id BIGSERIAL PRIMARY KEY,
    document_type VARCHAR(100) NOT NULL,
    title VARCHAR(500) NOT NULL,
    content TEXT NOT NULL,
    business_name VARCHAR(255),
    business_type VARCHAR(255),
    jurisdiction VARCHAR(50),
    language VARCHAR(10) DEFAULT 'en',
    user_id BIGINT NOT NULL,
    scan_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (scan_id) REFERENCES scan_results(id)
);

-- Payments table
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount_cents INT NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'EUR',
    payment_type VARCHAR(50) NOT NULL,
    stripe_session_id VARCHAR(255),
    stripe_payment_intent VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Indexes
CREATE INDEX idx_scan_results_url ON scan_results(url);
CREATE INDEX idx_scan_results_user ON scan_results(user_id);
CREATE INDEX idx_generated_documents_user ON generated_documents(user_id);
CREATE INDEX idx_payments_user ON payments(user_id);
CREATE INDEX idx_payments_stripe_session ON payments(stripe_session_id);
CREATE INDEX idx_users_email ON users(email);
