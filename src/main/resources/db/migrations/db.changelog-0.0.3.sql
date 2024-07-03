-- liquibase formatted sql

/*
-- Release 0.0.3 changelog.
-- Includes Initial Transaction Schema Creation Script.
 */

-- changeset artur:0.0.1_5 runOnChange:false failOnError:true labels:create_transactions_table
BEGIN;
CREATE TABLE "transactions"
(
  "id"              UUID              NOT NULL,
  "timestamp"       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  "from_account_id" UUID              NOT NULL,
  "to_account_id"   UUID              NOT NULL,
  "amount"          NUMERIC DEFAULT 0 NOT NULL,
  "currency"        CHAR(3)           NOT NULL,
  "info"            JSONB,
  CONSTRAINT "transactions_pk" PRIMARY KEY ("id"),
  CONSTRAINT "transaction_from_account_fk" FOREIGN KEY ("from_account_id") REFERENCES "accounts" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "transaction_to_account_fk" FOREIGN KEY ("to_account_id") REFERENCES "accounts" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE INDEX transactions_from_account_id_idx ON transactions (from_account_id);
CREATE INDEX transactions_to_account_id_idx ON transactions (to_account_id);
COMMIT;
