CREATE TABLE CUSTOMERS
(
	ID				NUMERIC NOT NULL,
	DateJoined		TEXT,
	BirthDate		TEXT,
	FirstName		TEXT,
	LastName			TEXT,
	Phone				TEXT,
	Street				TEXT,
	City				TEXT,
	Country				TEXT,
	Gender				TEXT,
	Email				TEXT
);

CREATE TABLE RESTAURANTS
(
	ID				NUMERIC NOT NULL,
	DateJoined		TEXT,
	Name				TEXT,
	Street				TEXT,
	City				TEXT,
	Country				TEXT,
	Email				TEXT
);

CREATE TABLE ORDERS
(
	ID				NUMERIC NOT NULL,
	CustomerID		NUMERIC NOT NULL,
	RestaurantID		NUMERIC NOT NULL,
	OrderDate		TEXT,
	Price				NUMERIC
);