
------------------------------------ Add cities --------------------------------------------

INSERT INTO cities (name,country) VALUES
	('Tashkent','Uzbekistan'),
	('Moscow', 'Russia'),
	('Bern', 'Switzerland');

----------------------------------- Add hotels ----------------------------------------

INSERT INTO hotels (
	city_id,
	name,
	type,
	rating,
	address,
	brand
)

VALUES
	(1,'Hilton Tashkent City','HOTEL',5.0, 'Uqchi Street, 1','Hilton'),
	(2,'Radisson Collection Hotel', 'HOTEL', 4.9, '2/1 Kutuzovskiy Avenue Bld. 1', 'Radisson'),
	(3,'Villa de Este', 'VILLA', 5.0, 'Lombardy, Provincia di Como, Via Regina, 40', 'Este');

--------------------------------- Add amenities ----------------------------------------

INSERT INTO amenities (name) VALUES
	('air conditioner'),
	('Mini bar'),
	('TV'),
	('Balcony'),
	('Sea view'),
	('Jacuzzi');

----------------------------------- Add hotel amenities --------------------------------------

INSERT INTO hotel_amenities (hotel_id,amenity_id) VALUES
	(1,2),
	(2,4),
	(3,5);

-------------------------------------- Add rooms -------------------------------------------

INSERT INTO rooms (
	hotel_id,
	room_number,
	type,
	max_guests,
	meal_plan
)

VALUES
    -- Hilton, Tashkent City
	(1,'101','SINGLE',1,'NONE'),
	(1,'102','DOUBLE',2,'BREAKFAST'),
	(1,'103','SUITE',2,'HALF_BOARD'),

    -- Radisson, Moscow
	(2,'201','SINGLE',1,'NONE'),
	(2,'202','DOUBLE',2,'BREAKFAST'),
	(2,'203','SUITE',2,'HALF_BOARD'),

    -- Villa, Bern
	(3,'301','VILLA',6,'NONE');

--------------------------------- Add rooms_amenities -------------------------------------------

INSERT INTO room_amenities (room_id,amenity_id) VALUES
    -- Hilton, Tashkent City
	(1,1),
	(1,3),

	(2,1),
	(2,3),

	(3,1),
	(3,2),
	(3,3),
	(3,4),

	-- Radisson, Moscow
	(4,1),
	(4,3),

	(5,1),
	(5,3),

	(6,1),
	(6,2),
	(6,3),
	(6,4),
	(6,6),

    -- Villa, Bern
	(7,1),
	(7,2),
	(7,3),
	(7,4),
	(7,5),
	(7,6);

----------------------------------- Add rates -----------------------------------------

INSERT INTO rates (
	room_id,
	price_per_night,
	valid_from,
	valid_to
)

VALUES
	-- Hilton, Tasheknt City SIGNLE ROOM
	(1,50,'2026-01-01','2026-02-28'),
	(1,60,'2026-03-01', '2026-05-31'),
	(1,100,'2026-06-01','2026-08-31'),
	(1,120,'2026-09-01','2026-11-30'),
	(1,50,'2026-12-01','2026-12-31'),

	-- Hilton, Tasheknt City DOUBLE ROOM
	(2,100,'2026-01-01','2026-02-28'),
	(2,120,'2026-03-01', '2026-05-31'),
	(2,200,'2026-06-01','2026-08-31'),
	(2,240,'2026-09-01','2026-11-30'),
	(2,100,'2026-12-01','2026-12-31'),

	-- Hilton, Tasheknt City SUITE
	(3,150,'2026-01-01','2026-02-28'),
	(3,200,'2026-03-01', '2026-05-31'),
	(3,300,'2026-06-01','2026-08-31'),
	(3,340,'2026-09-01','2026-11-30'),
	(3,150,'2026-12-01','2026-12-31'),

	-- Radisson, Moscow SINGLE ROOM
	(4,80,'2026-01-01','2026-02-28'),
	(4,130,'2026-03-01', '2026-05-31'),
	(4,160,'2026-06-01','2026-08-31'),
	(4,140,'2026-09-01','2026-11-30'),
	(4,80,'2026-12-01','2026-12-31'),

	-- Radisson, Moscow DOUBLE ROOM
	(5,160,'2026-01-01','2026-02-28'),
	(5,260,'2026-03-01', '2026-05-31'),
	(5,320,'2026-06-01','2026-08-31'),
	(5,280,'2026-09-01','2026-11-30'),
	(5,160,'2026-12-01','2026-12-31'),

	-- Radisson, Moscow SUITE
	(6,200,'2026-01-01','2026-02-28'),
	(6,250,'2026-03-01', '2026-05-31'),
	(6,350,'2026-06-01','2026-08-31'),
	(6,280,'2026-09-01','2026-11-30'),
	(6,200,'2026-12-01','2026-12-31'),

	-- Villa, Bern VILLA
	(7,1000,'2026-01-01','2026-02-28'),
	(7,1500,'2026-03-01', '2026-05-31'),
	(7,2500,'2026-06-01','2026-08-31'),
	(7,1800,'2026-09-01','2026-11-30'),
	(7,1000,'2026-12-01','2026-12-31');

