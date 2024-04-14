CREATE TABLE Members (
    member_id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    join_date DATE,
	username VARCHAR(255) NOT NULL UNIQUE,
	password VARCHAR(255) NOT NULL
);

CREATE TABLE Trainers (
    trainer_id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
	username VARCHAR(255) NOT NULL UNIQUE,
	password VARCHAR(255) NOT NULL,
	start_time TIME,
	end_time TIME
);

CREATE TABLE Admins (
    admin_id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
	username VARCHAR(255) NOT NULL UNIQUE,
	password VARCHAR(255) NOT NULL
);

CREATE TABLE Goals (
    member_id INTEGER REFERENCES Members(member_id),
    description VARCHAR(255)
);

CREATE TABLE Bookings (
    booking_id SERIAL PRIMARY KEY,
    booking_date DATE,
	description VARCHAR(255) NOT NULL
);

CREATE TABLE BookingsDetails (
    booking_id INTEGER PRIMARY KEY REFERENCES Bookings(booking_id),
    member_id INTEGER REFERENCES Members(member_id),
	trainer_id INTEGER REFERENCES Trainers(trainer_id)
);

CREATE TABLE Classes (
    class_id SERIAL PRIMARY KEY,
    class_date DATE,
	description VARCHAR(255) NOT NULL
);

CREATE TABLE Teaches (
    class_id INTEGER PRIMARY KEY REFERENCES Classes(class_id),
	trainer_id INTEGER REFERENCES Trainers(trainer_id)
);

CREATE TABLE Attends (
    class_id INTEGER PRIMARY KEY REFERENCES Classes(class_id),
	member_id INTEGER REFERENCES Members(member_id)
);

CREATE TABLE Billings (
    billing_id SERIAL PRIMARY KEY,
    amount_payed INTEGER,
	member_id INTEGER REFERENCES Members(member_id)
);

CREATE TABLE Maintenances (
    maintenance_id SERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
	start_date DATE
);