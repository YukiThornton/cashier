\c cashier cashier

INSERT INTO cashier.cash (value, currency_code) VALUES
  (1, 'JPY'),
  (5, 'JPY'),
  (10, 'JPY'),
  (50, 'JPY'),
  (100, 'JPY'),
  (500, 'JPY'),
  (1000, 'JPY'),
  (5000, 'JPY'),
  (10000, 'JPY')
;

INSERT INTO cashier.cash_count (cash_id, num) SELECT id, 10 FROM cashier.cash;
