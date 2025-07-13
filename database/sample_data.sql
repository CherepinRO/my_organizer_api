-- Super Organizer API Sample Data

-- Insert sample tasks
INSERT INTO tasks (date, task_name, comment, deadline, priority, task_type) VALUES
(
    CURRENT_DATE,
    'Complete project documentation',
    'Write comprehensive documentation for the Super Organizer API',
    CURRENT_TIMESTAMP + INTERVAL '3 days',
    'HIGH',
    'WORK'
),
(
    CURRENT_DATE - INTERVAL '1 day',
    'Grocery shopping',
    'Buy vegetables, fruits, and dairy products',
    CURRENT_TIMESTAMP + INTERVAL '1 day',
    'MEDIUM',
    'HOME'
),
(
    CURRENT_DATE - INTERVAL '2 days',
    'Code review',
    'Review pull requests from team members',
    NULL,
    'MEDIUM',
    'WORK'
),
(
    CURRENT_DATE,
    'Clean the house',
    'Deep cleaning of all rooms',
    CURRENT_TIMESTAMP + INTERVAL '2 days',
    'LOW',
    'HOME'
),
(
    CURRENT_DATE + INTERVAL '1 day',
    'Prepare presentation',
    'Create slides for the quarterly meeting',
    CURRENT_TIMESTAMP + INTERVAL '5 days',
    'HIGH',
    'WORK'
),
(
    CURRENT_DATE - INTERVAL '3 days',
    'Exercise routine',
    '30-minute workout session',
    NULL,
    'MEDIUM',
    'HOME'
),
(
    CURRENT_DATE,
    'Team meeting',
    'Weekly team sync and planning',
    CURRENT_TIMESTAMP + INTERVAL '1 day',
    'HIGH',
    'WORK'
),
(
    CURRENT_DATE - INTERVAL '1 day',
    'Pay bills',
    'Electricity, water, and internet bills',
    CURRENT_TIMESTAMP + INTERVAL '3 days',
    'MEDIUM',
    'HOME'
),
(
    CURRENT_DATE + INTERVAL '2 days',
    'Database optimization',
    'Optimize queries and add indexes',
    CURRENT_TIMESTAMP + INTERVAL '7 days',
    'LOW',
    'WORK'
),
(
    CURRENT_DATE,
    'Garden maintenance',
    'Water plants and trim hedges',
    NULL,
    'LOW',
    'HOME'
);

-- Verify the data was inserted
SELECT 
    id,
    date,
    task_name,
    comment,
    deadline,
    priority,
    task_type,
    created_at,
    updated_at
FROM tasks
ORDER BY priority DESC, date ASC;