INSERT INTO "p_user" ("name", "password", "slack_id", "belong", "status", "role", "created_at", "updated_at")
VALUES
    ('ms1', '$2a$10$12CSTy7TYhhRR/klEiJrzu/Vd9bIGrJvYPCC0SF2zntjlQ6fi22bi', 'U09SU4KPL66', null, 'APPROVED', 'MASTER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO "p_user" ("name", "password", "slack_id", "belong", "status", "role", "created_at", "updated_at")
VALUES
    ('cm1', '$2a$10$12CSTy7TYhhRR/klEiJrzu/Vd9bIGrJvYPCC0SF2zntjlQ6fi22bi', 'U09SU4KPL66', '6cb39cec-c72b-41c2-8c80-47b00a7cf9c5', 'PENDING', 'COMPANY_MANAGER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO "p_user" ("name", "password", "slack_id", "belong", "status", "role", "created_at", "updated_at")
VALUES
    ('hm1', '$2a$10$12CSTy7TYhhRR/klEiJrzu/Vd9bIGrJvYPCC0SF2zntjlQ6fi22bi', 'U09SU4KPL66', '6cb39cec-c72b-41c2-8c80-47b00a7cf9c5', 'PENDING', 'HUB_MANAGER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO "p_user" ("name", "password", "slack_id", "belong", "status", "role", "created_at", "updated_at")
VALUES
    ('hd1', '$2a$10$12CSTy7TYhhRR/klEiJrzu/Vd9bIGrJvYPCC0SF2zntjlQ6fi22bi', 'U09SU4KPL66', null, 'PENDING', 'HUB_DRIVER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO "p_user" ("name", "password", "slack_id", "belong", "status", "role", "created_at", "updated_at")
VALUES
    ('cd1', '$2a$10$12CSTy7TYhhRR/klEiJrzu/Vd9bIGrJvYPCC0SF2zntjlQ6fi22bi', 'U09SU4KPL66', '6cb39cec-c72b-41c2-8c80-47b00a7cf9c5', 'PENDING', 'COMPANY_DRIVER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
