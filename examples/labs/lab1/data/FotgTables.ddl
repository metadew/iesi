CREATE TABLE #team#.CUSTOMERS
(
	ID				NUMERIC NOT NULL,
	DateJoined		TIMESTAMP,
	BirthDate		TIMESTAMP,
	FirstName		TEXT,
	LastName			TEXT,
	Phone				TEXT,
	Street				TEXT,
	City				TEXT,
	Country				TEXT,
	Gender				TEXT,
	Email				TEXT
);

CREATE TABLE #team#.RESTAURANTS
(
	ID				NUMERIC NOT NULL,
	DateJoined		TIMESTAMP,
	Name				TEXT,
	Street				TEXT,
	City				TEXT,
	Country				TEXT,
	Email				TEXT
);

CREATE TABLE #team#.ORDERS
(
	ID				NUMERIC NOT NULL,
	CustomerID		NUMERIC NOT NULL,
	RestaurantID		NUMERIC NOT NULL,
	OrderDate		TIMESTAMP,
	Price				NUMERIC
);