INSERT INTO products (title, description, image_name, price) VALUES
                                                                 ('Product 1', 'Description for product 1', '100.png', 19.99),
                                                                 ('Product 2', 'Description for product 2', '200.jpg', 29.99),
                                                                 ('Product 3', 'Description for product 3', '300.jpeg', 39.99),
                                                                 ('Product 4', 'Description for product 4', '400.jpeg', 49.99),
                                                                 ('Product 5', 'Description for product 5', '500.png', 59.99),
                                                                 ('Product 6', 'Description for product 6', '600.jpeg', 69.99),
                                                                 ('Product 7', 'Description for product 7', '700.png', 79.99),
                                                                 ('Product 8', 'Description for product 8', '800.jpeg', 89.99),
                                                                 ('Product 9', 'Description for product 9', '900.png', 99.99);

-- Вставка данных в таблицу orders
INSERT INTO orders (number, total_price) VALUES
                                             ('ORD12345', 379.94),
                                             ('ORD12346', 409.90),
                                             ('ORD12347', 599.90),
                                             ('ORD12348', 169.97),
                                             ('ORD12349', 267.97);

-- Вставка данных в таблицу order_items
INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (1, 1, 1, 19.99),
                                                                       (1, 2, 2, 59.98),
                                                                       (1, 9, 3, 299.97),
                                                                       (2, 3, 9, 359.91),
                                                                       (2, 4, 1, 49.99),
                                                                       (3, 5, 10, 599.90),
                                                                       (4, 6, 1, 69.99),
                                                                       (4, 1, 2, 99.98),
                                                                       (5, 8, 3, 267.97);