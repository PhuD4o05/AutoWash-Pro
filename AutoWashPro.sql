USE MASTER;
GO

IF DB_ID('AutoWashPro') IS NOT NULL
BEGIN
    ALTER DATABASE AutoWashPro
    SET SINGLE_USER WITH ROLLBACK IMMEDIATE;

    DROP DATABASE AutoWashPro;
END
GO

CREATE DATABASE AutoWashPro;
GO

USE AutoWashPro;
GO



-- ====== CUSTOMERS ======
CREATE TABLE customers (
    id BIGINT IDENTITY PRIMARY KEY,

    full_name NVARCHAR(100),
    phone_number NVARCHAR(30) UNIQUE,
    email NVARCHAR(100),
    password NVARCHAR(255),

    avatar_url NVARCHAR(255),

    membership_tier NVARCHAR(50),
    total_points INT,
    current_points INT,

    is_active BIT,
    role NVARCHAR(30),

    created_at DATETIME2,
    updated_at DATETIME2
);
GO



-- ====== RECEPTIONISTS ======
CREATE TABLE receptionists (
    id BIGINT IDENTITY PRIMARY KEY,

    full_name NVARCHAR(100),
    phone_number NVARCHAR(30) UNIQUE,
    email NVARCHAR(100),
    password NVARCHAR(255),

    is_active BIT,
    role NVARCHAR(30),

    created_at DATETIME2,
    updated_at DATETIME2
);
GO



-- ====== WASHERS ======
CREATE TABLE washers (
    id BIGINT IDENTITY PRIMARY KEY,

    full_name NVARCHAR(100) NOT NULL,

    phone_number NVARCHAR(30) NOT NULL UNIQUE,

    email NVARCHAR(100),
    password NVARCHAR(255),

    is_active BIT,
    role NVARCHAR(30),

    created_at DATETIME2,
    updated_at DATETIME2
);
GO



-- ====== VEHICLES ======
CREATE TABLE vehicles (
    id BIGINT IDENTITY PRIMARY KEY,

    license_plate NVARCHAR(30) UNIQUE,
    brand NVARCHAR(50),
    model NVARCHAR(50),
    color NVARCHAR(30),

    image_url NVARCHAR(255),

    customer_id BIGINT,

    created_at DATETIME2,
    updated_at DATETIME2,

    CONSTRAINT FK_Vehicle_Customer
        FOREIGN KEY(customer_id)
        REFERENCES customers(id)
);
GO



-- ====== SERVICE PACKAGES ======
CREATE TABLE service_packages (
    id BIGINT IDENTITY PRIMARY KEY,

    name NVARCHAR(100) NOT NULL UNIQUE,

    description NVARCHAR(MAX),

    estimated_minutes INT,
    base_price DECIMAL(12,2),

    is_active BIT,

    created_at DATETIME2,
    updated_at DATETIME2
);
GO



-- ====== WASH BAYS ======
CREATE TABLE wash_bays (
    id BIGINT IDENTITY PRIMARY KEY,

    bay_number NVARCHAR(50) NOT NULL UNIQUE,

    status NVARCHAR(30)
);
GO



-- ====== BOOKINGS ======
CREATE TABLE bookings (
    id BIGINT IDENTITY PRIMARY KEY,

    customer_id BIGINT,
    vehicle_id BIGINT,
    service_package_id BIGINT,

    scheduled_time DATETIME2,
    checkin_time DATETIME2,
    completed_time DATETIME2,

    status NVARCHAR(30),

    qr_code NVARCHAR(255),

    total_price DECIMAL(12,2),

    voucher_code NVARCHAR(100),

    assigned_washer_id BIGINT,
    assigned_bay_id BIGINT,

    created_at DATETIME2,
    updated_at DATETIME2,

    CONSTRAINT FK_Booking_Customer
        FOREIGN KEY(customer_id)
        REFERENCES customers(id),

    CONSTRAINT FK_Booking_Vehicle
        FOREIGN KEY(vehicle_id)
        REFERENCES vehicles(id),

    CONSTRAINT FK_Booking_ServicePackage
        FOREIGN KEY(service_package_id)
        REFERENCES service_packages(id),

    CONSTRAINT FK_Booking_Washer
        FOREIGN KEY(assigned_washer_id)
        REFERENCES washers(id),

    CONSTRAINT FK_Booking_WashBay
        FOREIGN KEY(assigned_bay_id)
        REFERENCES wash_bays(id)
);
GO



