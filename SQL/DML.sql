INSERT INTO Trainers (first_name, last_name, username, password, start_time, end_time)
VALUES 
('Tom', 'Brady', 'tombrady', 'password', '07:00:00', '19:00:00'),
('Jackie', 'Robinson', 'jackierobinson', 'password', '09:00:00', '17:00:00');

INSERT INTO Admins (first_name, last_name, username, password)
VALUES 
('John', 'Smith', 'johnsmith', 'password');

INSERT INTO Classes (class_date, description)
VALUES
('2024-12-01', '30 Minute Cardio Session'),
('2024-11-01', '60 Minute Mountain Hike'),
('2024-10-01', '30 Minute Weight Lifting');

INSERT INTO Maintenances (description, start_date)
VALUES
('Fixing stair machine', '2024-01-01');

INSERT INTO Teaches (class_id, trainer_id)
SELECT c.class_id, t.trainer_id FROM classes c, trainers t
WHERE t.first_name = 'Tom' AND c.description = '30 Minute Cardio Session';

INSERT INTO Teaches (class_id, trainer_id)
SELECT c.class_id, t.trainer_id FROM classes c, trainers t
WHERE t.first_name = 'Tom' AND c.description = '60 Minute Mountain Hike';

INSERT INTO Teaches (class_id, trainer_id)
SELECT c.class_id, t.trainer_id FROM classes c, trainers t
WHERE t.first_name = 'Jackie' AND c.description = '30 Minute Weight Lifting';