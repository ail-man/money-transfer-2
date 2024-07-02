-- liquibase formatted sql

/*
-- Release 0.0.1 changelog.
-- Includes Initial Schema Creation Script.
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

-- changeset artur:0.0.1_3 runOnChange:false failOnError:true labels:create_accounts_table
BEGIN;
CREATE TABLE "accounts"
(
  "id"          UUID                        NOT NULL,
  "customer_id" UUID                        NOT NULL,
  "version"     INTEGER DEFAULT 0           NOT NULL,
  "enabled"     BOOLEAN DEFAULT FALSE       NOT NULL,
  "balance"     NUMERIC DEFAULT 0           NOT NULL,
  "currency"    CHAR(3)                     NOT NULL,
  "info"        JSONB,
  "created_at"  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  "updated_at"  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  "expires_at"  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT "accounts_pk" PRIMARY KEY ("id"),
  CONSTRAINT "accounts_to_customers_fk" FOREIGN KEY ("customer_id") REFERENCES "customers" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE INDEX accounts_customer_id_idx ON accounts (customer_id);
COMMIT;

-- changeset artur:0.0.1_4 runOnChange:false failOnError:true labels:create_accounts_history_table
BEGIN;
CREATE TABLE "accounts_history"
(
  "id"          UUID                        NOT NULL,
  "customer_id" UUID                        NOT NULL,
  "version"     INTEGER DEFAULT 0           NOT NULL,
  "enabled"     BOOLEAN DEFAULT FALSE       NOT NULL,
  "balance"     NUMERIC DEFAULT 0           NOT NULL,
  "currency"    CHAR(3)                     NOT NULL,
  "info"        JSONB,
  "created_at"  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  "updated_at"  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  "expires_at"  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT "accounts_history_pk" PRIMARY KEY ("id", "version"),
  CONSTRAINT "accounts_history_to_accounts_fk" FOREIGN KEY ("id") REFERENCES "accounts" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "accounts_history_to_customers_fk" FOREIGN KEY ("customer_id") REFERENCES "customers" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE INDEX accounts_history_id_idx ON customers_history (id);
COMMIT;

-- changeset artur:0.0.1_5 runOnChange:false failOnError:true labels:create_transactions_table
BEGIN;
CREATE TABLE "transactions"
(
  "id"              UUID                        NOT NULL,
  "timestamp"       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  "from_account_id" UUID                        NOT NULL,
  "to_account_id"   UUID                        NOT NULL,
  "amount"          NUMERIC DEFAULT 0           NOT NULL,
  "currency"        CHAR(3)                     NOT NULL,
  "info"            JSONB,
  CONSTRAINT "transactions_pk" PRIMARY KEY ("id"),
  CONSTRAINT "transaction_from_account_fk" FOREIGN KEY ("from_account_id") REFERENCES "accounts" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "transaction_to_account_fk" FOREIGN KEY ("to_account_id") REFERENCES "accounts" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE INDEX transactions_from_account_id_idx ON transactions (from_account_id);
CREATE INDEX transactions_to_account_id_idx ON transactions (to_account_id);
COMMIT;