-- ====== PAYMENTS ======
CREATE TABLE payments (
    id BIGINT IDENTITY PRIMARY KEY,

    booking_id BIGINT UNIQUE,

    amount DECIMAL(12,2),

    method NVARCHAR(30),
    status NVARCHAR(30),

    transaction_id NVARCHAR(255),

    paid_at DATETIME2,

    created_at DATETIME2,
    updated_at DATETIME2,

    CONSTRAINT FK_Payment_Booking
        FOREIGN KEY(booking_id)
        REFERENCES bookings(id)
);
GO



-- ====== LOYALTY TRANSACTIONS ======
CREATE TABLE loyalty_transactions (
    id BIGINT IDENTITY PRIMARY KEY,

    customer_id BIGINT NOT NULL,

    booking_id BIGINT,

    type NVARCHAR(30),

    points INT,

    description NVARCHAR(500),

    created_at DATETIME2,

    CONSTRAINT FK_Loyalty_Customer
        FOREIGN KEY(customer_id)
        REFERENCES customers(id),

    CONSTRAINT FK_Loyalty_Booking
        FOREIGN KEY(booking_id)
        REFERENCES bookings(id)
);
GO



-- ====== PROMOTIONS ======
CREATE TABLE promotions (
    id BIGINT IDENTITY PRIMARY KEY,

    code NVARCHAR(100),

    name NVARCHAR(255),
    description NVARCHAR(MAX),

    applicable_tier NVARCHAR(50),

    discount_percent INT,
    discount_amount BIGINT,

    start_date DATETIME2,
    end_date DATETIME2,

    is_active BIT,

    created_at DATETIME2,
    updated_at DATETIME2
);
GO



-- ====== VOUCHERS ======
CREATE TABLE vouchers (
    id BIGINT IDENTITY PRIMARY KEY,

    code NVARCHAR(100),

    description NVARCHAR(MAX),

    discount_percent INT,
    discount_amount BIGINT,

    required_points INT,

    valid_from DATETIME2,
    valid_until DATETIME2,

    is_active BIT,

    promotion_id BIGINT,
    customer_id BIGINT,

    created_at DATETIME2,
    updated_at DATETIME2,

    CONSTRAINT FK_Voucher_Promotion
        FOREIGN KEY(promotion_id)
        REFERENCES promotions(id),

    CONSTRAINT FK_Voucher_Customer
        FOREIGN KEY(customer_id)
        REFERENCES customers(id)
);
GO



-- ===== NOTIFICATIONS ======
CREATE TABLE notifications (
    id BIGINT IDENTITY PRIMARY KEY,

    title NVARCHAR(255),
    message NVARCHAR(MAX),

    is_read BIT,

    customer_id BIGINT,

    created_at DATETIME2,

    CONSTRAINT FK_Notification_Customer
        FOREIGN KEY(customer_id)
        REFERENCES customers(id)
);
GO



-- ====== WORK SHIFTS ======
CREATE TABLE work_shifts (
    id BIGINT IDENTITY PRIMARY KEY,

    washer_id BIGINT NOT NULL,

    shift_date DATE,
    shift_type NVARCHAR(20),

    start_time TIME,
    end_time TIME,

    created_at DATETIME2,
    updated_at DATETIME2,

    CONSTRAINT FK_WorkShift_Washer
        FOREIGN KEY (washer_id)
        REFERENCES washers(id)
);
GO