---------------------------------- Add availability --------------------------------------------

------------- Hilton, Tashkent City ------------

-- SEED AVAILABLE DATES -- SINGLE
INSERT INTO room_availability (room_id, date, is_available)
SELECT 1, generate_series('2026-04-01'::date, '2026-04-20'::date, '1 day')::date, true
ON CONFLICT (room_id, date) DO UPDATE SET is_available = false;

-- SEED UNAVAILABLE DATES -- SINGLE
INSERT INTO room_availability (room_id, date, is_available)
SELECT 1, generate_series('2026-04-21'::date, '2026-04-23'::date, '1 day')::date, false
ON CONFLICT (room_id, date) DO UPDATE SET is_available = false;

-- SEED AVAILABLE DATES -- SINGLE
INSERT INTO room_availability (room_id, date, is_available)
SELECT 1, generate_series('2026-04-24'::date, '2026-04-30'::date, '1 day')::date, true
ON CONFLICT (room_id, date) DO UPDATE SET is_available = false;



-- SEED AVAILABLE DATES -- DOUBLE
INSERT INTO room_availability (room_id, date, is_available)
SELECT 2, generate_series('2026-04-01'::date, '2026-04-20'::date, '1 day')::date, true
ON CONFLICT (room_id, date) DO UPDATE SET is_available = false;

-- SEED UNAVAILABLE DATES -- DOUBLE
INSERT INTO room_availability (room_id, date, is_available)
SELECT 2, generate_series('2026-04-21'::date, '2026-04-23'::date, '1 day')::date, false
ON CONFLICT (room_id, date) DO UPDATE SET is_available = false;

-- SEED AVAILABLE DATES -- DOUBLE
INSERT INTO room_availability (room_id, date, is_available)
SELECT 2, generate_series('2026-04-24'::date, '2026-04-30'::date, '1 day')::date, true
ON CONFLICT (room_id, date) DO UPDATE SET is_available = false;



-- SEED AVAILABLE DATES -- SUITE
INSERT INTO room_availability (room_id, date, is_available)
SELECT 3, generate_series('2026-04-01'::date, '2026-04-20'::date, '1 day')::date, true
ON CONFLICT (room_id, date) DO UPDATE SET is_available = false;

-- SEED UNAVAILABLE DATES -- SUITE
INSERT INTO room_availability (room_id, date, is_available)
SELECT 3, generate_series('2026-04-21'::date, '2026-04-23'::date, '1 day')::date, false
ON CONFLICT (room_id, date) DO UPDATE SET is_available = false;

-- SEED AVAILABLE DATES -- SUITE
INSERT INTO room_availability (room_id, date, is_available)
SELECT 3, generate_series('2026-04-24'::date, '2026-04-30'::date, '1 day')::date, true
ON CONFLICT (room_id, date) DO UPDATE SET is_available = false;


------------- RADISSON, MOSCOW ------------

-- SEED AVAILABLE DATES -- SINGLE
INSERT INTO room_availability (room_id, date, is_available)
SELECT 4, generate_series('2026-04-01'::date, '2026-04-20'::date, '1 day')::date, true
ON CONFLICT (room_id, date) DO UPDATE SET is_available = false;

-- SEED UNAVAILABLE DATES -- SINGLE
INSERT INTO room_availability (room_id, date, is_available)
SELECT 4, generate_series('2026-04-21'::date, '2026-04-23'::date, '1 day')::date, false
ON CONFLICT (room_id, date) DO UPDATE SET is_available = false;

