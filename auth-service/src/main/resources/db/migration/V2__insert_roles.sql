INSERT INTO roles (name) VALUES
                             ('USER'),
                             ('ADMIN'),
                             ('COURIER')
ON CONFLICT (name) DO NOTHING;