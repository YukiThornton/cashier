\c cashier cashier

CREATE SCHEMA cashier;

CREATE TABLE cashier.cash (
  id SERIAL NOT NULL,
  value INTEGER NOT NULL,
  currency_code TEXT NOT NULL,
  PRIMARY KEY(id),
  UNIQUE(value, currency_code)
);

CREATE TABLE cashier.cash_count (
  cash_id INTEGER NOT NULL,
  num INTEGER NOT NULL,
  PRIMARY KEY(cash_id),
  FOREIGN KEY(cash_id) REFERENCES cashier.cash(id)
);
