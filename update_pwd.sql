USE vhr;
UPDATE hr SET password = '$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm' WHERE username = 'admin';
SELECT id, username, name, LEFT(password, 50) as password FROM hr;