-- ====== ASSIGNMENTS ======
CREATE TABLE assignments (
    id BIGINT IDENTITY PRIMARY KEY,

    booking_id BIGINT,
    washer_id BIGINT,
    work_shift_id BIGINT,
    wash_bay_id BIGINT,

    start_time DATETIME2,
    end_time DATETIME2,

    status NVARCHAR(50),

    created_at DATETIME2,
    updated_at DATETIME2,

    CONSTRAINT FK_Assignment_Booking
        FOREIGN KEY (booking_id)
        REFERENCES bookings(id),

    CONSTRAINT FK_Assignment_Washer
        FOREIGN KEY (washer_id)
        REFERENCES washers(id),

    CONSTRAINT FK_Assignment_WorkShift
        FOREIGN KEY (work_shift_id)
        REFERENCES work_shifts(id),

    CONSTRAINT FK_Assignment_WashBay
        FOREIGN KEY (wash_bay_id)
        REFERENCES wash_bays(id)
);
GO



-- ====== WASH QUEUE ======
CREATE TABLE wash_queue (
    id BIGINT IDENTITY PRIMARY KEY,

    booking_id BIGINT NOT NULL UNIQUE,

    queue_position INT,

    enqueued_at DATETIME2,

    status NVARCHAR(30),

    started_at DATETIME2,
    finished_at DATETIME2,

    CONSTRAINT FK_WashQueue_Booking
        FOREIGN KEY(booking_id)
        REFERENCES bookings(id)
);
GO



-- ====== DYNAMIC PRICE RULES ======
CREATE TABLE dynamic_price_rules (
    id BIGINT IDENTITY PRIMARY KEY,

    rule_name NVARCHAR(255),

    is_weekend BIT,
    is_holiday BIT,

    specific_date DATE,

    applicable_tier NVARCHAR(50),

    percent_adjustment INT,

    is_active BIT,

    created_at DATETIME2,
    updated_at DATETIME2
);
GO



-- ====== REFRESH TOKENS ======
CREATE TABLE refresh_tokens (
    id BIGINT IDENTITY PRIMARY KEY,

    token NVARCHAR(500) NOT NULL UNIQUE,

    username NVARCHAR(100) NOT NULL,

    expiry_date DATETIME2 NOT NULL,

    revoked BIT
);
GO



-- ====== ANALYTICS REPORTS ======
CREATE TABLE analytics_reports (
    id BIGINT IDENTITY PRIMARY KEY,

    report_date DATE,

    report_type NVARCHAR(50),

    total_bookings BIGINT,
    total_revenue BIGINT,
    total_customers BIGINT,

    top_service NVARCHAR(255),

    data_json NVARCHAR(MAX),

    created_at DATETIME2
);
GO




-- ~~~~~~~~~~~~~~~~~~~~~~~~ INSERT DATA ~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-- ====== CUSTOMER DATA ======
INSERT INTO customers
(
    full_name,
    phone_number,
    email,
    password,
    avatar_url,
    membership_tier,
    total_points,
    current_points,
    is_active,
    role,
    created_at,
    updated_at
)
VALUES
(
    N'Nguyen Van A',
    '0901234567',
    'customer1@gmail.com',
    '$2a$10$dummyHash',
    NULL,
    'MEMBER',
    0,
    0,
    1,
    'CUSTOMER',
    GETDATE(),
    GETDATE()
),
(
    N'Tran Thi B',
    '0907654321',
    'customer2@gmail.com',
    '$2a$10$dummyHash',
    NULL,
    'SILVER',
    1500,
    1500,
    1,
    'CUSTOMER',
    GETDATE(),
    GETDATE()
);
GO



-- ====== WASHER DATA ======
INSERT INTO washers
(
    full_name,
    phone_number,
    email,
    password,
    is_active,
    role,
    created_at,
    updated_at
)
VALUES
(
    N'Pham Washer 1',
    '0911111111',
    'washer1@gmail.com',
    '$2a$10$dummyHash',
    1,
    'WASHER',
    GETDATE(),
    GETDATE()
),
(
    N'Pham Washer 2',
    '0922222222',
    'washer2@gmail.com',
    '$2a$10$dummyHash',
    1,
    'WASHER',
    GETDATE(),
    GETDATE()
);
GO



