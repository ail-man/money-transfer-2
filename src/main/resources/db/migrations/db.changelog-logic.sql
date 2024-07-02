--liquibase formatted sql

/*
-- DB Logic changelog (Stored Procedures, Functions, Packages, Triggers, etc.).
-- Includes INSERT/UPDATE triggers.
 */

--changeset -:- runOnChange:true splitStatements:false labels:db_logic_changelog
create or replace function save_customer_in_history()
  returns trigger as
$$
begin
  if (new.version is distinct from old.version) then
    insert into customers_history (id,
                                   version,
                                   enabled,
                                   info,
                                   created_at,
                                   updated_at)
    values (new.id,
            new.version,
            new.enabled,
            new.info,
            new.created_at,
            new.updated_at);
  end if;
  return new;
end;
$$ language plpgsql;

drop trigger if exists customers_history_update_tr on customers;

create trigger customers_history_update_tr
  after insert or update of version
  on customers
  for each row
execute procedure save_customer_in_history();


create or replace function save_account_in_history()
  returns trigger as
$$
begin
  if (new.version is distinct from old.version) then
    insert into accounts_history (id,
                                  version,
                                  enabled,
                                  customer_id,
                                  info,
                                  created_at,
                                  expires_at)
    values (new.id,
            new.version,
            new.enabled,
            new.customer_id,
            new.info,
            new.created_at,
            new.expires_at);
  end if;
  return new;
end;
$$ language plpgsql;

drop trigger if exists accounts_history_update_tr on customers;

create trigger accounts_history_update_tr
  after insert or update of version
  on accounts
  for each row
execute procedure save_account_in_history();
