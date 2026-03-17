CREATE TABLE IF NOT EXISTS currencies (
ID INTEGER PRIMARY KEY autoincrement,
Code VARCHAR(3) NOT NULL UNIQUE,
FullName VARCHAR(100) NOT NULL,
Sign VARCHAR(5) NOT NULL
);

INSERT INTO currencies (Code, FullName, Sign)
VALUES
('USD', 'US Dollar', '$'),
('EUR', 'Euro', '€'),
('GBP', 'British Pound', '£'),
('JPY', 'Yen', '¥'),
('UAH', 'Hryvnia', '₴');