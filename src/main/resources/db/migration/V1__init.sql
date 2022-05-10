-- -----------------------------------------------------
-- Table `accounts`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `accounts` (
                                          `id` BIGINT NOT NULL AUTO_INCREMENT,
                                          `email` VARCHAR(100) NOT NULL,
                                          `nickname` VARCHAR(45) NULL,
                                          `password` VARCHAR(255) NOT NULL,
                                          `email_confirmed` BIT NOT NULL DEFAULT FALSE,
                                          `email_confirm_token` VARCHAR(255) NULL,
                                          `account_type` VARCHAR(45) NOT NULL,
                                          `created_at` TIMESTAMP DEFAULT now(),
                                          `updated_at` TIMESTAMP DEFAULT now(),
                                          PRIMARY KEY (`id`),
                                          UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE,
                                          UNIQUE INDEX `nickname_UNIQUE` (`nickname` ASC) VISIBLE);


-- -----------------------------------------------------
-- Table `posts`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `posts` (
                                       `id` BIGINT NOT NULL AUTO_INCREMENT,
                                       `title` VARCHAR(255) NOT NULL,
                                       `content` LONGTEXT NULL,
                                       `created_at` TIMESTAMP DEFAULT now(),
                                       `updated_at` TIMESTAMP DEFAULT now(),
                                       `account_id` BIGINT NOT NULL,
                                       PRIMARY KEY (`id`),
                                       INDEX `fk_post_account_idx` (`account_id` ASC) VISIBLE,
                                       CONSTRAINT `fk_post_account`
                                           FOREIGN KEY (`account_id`)
                                               REFERENCES `accounts` (`id`)
                                               ON DELETE NO ACTION
                                               ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `files`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `files` (
                                       `id` BIGINT NOT NULL AUTO_INCREMENT,
                                       `name` VARCHAR(255) NOT NULL,
                                       `path` VARCHAR(255) NOT NULL,
                                       `ext` VARCHAR(45) NOT NULL,
                                       `created_at` TIMESTAMP DEFAULT now(),
                                       `updated_at` TIMESTAMP DEFAULT now(),
                                       `post_id` BIGINT NOT NULL,
                                       PRIMARY KEY (`id`),
                                       INDEX `fk_file_post1_idx` (`post_id` ASC) VISIBLE,
                                       CONSTRAINT `fk_file_post1`
                                           FOREIGN KEY (`post_id`)
                                               REFERENCES `posts` (`id`)
                                               ON DELETE NO ACTION
                                               ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `comments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `comments` (
                                          `id` BIGINT NOT NULL AUTO_INCREMENT,
                                          `content` VARCHAR(255) NOT NULL,
                                          `created_at` TIMESTAMP DEFAULT now(),
                                          `updated_at` TIMESTAMP DEFAULT now(),
                                          `account_id` BIGINT NOT NULL,
                                          `post_id` BIGINT NOT NULL,
                                          PRIMARY KEY (`id`),
                                          INDEX `fk_comments_accounts1_idx` (`account_id` ASC) VISIBLE,
                                          INDEX `fk_comments_posts1_idx` (`post_id` ASC) VISIBLE,
                                          CONSTRAINT `fk_comments_accounts1`
                                              FOREIGN KEY (`account_id`)
                                                  REFERENCES `accounts` (`id`)
                                                  ON DELETE NO ACTION
                                                  ON UPDATE NO ACTION,
                                          CONSTRAINT `fk_comments_posts1`
                                              FOREIGN KEY (`post_id`)
                                                  REFERENCES `posts` (`id`)
                                                  ON DELETE NO ACTION
                                                  ON UPDATE NO ACTION);