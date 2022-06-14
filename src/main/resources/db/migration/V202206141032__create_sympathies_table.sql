-- -----------------------------------------------------
-- Table `thumb`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `thumbs` (
                                          `id` BIGINT NOT NULL AUTO_INCREMENT,
                                          `created_at` TIMESTAMP DEFAULT now(),
                                          `updated_at` TIMESTAMP DEFAULT now(),
                                          `account_id` BIGINT NOT NULL,
                                          `post_id` BIGINT NOT NULL,
                                          PRIMARY KEY (`id`),
                                          INDEX `fk_thumbs_accounts1_idx` (`account_id` ASC) VISIBLE,
                                          INDEX `fk_thumbs_posts1_idx` (`post_id` ASC) VISIBLE,
                                          CONSTRAINT `fk_thumbs_accounts1`
                                              FOREIGN KEY (`account_id`)
                                                  REFERENCES `accounts` (`id`)
                                                  ON DELETE NO ACTION
                                                  ON UPDATE NO ACTION,
                                          CONSTRAINT `fk_thumbs_posts1`
                                              FOREIGN KEY (`post_id`)
                                                  REFERENCES `posts` (`id`)
                                                  ON DELETE NO ACTION
                                                  ON UPDATE NO ACTION);