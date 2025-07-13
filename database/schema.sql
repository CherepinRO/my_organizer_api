-- Super Organizer API PostgreSQL Database Schema

-- Create the database (run separately or in a different connection)
-- CREATE DATABASE super_organizer_db;

-- Use the database
-- \c super_organizer_db;

-- Create ENUMs for Priority and TaskType
CREATE TYPE priority_enum AS ENUM ('HIGH', 'MEDIUM', 'LOW');
CREATE TYPE task_type_enum AS ENUM ('WORK', 'HOME');

-- Create the tasks table
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    task_name VARCHAR(255) NOT NULL,
    comment TEXT,
    deadline TIMESTAMP,
    priority priority_enum NOT NULL,
    task_type task_type_enum NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_tasks_date ON tasks(date);
CREATE INDEX idx_tasks_priority ON tasks(priority);
CREATE INDEX idx_tasks_task_type ON tasks(task_type);
CREATE INDEX idx_tasks_deadline ON tasks(deadline);
CREATE INDEX idx_tasks_task_name ON tasks(task_name);
CREATE INDEX idx_tasks_created_at ON tasks(created_at);

-- Create a composite index for common search patterns
CREATE INDEX idx_tasks_priority_type ON tasks(priority, task_type);
CREATE INDEX idx_tasks_date_priority ON tasks(date, priority);

-- Create a trigger to automatically update the updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_tasks_updated_at
    BEFORE UPDATE ON tasks
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Create constraints
ALTER TABLE tasks ADD CONSTRAINT chk_task_name_not_empty CHECK (LENGTH(TRIM(task_name)) > 0);
ALTER TABLE tasks ADD CONSTRAINT chk_deadline_future CHECK (deadline IS NULL OR deadline > created_at);