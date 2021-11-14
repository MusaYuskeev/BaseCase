CREATE TABLE IF NOT EXISTS public.resume
(
    uuid character(36) COLLATE pg_catalog."default" NOT NULL,
    full_name text COLLATE pg_catalog."default",
    CONSTRAINT resume_pkey PRIMARY KEY (uuid)
)

TABLESPACE pg_default;

ALTER TABLE public.resume
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS public.contact
(
    id SERIAL,
    type text COLLATE pg_catalog."default" NOT NULL,
    value text COLLATE pg_catalog."default" NOT NULL,
    resume_uuid character(36) REFERENCES public.resume (uuid) ON DELETE CASCADE NOT NULL,
    CONSTRAINT contact_pkey PRIMARY KEY (id),


)
CREATE INDEX contact_uuid_type_index
    ON public.contact
    (resume_uuid , type )
;

