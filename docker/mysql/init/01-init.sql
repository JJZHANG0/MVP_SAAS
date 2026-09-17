-- ToolFix Database Initialization
-- This file ensures the database and user are properly set up
-- Additional tables will be created automatically by Spring Boot JPA (ddl-auto: update)

-- Database is already created by docker environment variables
-- This file can be used for any custom initialization if needed in the future

-- Set default character set and collation
ALTER DATABASE toolfix CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Grant all privileges (already done via environment variables, but ensuring it)
GRANT ALL PRIVILEGES ON toolfix.* TO 'toolfix'@'%';
FLUSH PRIVILEGES;

-- Create a health check table (optional, for monitoring)
CREATE TABLE IF NOT EXISTS health_check (
    id INT PRIMARY KEY AUTO_INCREMENT,
    last_check TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'OK'
);

INSERT INTO health_check (status) VALUES ('OK') ON DUPLICATE KEY UPDATE last_check = CURRENT_TIMESTAMP;
