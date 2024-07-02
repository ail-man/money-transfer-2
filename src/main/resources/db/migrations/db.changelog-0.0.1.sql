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

-- changeset artur:0.0.1_3 runOnChange:false failOnError:true labels:create_accounts_table
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

-- changeset artur:0.0.1_4 runOnChange:false failOnError:true labels:create_accounts_history_table
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
