CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TYPE type_operation AS ENUM
    ('S', 'I', 'U', 'D');

CREATE TYPE type_gender as ENUM ('W', 'M');

CREATE TABLE pd_processing_consent
(
    consent_id uuid primary key default uuid_generate_v4 (),
    consent_date timestamp,
    person_gender type_gender,
    person_full_name text,
    person_pass_number text,
    ddm_created_at timestamp with time zone DEFAULT now(),
    ddm_created_by text,
    ddm_updated_at timestamp with time zone DEFAULT now(),
    ddm_updated_by text
);

CREATE TABLE pd_processing_consent_hst
(
    consent_id uuid,
    consent_date timestamp with time zone,
    person_gender type_gender,
    person_full_name text,
    person_pass_number text,
    ddm_created_at timestamp with time zone,
    ddm_created_by text,
    ddm_dml_op character(1),
    ddm_system_id uuid,
    ddm_application_id uuid,
    ddm_business_process_id uuid,
    ddm_business_process_definition_id text,
    ddm_business_process_instance_id text,
    ddm_business_activity text,
    ddm_business_activity_instance_id text,
    ddm_digital_sign text,
    ddm_digital_sign_derived text,
    ddm_digital_sign_checksum text,
    ddm_digital_sign_derived_checksum text,
    CONSTRAINT ui_pd_processing_consent_hst UNIQUE (consent_id, ddm_created_at)
);

INSERT INTO pd_processing_consent_hst(
	consent_id, consent_date, person_gender, person_full_name, person_pass_number, ddm_created_at,
	 ddm_created_by, ddm_dml_op, ddm_system_id, ddm_application_id, ddm_business_process_id,
	 ddm_business_process_definition_id, ddm_business_process_instance_id, ddm_business_activity,
	 ddm_business_activity_instance_id, ddm_digital_sign, ddm_digital_sign_derived,
	 ddm_digital_sign_checksum, ddm_digital_sign_derived_checksum)
	VALUES ('3cc262c1-0cd8-4d45-be66-eb0fca821e0a', '2020-01-15 12:00:01+02:00', 'M', 'John Doe Patronymic',
	 'AB123456', '2021-08-20 18:00:07+03:00', 'user', 'I', 'bd223413-214a-4d6d-9dee-39813f15dad0',
	 '04a00dc5-904d-4043-b8b0-aeb2d4c73ee2', 'b533ab28-4068-4e48-9115-e3a74fcfa243',
	 'BP_DEF', 'd3d4db61-049c-4830-b77c-e8a7d2ebec89', 'B_ACT', 'B_ACT_INST_ID', 'DIGN_SIGN', 'DIGN_SIGN_DER',
	 '32b0cfd6c6e2dd5750268f634b788f845fd075103b007110d62c6fae0e94028c',
	 '6926f443a89f6aebe1fa2477e40d4c32b8f4aab524f6dc1dd2aa331304c320c4'),
	 ('9ce4cad9-ff50-4fa3-b893-e07afea0cb8d', '2021-01-17 18:00:00+00', 'W', 'Benjamin Franklin Patronymic',
	 'XY098765', '2021-08-20 18:00:00+00', 'user', 'I', 'bd223413-214a-4d6d-9dee-39813f15dad0',
     '04a00dc5-904d-4043-b8b0-aeb2d4c73ee2', 'b533ab28-4068-4e48-9115-e3a74fcfa243',
     'BP_DEF', 'd3d4db61-049c-4830-b77c-e8a7d2ebec89', 'B_ACT', 'B_ACT_INST_ID', 'D', null,
     'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', null);

CREATE TABLE pd_processing_date
(
    consent_id uuid primary key default uuid_generate_v4 (),
    consent_date date,
    consent_time time with time zone,
    consent_datetime timestamp,
    ddm_created_at timestamp with time zone DEFAULT now(),
    ddm_created_by text,
    ddm_updated_at timestamp with time zone DEFAULT now(),
    ddm_updated_by text
);

CREATE TABLE pd_processing_date_hst
(
    consent_id uuid,
    consent_date date,
    consent_time time with time zone,
    consent_datetime timestamp,
    ddm_created_at timestamp with time zone,
    ddm_created_by text,
    ddm_dml_op character(1),
    ddm_system_id uuid,
    ddm_application_id uuid,
    ddm_business_process_id uuid,
    ddm_business_process_definition_id text,
    ddm_business_process_instance_id uuid,
    ddm_business_activity text,
    ddm_business_activity_instance_id text,
    ddm_digital_sign text,
    ddm_digital_sign_derived text,
    ddm_digital_sign_checksum text,
    ddm_digital_sign_derived_checksum text,
    CONSTRAINT ui_pd_processing_date_hst UNIQUE (consent_id, ddm_created_at)
);

INSERT INTO pd_processing_date_hst(
	consent_id, consent_date, consent_time, consent_datetime, ddm_created_at,
	 ddm_created_by, ddm_dml_op, ddm_system_id, ddm_application_id, ddm_business_process_id,
	 ddm_business_process_definition_id, ddm_business_process_instance_id, ddm_business_activity,
	 ddm_business_activity_instance_id, ddm_digital_sign, ddm_digital_sign_derived,
	 ddm_digital_sign_checksum, ddm_digital_sign_derived_checksum)
	VALUES ('3cc262c1-0cd8-4d45-be66-eb0fca821e0a', '2020-01-15', '12:00:01+02:00', null,
	 '2021-08-20 18:00:07+03:00', 'user', 'I', 'bd223413-214a-4d6d-9dee-39813f15dad0',
	 '04a00dc5-904d-4043-b8b0-aeb2d4c73ee2', 'b533ab28-4068-4e48-9115-e3a74fcfa243',
	 'BP_DEF', 'd3d4db61-049c-4830-b77c-e8a7d2ebec89', 'B_ACT', 'B_ACT_INST_ID', 'DIGN_SIGN', 'DIGN_SIGN_DER',
	 '32b0cfd6c6e2dd5750268f634b788f845fd075103b007110d62c6fae0e94028c',
	 '6926f443a89f6aebe1fa2477e40d4c32b8f4aab524f6dc1dd2aa331304c320c4'),
	 ('9ce4cad9-ff50-4fa3-b893-e07afea0cb8d', '2021-01-17', '18:00:00+02:00', null,
	  '2021-08-20 18:00:00+00', 'user', 'I', 'bd223413-214a-4d6d-9dee-39813f15dad0',
     '04a00dc5-904d-4043-b8b0-aeb2d4c73ee2', 'b533ab28-4068-4e48-9115-e3a74fcfa243',
     'BP_DEF', 'd3d4db61-049c-4830-b77c-e8a7d2ebec89', 'B_ACT', 'B_ACT_INST_ID', 'D', null,
     'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', null);