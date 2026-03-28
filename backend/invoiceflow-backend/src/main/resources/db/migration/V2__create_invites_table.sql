create table invites (
  id uuid primary key default gen_random_uuid(),

  company_id uuid not null references companies(id) on delete restrict,

  email text not null,
  role text not null,

  status text not null default 'PENDING', -- PENDING | ACCEPTED | REVOKED
  created_at timestamp not null default now(),
  accepted_at timestamp null,

  created_by_user_id uuid not null references users(id) on delete restrict
);

-- One active invite per company + email
create unique index invites_company_email_unique
  on invites(company_id, lower(email))
  where status = 'PENDING';

-- Helpful indexes
create index invites_company_id_idx on invites(company_id);
create index invites_email_idx on invites(lower(email));