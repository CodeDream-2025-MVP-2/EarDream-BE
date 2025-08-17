-- 테스트용 users 테이블 생성
DROP TABLE users;

CREATE TABLE users (
    id                NUMBER GENERATED AS IDENTITY PRIMARY KEY,
    clerk_id          VARCHAR2(255) UNIQUE,
    name              VARCHAR2(100) NOT NULL,
    phone_number      VARCHAR2(20),
    profile_image_url VARCHAR2(500),
    birth_date        DATE,
    address           VARCHAR2(500),
    user_type         VARCHAR2(20) DEFAULT 'ACTIVE_USER' CHECK (user_type IN ('PENDING_RECIPIENT', 'ACTIVE_USER')),
    family_role       VARCHAR2(50),
    is_leader         CHAR(1) DEFAULT 'N' CHECK (is_leader IN ('Y', 'N')),
    is_receiver       CHAR(1) DEFAULT 'N' CHECK (is_receiver IN ('Y', 'N')),
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 테스트 데이터 삽입
INSERT INTO users (name, phone_number, clerk_id, user_type, family_role, is_leader) 
VALUES ('홍길동', '010-1234-5678', 'clerk_test_123', 'ACTIVE_USER', '아들', 'Y');

INSERT INTO users (name, phone_number, address, user_type, family_role, is_receiver) 
VALUES ('김할머니', '010-9876-5432', '서울시 강남구', 'PENDING_RECIPIENT', '어머니', 'Y');

COMMIT;