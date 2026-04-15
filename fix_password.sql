USE vhr;
UPDATE hr SET password = '$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm' WHERE id = 1;
SELECT id, username, name, password FROM hr;
