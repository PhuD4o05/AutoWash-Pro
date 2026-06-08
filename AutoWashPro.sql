if DB_ID ('AutoWashPro') is null
begin
	create database AutoWashPro
end
go

--use master
--drop database AutoWashPro

use AutoWashPro
go

-- ~~~~~					USER DATABASES					~~~~~
create table UserRoles (
	RoleID tinyint primary key,
	RoleName nvarchar(15) not null unique
);
go
insert into UserRoles values
	(1, 'Admin'),
	(2, 'Reception'),
	(3, 'Washer'),
	(4, 'Customer');
go

create table Users (
	UserID int identity primary key,
	[PasswordHash] nvarchar(255) not null,
	FullName nvarchar(50) not null,
	Phone varchar(15),
	Email nvarchar(30),
	RoleID tinyint not null foreign key references UserRoles(RoleID),

	constraint CK_User_Contacts
	check (
		nullif(ltrim(rtrim(Phone)), '') is not null or 
		nullif(ltrim(rtrim(Email)), '') is not null
	)
)
go

create table Staff (
	UserID int primary key foreign key references Users(UserID),
	Salary decimal(12,2) not null,
	ShiftsAbsent int default 0,
	HireDate date not null,

	constraint CK_AbsentPos
	check (ShiftsAbsent >= 0),

	constraint CK_ValidSalary
	check (Salary >= 0)
)
go
-- Calculate how long a staff memember has worked
create function dbo.GetYearsWorked (@HireDate date) 
returns int as
begin
	return (
		datediff(year, @HireDate, getdate()) -
		case
			WHEN dateadd(
				year,
				datediff(year, @HireDate, getdate()),
				@HireDate
			) > getdate()
			then 1
			else 0
		end
	);
end;
go
 
create table MembershipRanks (
	RankID tinyint primary key,
	RankName nvarchar(15) not null unique,
	MinimumPoints int not null,

	constraint CK_Rank_MinimumPoints
	check (MinimumPoints >= 0)
)
insert into MembershipRanks values
	(1, 'Member', 0),
	(2, 'Silver', 500),
	(3, 'Gold', 1500),
	(4, 'Platinum', 3000);
go
 
create table Customers (
	UserID int primary key foreign key references Users(UserID),
	Points int not null default 0,
	RankID tinyint not null default 1 foreign key references MembershipRanks(RankID),

	constraint CK_Customer_ValidPoints
	check (Points >= 0)
)
go

create table Cars (
	LicensePlate nvarchar(25) primary key,
	UserID int not null foreign key references Customers(UserID),
	Brand nvarchar(50),
	Model nvarchar(50),
	Color nvarchar(15)
)
go

create table DaysOfWeek (
    WorkDays tinyint primary key,
    [DayName] nvarchar(10) not null unique
)
go
insert into DaysOfWeek (WorkDays, DayName)
values
    (1, 'Monday'),
    (2, 'Tuesday'),
    (3, 'Wednesday'),
    (4, 'Thursday'),
    (5, 'Friday'),
    (6, 'Saturday'),
    (7, 'Sunday');

create table Shifts (
	ShiftID int identity primary key,
	UserID int not null,
	WorkDays tinyint not null,
	StartTime time not null,
	EndTime time not null,
	IsAbsent bit not null default 0,

	constraint CK_ValidShift
	check (WorkDays between 1 and 7 and StartTime < EndTime),
	
	foreign key (UserID) references Staff(UserID),
	foreign key (WorkDays) references DaysOfWeek(WorkDays)
)
go

create procedure MarkAbsent @ShiftID int
as
begin
	if exists (
		select 1 from Shifts
		where ShiftID = @ShiftID and IsAbsent = 0
	)
	begin
		update Shifts
		set IsAbsent = 1
		where ShiftID = @ShiftID
	
		update Staff
		set ShiftsAbsent = ShiftsAbsent + 1
		where UserID = (
			select UserID from Shifts
				where ShiftID = @ShiftID
		);
	end
end;
go


-- ~~~~~					BOOKING DATABASE					~~~~~
create table BookingStatus (
	StatusID tinyint primary key,
	StatusName nvarchar(25) not null unique
);
go
insert into BookingStatus values
	(1, 'Pending'),
	(2, 'Confirmed'),
	(3, 'Checked In'),
	(4, 'Waiting'),
	(5, 'Washing'),
	(6, 'Completed'),
	(7, 'Cancelled');
go

create table [Services] (
	ServiceID int identity primary key,
	ServicePackage nvarchar(50) not null unique,
	Price decimal(12,2) not null,
	ServiceDuration int not null,
	IsActive bit not null default 1,

	constraint CK_Price
	check (Price > 0),

	constraint CK_ServiceDuration
	check (ServiceDuration > 0)
)
insert into [Services] (ServicePackage, Price, ServiceDuration) values
	('Express Wash', 50000, 10),
	('Basic Wash', 120000, 45),
	('Premium Wash', 200000, 60),
	('Luxury Wash', 500000, 120),
	('Basic Interior Care', 50000, 30),
	('Deep Interior Detailing', 1700000, 360),
	('Interior Odor Elimination', 150000, 60),
	('Wheel Rim Detailing', 450000, 45),
	('Glass Water Spot Removal', 400000, 60),
	('Engine Bay Detailing', 700000, 120);
go

create table Bookings (
	BookingID int identity primary key,
	UserID int foreign key references Customers(UserID),
	LicensePlate nvarchar(25) not null foreign key references Cars(LicensePlate),

	ServiceID int not null foreign key references [Services](ServiceID),
	PaymentMethod nvarchar(25) not null,

	BookTime datetime not null,
	CurrentStatus tinyint not null foreign key references BookingStatus(StatusID),
	CompletedAt datetime,

	constraint CK_PaymentMethod
	check (
		PaymentMethod in ('Cash', 'Online Bank Transfer')
	)
);
go

create table BookingAssignments (
	BookingID int not null foreign key references Bookings(BookingID),
	UserID int not null foreign key references Staff(UserID),

	primary key(BookingID, UserID),
);
go

create table BookingStatusHistory (
	HistoryID int identity primary key,

	BookingID int not null foreign key references Bookings(BookingID),
	StatusID tinyint not null foreign key references BookingStatus(StatusID),

	ChangedAt datetime not null default getdate()
);
go

-- ~~~~~					TRANSACTIONS DATABASE				~~~~~
create table PaymentStatus (
	StatusID tinyint primary key,
	StatusName nvarchar(15) not null unique
)
go
insert into PaymentStatus values
	(1, 'Pending'),
	(2, 'Paid'),
	(3, 'Refunded');
go

create table Payments (
	PaymentID int identity primary key,
	BookingID int not null unique foreign key references Bookings(BookingID),

	Amount decimal(12,2) not null,
	PaymentDate datetime not null default getDate(),
	StatusID tinyint not null foreign key references PaymentStatus(StatusID),

	constraint CK_PaymentAmount
	check (Amount >= 0)
);
go

create table PointsTransactions (
	TransactionID int identity primary key,
	PaymentID int foreign key references Payments(PaymentID),
	UserID int not null foreign key references Customers(UserID),

	PointsChanged int not null,
	[Description] nvarchar(100),
	TransactionDate datetime not null default getDate(),

	constraint CK_PointsTransaction
	check (PointsChanged <> 0)
)
go