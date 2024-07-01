-- liquibase formatted sql

/*
-- Release 0.0.1 changelog.
-- Includes Initial Schema Creation Script.
 */

-- changeset artur:0.0.1_1 runOnChange:false failOnError:true labels:create_customers_table
CREATE TABLE "customers"
(
  "id"         UUID    NOT NULL,
  "version"    INTEGER NOT NULL DEFAULT 0,
  "enabled"    BOOLEAN          DEFAULT FALSE NOT NULL,
  "info"       JSONB,
  "created_at" TIMESTAMP WITHOUT TIME ZONE,
  "updated_at" TIMESTAMP WITHOUT TIME ZONE,
  CONSTRAINT "customers_pk" PRIMARY KEY ("id")
);

-- changeset artur:0.0.1_2 runOnChange:false failOnError:true labels:create_customers_history_table
CREATE TABLE "customers_history"
(
  "id"         UUID    NOT NULL,
  "version"    INTEGER NOT NULL DEFAULT 0,
  "enabled"    BOOLEAN          DEFAULT FALSE NOT NULL,
  "info"       JSONB,
  "created_at" TIMESTAMP WITHOUT TIME ZONE,
  "updated_at" TIMESTAMP WITHOUT TIME ZONE,
  CONSTRAINT "customers_history_pk" PRIMARY KEY ("id", "version"),
  CONSTRAINT "customers_history_to_customers_fk" FOREIGN KEY ("id") REFERENCES "customers" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
