-- src/main/resources/data.sql

-- Insert roles
INSERT INTO roles (id, name) VALUES (1, 'ADMINISTRATEUR');
INSERT INTO roles (id, name) VALUES (2, 'ETUDIANT');
INSERT INTO roles (id, name) VALUES (3, 'FORMATEUR');

/*
 By default, Spring Boot will automatically run data.sql
 on startup if you have spring.jpa.hibernate.ddl-auto set to
 update (as in your application.properties).

 */