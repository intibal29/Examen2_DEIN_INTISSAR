SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";
DROP SCHEMA IF EXISTS `libros` ;
CREATE SCHEMA IF NOT EXISTS `libros` DEFAULT CHARACTER SET latin1 COLLATE latin1_spanish_ci;
USE `libros` ;

-- -----------------------------------------------------
-- Table `libros`.`Alumno`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `libros`.`Alumno` (
	`dni` VARCHAR(9) NOT NULL,
	`nombre` VARCHAR(150) NULL DEFAULT NULL,
	`apellido1` VARCHAR(150) NULL DEFAULT NULL,
	`apellido2` VARCHAR(150) NULL DEFAULT NULL,
	PRIMARY KEY (`dni`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_spanish_ci;


-- -----------------------------------------------------
-- Table `libros`.`Libro`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `libros`.`Libro` (
	`codigo` INT NOT NULL AUTO_INCREMENT,
	`titulo` VARCHAR(150) NULL DEFAULT NULL,
	`autor` VARCHAR(200) NULL DEFAULT NULL,
	`editorial` VARCHAR(150) NULL DEFAULT NULL,
	`estado` VARCHAR(50) NULL DEFAULT NULL,
	`baja` INT NULL DEFAULT '0',
	PRIMARY KEY (`codigo`)
)ENGINE = InnoDB DEFAULT CHARACTER SET = latin1 COLLATE = latin1_spanish_ci;


-- -----------------------------------------------------
-- Table `libros`.`Historio_prestamo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `libros`.`Historico_prestamo` (
	`id_prestamo` INT NOT NULL AUTO_INCREMENT,
	`dni_alumno` VARCHAR(9) NOT NULL,
	`codigo_libro` INT NOT NULL,
	`fecha_prestamo` DATETIME NULL DEFAULT NULL,
	`fecha_devolucion` DATETIME NULL DEFAULT NULL,
	PRIMARY KEY (`id_prestamo`),
	CONSTRAINT `FK_Historico_prestamo_Alumno` FOREIGN KEY (`dni_alumno`) REFERENCES `libros`.`Alumno` (`dni`),
	CONSTRAINT `FK_Historico_prestamo_Libro` FOREIGN KEY (`codigo_libro`) REFERENCES `libros`.`Libro` (`codigo`)
)ENGINE = InnoDB DEFAULT CHARACTER SET = latin1 COLLATE = latin1_spanish_ci;


-- -----------------------------------------------------
-- Table `libros`.`Prestamo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `libros`.`Prestamo` (
	`id_prestamo` INT  NOT NULL AUTO_INCREMENT,
	`dni_alumno` VARCHAR(9) NOT NULL,
	`codigo_libro` INT NOT NULL,
	`fecha_prestamo` DATETIME NULL DEFAULT NULL,
	PRIMARY KEY (`id_prestamo`),
	CONSTRAINT `FK_Prestamo_Libro` FOREIGN KEY (`codigo_libro`)  REFERENCES `libros`.`Libro` (`codigo`),
	CONSTRAINT `FK_Prestamo_Alumno` FOREIGN KEY (`dni_alumno`) REFERENCES `libros`.`Alumno` (`dni`)
)ENGINE = InnoDB DEFAULT CHARACTER SET = latin1 COLLATE = latin1_spanish_ci;

COMMIT;