-- SEED AVAILABLE DATES -- SINGLE
INSERT INTO room_availability (room_id, date, is_available)
SELECT 4, generate_series('2026-04-24'::date, '2026-04-30'::date, '1 day')::date, true
ON CONFLICT (room_id, date) DO UPDATE SET is_available = false;



-- SEED AVAILABLE DATES -- DOUBLE
INSERT INTO room_availability (room_id, date, is_available)
SELECT 5, generate_series('2026-04-01'::date, '2026-04-20'::date, '1 day')::date, true
ON CONFLICT (room_id, date) DO UPDATE SET is_available = false;

-- SEED UNAVAILABLE DATES -- DOUBLE
INSERT INTO room_availability (room_id, date, is_available)
SELECT 5, generate_series('2026-04-21'::date, '2026-04-23'::date, '1 day')::date, false
ON CONFLICT (room_id, date) DO UPDATE SET is_available = false;

-- SEED AVAILABLE DATES -- DOUBLE
INSERT INTO room_availability (room_id, date, is_available)
SELECT 5, generate_series('2026-04-24'::date, '2026-04-30'::date, '1 day')::date, true
ON CONFLICT (room_id, date) DO UPDATE SET is_available = false;



-- SEED AVAILABLE DATES -- SUITE
INSERT INTO room_availability (room_id, date, is_available)
SELECT 6, generate_series('2026-04-01'::date, '2026-04-20'::date, '1 day')::date, true
ON CONFLICT (room_id, date) DO UPDATE SET is_available = false;

-- SEED UNAVAILABLE DATES -- SUITE
INSERT INTO room_availability (room_id, date, is_available)
SELECT 6, generate_series('2026-04-21'::date, '2026-04-23'::date, '1 day')::date, false
ON CONFLICT (room_id, date) DO UPDATE SET is_available = false;

-- SEED AVAILABLE DATES -- SUITE
INSERT INTO room_availability (room_id, date, is_available)
SELECT 6, generate_series('2026-04-24'::date, '2026-04-30'::date, '1 day')::date, true
ON CONFLICT (room_id, date) DO UPDATE SET is_available = false;


------------- Villa, Bern ------------
-- SEED AVAILABLE DATES -- VILLA
INSERT INTO room_availability (room_id, date, is_available)
SELECT 7, generate_series('2026-04-01'::date, '2026-04-20'::date, '1 day')::date, true
ON CONFLICT (room_id, date) DO UPDATE SET is_available = false;

-- SEED UNAVAILABLE DATES -- VILLA
INSERT INTO room_availability (room_id, date, is_available)
SELECT 7, generate_series('2026-04-21'::date, '2026-04-23'::date, '1 day')::date, false
ON CONFLICT (room_id, date) DO UPDATE SET is_available = false;

-- SEED AVAILABLE DATES -- VILLA
INSERT INTO room_availability (room_id, date, is_available)
SELECT 7, generate_series('2026-04-24'::date, '2026-04-30'::date, '1 day')::date, true
ON CONFLICT (room_id, date) DO UPDATE SET is_available = false;



---------------------------------- Add cancellation_policies --------------------------------------------

INSERT INTO cancellation_policies (name, hours_before_check_in, refund_percentage)

VALUES
    -- Flexible - Полный возврат, если до 24 часов отмена - возврат 100%
    ('Flexible', 24, 100.00),

    -- Moderate - Если отмена за 72 часа ( 3 дня ) до въезда, то возврат 100%, в другом случае 50%
    ('Moderate - Full Refund', 72, 100.00),
    ('Moderate - Half Refund', 24, 50.00),

    -- Strict - Если отмена за 7 дней до въещда, то возврат 100%, в другом случае, возврата нет
    ('Strict - Full Refund', 168, 100.00),
    ('Strict - No Refund', 24, 0.00),

    -- Non-refundable: без возврата денег
    ('Non-Refundable', 0, 0.00);


---------------------------------- Add users --------------------------------------------
INSERT INTO users (first_name,last_name,card_token,email,phone) VALUES
	('Lebron','James', '79c3d4a3-6467-43dd-89f4-6c38f4f1ee5c', 'master07n.n@gmail.com', '998060060606'),
	('Michael','Jordan', '79c3d4a3-6467-43dd-89f4-6c38f4f1ee5c', 'yaelseba21@gmail.com', '998232232323'),
	('Giannis', 'Antetokounmpo' , '79c3d4a3-6467-43dd-89f4-6c38f4f1ee5c', 'mixprom100@gmail.com', '998343343434');


