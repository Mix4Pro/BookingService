CREATE TABLE cities (
    id BIGSERIAL PRIMARY KEY,
	name VARCHAR (100),
	country VARCHAR(100)
);

CREATE TABLE hotels (
	id BIGSERIAL PRIMARY KEY,
	city_id BIGINT NOT NULL REFERENCES cities(id),
	name VARCHAR (255),
	type VARCHAR (255), -- HOTEL/APARTMENT/HOSTEL/HOUSE/VILLA
	rating DECIMAL(2,1),
	address VARCHAR (255),
	brand VARCHAR(255)
);

CREATE TABLE amenities (
	id BIGSERIAL PRIMARY KEY,
	name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE hotel_amenities (
	hotel_id BIGINT NOT NULL REFERENCES hotels(id),
	amenity_id BIGINT NOT NULL REFERENCES amenities(id),
	PRIMARY KEY (hotel_id,amenity_id)
);

CREATE TABLE rooms (
	id BIGSERIAL PRIMARY KEY,
	hotel_id BIGINT NOT NULL REFERENCES hotels(id),
	room_number VARCHAR(50) NOT NULL,
	type VARCHAR (20) NOT NULL, -- SINGLE/DOUBLE/SUITE/VILLA
	max_guests INT NOT NULL,
	meal_plan VARCHAR(20) NOT NULL -- NONE/BREAKFAST/HALF_BOARD/ALL_INCLUSIVE
);

CREATE TABLE room_amenities (
	room_id BIGINT NOT NULL REFERENCES rooms(id),
	amenity_id BIGINT NOT NULL REFERENCES amenities(id),
	PRIMARY KEY (room_id, amenity_id)
);

CREATE TABLE rates (
	id BIGSERIAL PRIMARY KEY,
	room_id BIGINT NOT NULL REFERENCES rooms(id),
	price_per_night DECIMAL (10,2) NOT NULL,
	valid_from DATE NOT NULL,
	valid_to DATE NOT NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE room_availability (
	id BIGSERIAL PRIMARY KEY,
	room_id BIGINT NOT NULL REFERENCES rooms(id),
	date DATE NOT NULL,
	is_available BOOLEAN DEFAULT TRUE,
	UNIQUE (room_id,date)
);

CREATE TABLE cancellation_policies (
	id BIGSERIAL PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	hours_before_check_in INT NOT NULL,
	refund_percentage DECIMAL (5,2) NOT NULL
);

CREATE TABLE users (
	id BIGSERIAL PRIMARY KEY,
	first_name VARCHAR(255) NOT NULL,
	last_name VARCHAR(255) NOT NULL,
	card_token VARCHAR(255) NOT NULL,
	email VARCHAR(255) NOT NULL UNIQUE,
	phone VARCHAR(20) NOT NULL
);

CREATE TABLE bookings (
	id BIGSERIAL PRIMARY KEY,
	user_id BIGINT NOT NULL REFERENCES users(id),
	room_id BIGINT NOT NULL REFERENCES rooms(id),
	rate_id BIGINT NOT NULL REFERENCES rates(id),
	cancellation_policy_id BIGINT NOT NULL REFERENCES cancellation_policies(id),
	check_in_date DATE NOT NULL,
	check_out_date DATE NOT NULL,
	guests_count INT NOT NULL,
	total_amount DECIMAL(10,2) NOT NULL,
	prepayment_amount DECIMAL (10,2),
	status VARCHAR(20) NOT NULL DEFAULT 'HOLD',
	payment_plan VARCHAR(20) NOT NULL,
	hold_expires_at TIMESTAMP,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE booking_history (
	id BIGSERIAL PRIMARY KEY,
	booking_id BIGINT NOT NULL REFERENCES bookings(id),
	status_from VARCHAR(50),
	status_to VARCHAR(50) NOT NULL,
	changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	changed_by VARCHAR(10), -- USER или SYSTEM
	comment TEXT
);

CREATE TABLE payment_history (
	id BIGSERIAL PRIMARY KEY,
	booking_id BIGINT NOT NULL REFERENCES bookings(id),
	type VARCHAR(10) NOT NULL, -- CHARGE или REFUND
	amount DECIMAL (10,2) NOT NULL,
	status VARCHAR(10) NOT NULL, -- SUCCESS или FAILED
	bank_transaction_id UUID,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);