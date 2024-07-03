-- liquibase formatted sql

/*
-- Release 0.0.1 changelog.
-- Includes Initial Customer Schema Creation Script.
 */

-- changeset artur:0.0.1_1 runOnChange:false failOnError:true labels:create_customers_table
CREATE TABLE "customers"
(
  "id"         UUID                        NOT NULL,
  "version"    INTEGER DEFAULT 0           NOT NULL,
  "enabled"    BOOLEAN DEFAULT FALSE       NOT NULL,
  "info"       JSONB,
  "created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT "customers_pk" PRIMARY KEY ("id")
);

-- changeset artur:0.0.1_2 runOnChange:false failOnError:true labels:create_customers_history_table
BEGIN;
CREATE TABLE "customers_history"
(
  "id"         UUID                        NOT NULL,
  "version"    INTEGER DEFAULT 0           NOT NULL,
  "enabled"    BOOLEAN DEFAULT FALSE       NOT NULL,
  "info"       JSONB,
  "created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT "customers_history_pk" PRIMARY KEY ("id", "version"),
  CONSTRAINT "customers_history_to_customers_fk" FOREIGN KEY ("id") REFERENCES "customers" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE INDEX customers_history_id_idx ON customers_history (id);
COMMIT;