-- ====== RECEPTIONIST DATA ======
INSERT INTO receptionists
(
    full_name,
    phone_number,
    email,
    password,
    is_active,
    role,
    created_at,
    updated_at
)
VALUES
(
    N'Le Reception',
    '0933333333',
    'reception@gmail.com',
    '$2a$10$dummyHash',
    1,
    'RECEPTIONIST',
    GETDATE(),
    GETDATE()
);
GO



-- ====== SERVICE PACKAGE DATA ======
INSERT INTO service_packages
(
    name,
    description,
    estimated_minutes,
    base_price,
    is_active,
    created_at,
    updated_at
)
VALUES
('Express Wash', 'Quick exterior wash', 15, 50000, 1, GETDATE(), GETDATE()),
('Basic Wash', 'Exterior cleaning', 30, 100000, 1, GETDATE(), GETDATE()),
('Premium Wash', 'Exterior + interior cleaning', 60, 250000, 1, GETDATE(), GETDATE()),
('Luxury Detail', 'Full detailing package', 180, 800000, 1, GETDATE(), GETDATE());
GO



-- ====== WASH BAY DATA ======
INSERT INTO wash_bays
(
    bay_number,
    status
)
VALUES
('BAY-01', 'AVAILABLE'),
('BAY-02', 'AVAILABLE'),
('BAY-03', 'AVAILABLE');
GO



-- ====== VEHICLE DATA ======
INSERT INTO vehicles
(
    license_plate,
    brand,
    model,
    color,
    image_url,
    customer_id,
    created_at,
    updated_at
)
VALUES
(
    '51A-12345',
    'Toyota',
    'Vios',
    'White',
    NULL,
    1,
    GETDATE(),
    GETDATE()
),
(
    '59B-88888',
    'Honda',
    'City',
    'Black',
    NULL,
    2,
    GETDATE(),
    GETDATE()
);
GO



-- ====== WORK SHIFT DATA ======
INSERT INTO work_shifts
(
    washer_id,
    shift_date,
    shift_type,
    start_time,
    end_time,
    created_at,
    updated_at
)
VALUES
(
    1,
    '2026-06-21',
    'MORNING',
    '08:00',
    '12:00',
    GETDATE(),
    GETDATE()
),
(
    2,
    '2026-06-21',
    'AFTERNOON',
    '13:00',
    '17:00',
    GETDATE(),
    GETDATE()
);
GO



-- ====== BOOKING DATA ======
INSERT INTO bookings
(
    customer_id,
    vehicle_id,
    service_package_id,
    scheduled_time,
    status,
    total_price,
    assigned_washer_id,
    assigned_bay_id,
    created_at,
    updated_at
)
VALUES
(
    1,
    1,
    1,
    DATEADD(HOUR, 2, GETDATE()),
    'CONFIRMED',
    100000,
    1,
    1,
    GETDATE(),
    GETDATE()
);
GO



-- ====== PAYMENT DATA ======
INSERT INTO payments
(
    booking_id,
    amount,
    method,
    status,
    transaction_id,
    paid_at,
    created_at,
    updated_at
)
VALUES
(
    1,
    100000,
    'CASH',
    'PAID',
    'TXN001',
    GETDATE(),
    GETDATE(),
    GETDATE()
);
GO



-- ====== ASSIGNMENT DATA ======
INSERT INTO assignments
(
    booking_id,
    washer_id,
    work_shift_id,
    wash_bay_id,
    start_time,
    status,
    created_at,
    updated_at
)
VALUES
(
    1,
    1,
    1,
    1,
    GETDATE(),
    'ACTIVE',
    GETDATE(),
    GETDATE()
);
GO



-- ====== WASH QUEUE DATA ======
INSERT INTO wash_queue
(
    booking_id,
    queue_position,
    enqueued_at,
    status
)
VALUES
(
    1,
    1,
    GETDATE(),
    'WAITING'
);
GO