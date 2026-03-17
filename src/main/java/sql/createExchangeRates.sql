CREATE TABLE IF NOT EXISTS exchange_rates (
ID INTEGER PRIMARY KEY AUTOINCREMENT,
BaseCurrencyId INTEGER NOT NULL,
TargetCurrencyId INTEGER NOT NULL,
Rate DECIMAL(10, 6) NOT NULL,

FOREIGN KEY (BaseCurrencyId) REFERENCES currencies (ID),
FOREIGN KEY (TargetCurrencyId) REFERENCES currencies (ID)
);

INSERT INTO exchange_rates (BaseCurrencyId, TargetCurrencyId, Rate)
VALUES
(1, 2, 0.86),
(1, 3, 0.79),
(1, 4, 157.65),
(1, 5, 43.91),
(2, 1, 1.14),
(2, 3, 0.87),
(2, 4, 183.63),
(2, 5, 51.07),
(3, 1, 1.35),
(3, 2, 1.16),
(3, 4, 212.23),
(3, 5, 59.01),
(4, 1, 0.0063),
(4, 2, 0.0054),
(4, 3, 0.0047),
(4, 5, 0.28),
(5, 1, 0.023),
(5, 2, 0.020),
(5, 3, 0.017),
(5, 4, 3.59);