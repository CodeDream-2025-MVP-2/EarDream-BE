-- EarDream 데이터베이스 스키마 (ERDCloud 호환, Oracle 용)

-- =================================================================
-- 1. 테이블 생성 (Table Creation)
-- =================================================================

CREATE TABLE users (
                       id                NUMBER GENERATED AS IDENTITY PRIMARY KEY,
                       kakao_id          VARCHAR2(255) UNIQUE,
                       name              VARCHAR2(100) NOT NULL,
                       phone_number      VARCHAR2(20),
                       profile_image_url VARCHAR2(500),
                       birth_date        DATE,
                       address           VARCHAR2(500),
                       created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE families (
                          id                NUMBER GENERATED AS IDENTITY PRIMARY KEY,
                          user_id           NUMBER NOT NULL,
                          family_name       VARCHAR2(100) NOT NULL,
                          family_profile_image_url VARCHAR2(500),
                          monthly_deadline  NUMBER(1) CHECK (monthly_deadline IN (2, 4)),
                          invite_code       VARCHAR2(10) UNIQUE NOT NULL,
                          status            VARCHAR2(20) DEFAULT 'ACTIVE',
                          created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_families_user FOREIGN KEY (user_id) REFERENCES users(id)

);

CREATE TABLE family_members (
                                id                NUMBER GENERATED AS IDENTITY PRIMARY KEY,
                                family_id         NUMBER NOT NULL,
                                user_id           NUMBER NOT NULL,
                                relationship      VARCHAR2(50) NOT NULL,
                                role              VARCHAR2(20) DEFAULT 'MEMBER' CHECK (role IN ('LEADER', 'MEMBER', 'RECEIVER')),
                                joined_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                CONSTRAINT fk_family_members_family FOREIGN KEY (family_id) REFERENCES families(id),
                                CONSTRAINT fk_family_members_user FOREIGN KEY (user_id) REFERENCES users(id),
                                CONSTRAINT uk_family_user UNIQUE (family_id, user_id)
);

CREATE TABLE posts (
                       id                NUMBER GENERATED AS IDENTITY PRIMARY KEY,
                       family_id         NUMBER NOT NULL,
                       user_id           NUMBER NOT NULL,
                       title             VARCHAR2(200) NOT NULL,
                       content           VARCHAR2(500),
                       post_month        VARCHAR2(7) NOT NULL,
                       created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT fk_posts_family FOREIGN KEY (family_id) REFERENCES families(id),
                       CONSTRAINT fk_posts_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE post_images (
                             id                NUMBER GENERATED AS IDENTITY PRIMARY KEY,
                             post_id           NUMBER NOT NULL,
                             image_url         VARCHAR2(500) NOT NULL,
                             description       VARCHAR2(200),
                             image_order       NUMBER(1) CHECK (image_order BETWEEN 1 AND 4),
                             created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             CONSTRAINT fk_post_images_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

CREATE TABLE subscriptions (
                               id                     NUMBER GENERATED AS IDENTITY PRIMARY KEY,
                               family_id              NUMBER NOT NULL,
                               plan_price             NUMBER(10,0) NOT NULL,
                               status                 VARCHAR2(20) DEFAULT 'ACTIVE',
                               next_billing_date      DATE,
                               inicis_billkey         VARCHAR2(100),
                               started_at             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               cancelled_at           TIMESTAMP,
                               pause_started_at       TIMESTAMP,
                               pause_ended_at         TIMESTAMP,
                               CONSTRAINT fk_subscriptions_family FOREIGN KEY (family_id) REFERENCES families(id)
);

CREATE TABLE publications (
                              id                NUMBER GENERATED AS IDENTITY PRIMARY KEY,
                              family_id         NUMBER NOT NULL,
                              publication_month VARCHAR2(7) NOT NULL,
                              pdf_url           VARCHAR2(500),
                              status            VARCHAR2(20) DEFAULT 'PREPARING',
                              carrier_id   VARCHAR2(30),
                              tracking_number   VARCHAR2(30),
                              created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              delivered_at      TIMESTAMP,
                              CONSTRAINT fk_publications_family FOREIGN KEY (family_id) REFERENCES families(id),
                              CONSTRAINT uk_family_month UNIQUE (family_id, publication_month)
);

CREATE TABLE invitations (
                             id                NUMBER GENERATED AS IDENTITY PRIMARY KEY,
                             family_id         NUMBER NOT NULL,
                             invite_code       VARCHAR2(10) NOT NULL,
                             invited_user_id   NUMBER,
                             status            VARCHAR2(20) DEFAULT 'PENDING',
                             expires_at        TIMESTAMP,
                             created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             accepted_at       TIMESTAMP,
                             CONSTRAINT fk_invitations_family FOREIGN KEY (family_id) REFERENCES families(id),
                             CONSTRAINT fk_invitations_user FOREIGN KEY (invited_user_id) REFERENCES users(id)
);

-- =================================================================
-- 2. 테이블 코멘트 (Table Comments)
-- =================================================================

COMMENT ON TABLE users IS '사용자 정보를 관리하는 테이블';
COMMENT ON TABLE families IS '가족 그룹 정보를 관리하는 테이블';
COMMENT ON TABLE family_members IS '가족 그룹 구성원 정보와 권한을 관리하는 테이블';
COMMENT ON TABLE posts IS '가족 구성원이 작성한 소식 게시글을 관리하는 테이블';
COMMENT ON TABLE post_images IS '소식 게시글에 첨부된 이미지를 관리하는 테이블';
COMMENT ON TABLE subscriptions IS '가족 그룹의 정기 구독 및 이니시스 결제 정보를 관리하는 테이블';
COMMENT ON TABLE publications IS '월간 소식지 발행 및 배송 상태를 관리하는 테이블';
COMMENT ON TABLE invitations IS '가족 그룹 초대 코드 생성 및 사용 이력을 관리하는 테이블';

-- =================================================================
-- 3. 컬럼 코멘트 (Column Comments)
-- =================================================================

-- users
COMMENT ON COLUMN users.id IS '사용자 내부 고유 ID (대체키, 자동증가)';
COMMENT ON COLUMN users.kakao_id IS '카카오 사용자 고유 ID';
COMMENT ON COLUMN users.name IS '사용자/받는 분 실명';
COMMENT ON COLUMN users.phone_number IS '전화번호 (암호화 저장)';
COMMENT ON COLUMN users.profile_image_url IS '프로필 이미지 로컬 저장 경로';
COMMENT ON COLUMN users.birth_date IS '생년월일';
COMMENT ON COLUMN users.address IS '소식지 배송 주소 (암호화 저장)';
COMMENT ON COLUMN users.created_at IS '등록일시';
COMMENT ON COLUMN users.updated_at IS '최종 수정일시';

-- families
COMMENT ON COLUMN families.id IS '가족 그룹 내부 고유 ID (대체키, 자동증가)';
COMMENT ON COLUMN families.user_id IS '가족 생성자 사용자 ID';
COMMENT ON COLUMN families.family_name IS '가족 그룹 이름';
COMMENT ON COLUMN families.family_profile_image_url IS '가족 그룹 프로필 이미지 로컬 저장 경로';
COMMENT ON COLUMN families.monthly_deadline IS '월간 소식지 마감 주차 (2=둘째주, 4=넷째주)';
COMMENT ON COLUMN families.invite_code IS '가족 초대용 고유 코드 (10자리)';
COMMENT ON COLUMN families.status IS '가족 그룹 상태 (ACTIVE, INACTIVE)';
COMMENT ON COLUMN families.created_at IS '가족 그룹 생성일시';
COMMENT ON COLUMN families.updated_at IS '최종 수정일시';

-- family_members
COMMENT ON COLUMN family_members.id IS '구성원 관계 내부 고유 ID (대체키, 자동증가)';
COMMENT ON COLUMN family_members.family_id IS '소속 가족 그룹 ID';
COMMENT ON COLUMN family_members.user_id IS '구성원 사용자 ID';
COMMENT ON COLUMN family_members.relationship IS '받는 분과의 관계 (아들, 딸 등)';
COMMENT ON COLUMN family_members.role IS '가족 내 역할 (LEADER, MEMBER, RECEIVER)';
COMMENT ON COLUMN family_members.joined_at IS '가족 그룹 가입일시';

-- posts
COMMENT ON COLUMN posts.id IS '소식 게시글 내부 고유 ID (대체키, 자동증가)';
COMMENT ON COLUMN posts.family_id IS '소속 가족 그룹 ID';
COMMENT ON COLUMN posts.user_id IS '소식 작성자 ID';
COMMENT ON COLUMN posts.content IS '소식 텍스트 내용 (최대 100자)';
COMMENT ON COLUMN posts.post_month IS '소식 작성 월 (YYYY-MM)';
COMMENT ON COLUMN posts.created_at IS '소식 작성일시';
COMMENT ON COLUMN posts.updated_at IS '소식 수정일시';

-- post_images
COMMENT ON COLUMN post_images.id IS '이미지 내부 고유 ID (대체키, 자동증가)';
COMMENT ON COLUMN post_images.post_id IS '소속 소식 게시글 ID';
COMMENT ON COLUMN post_images.image_url IS '이미지 로컬 저장 경로 (/uploads/images/)';
COMMENT ON COLUMN post_images.image_order IS '이미지 표시 순서 (1-4)';
COMMENT ON COLUMN post_images.created_at IS '이미지 업로드일시';

-- subscriptions
COMMENT ON COLUMN subscriptions.id IS '구독 내부 고유 ID (대체키, 자동증가)';
COMMENT ON COLUMN subscriptions.family_id IS '구독 가족 그룹 ID';
COMMENT ON COLUMN subscriptions.plan_price IS '구독 월 요금 (원, 정수)';
COMMENT ON COLUMN subscriptions.status IS '구독 상태 (ACTIVE, CANCELLED, PAUSED)';
COMMENT ON COLUMN subscriptions.next_billing_date IS '다음 결제 예정일';
COMMENT ON COLUMN subscriptions.inicis_billkey IS '이니시스 빌링키 (정기결제용)';
COMMENT ON COLUMN subscriptions.started_at IS '구독 시작일시';
COMMENT ON COLUMN subscriptions.cancelled_at IS '구독 해지일시';
COMMENT ON COLUMN subscriptions.pause_started_at IS '구독 일시정지 시작일시';
COMMENT ON COLUMN subscriptions.pause_ended_at IS '구독 일시정지 종료일시';

-- publications
COMMENT ON COLUMN publications.id IS '소식지 발행 내부 고유 ID (대체키, 자동증가)';
COMMENT ON COLUMN publications.family_id IS '발행 대상 가족 그룹 ID';
COMMENT ON COLUMN publications.publication_month IS '발행 월 (YYYY-MM)';
COMMENT ON COLUMN publications.pdf_url IS '완성된 소식지 PDF 로컬 저장 경로 (/uploads/pdfs/)';
COMMENT ON COLUMN publications.status IS '발행 상태 (PREPARING, READY, DELIVERED)';
COMMENT ON COLUMN publications.carrier_id IS '택배사 ID';
COMMENT ON COLUMN publications.tracking_number IS '운송장 번호';
COMMENT ON COLUMN publications.created_at IS '발행 요청일시';
COMMENT ON COLUMN publications.delivered_at IS '배송 완료일시';

-- invitations
COMMENT ON COLUMN invitations.id IS '초대 이력 내부 고유 ID (대체키, 자동증가)';
COMMENT ON COLUMN invitations.family_id IS '초대 대상 가족 그룹 ID';
COMMENT ON COLUMN invitations.invite_code IS '생성된 초대 코드';
COMMENT ON COLUMN invitations.invited_user_id IS '초대를 수락한 사용자 ID (users.id 참조)';
COMMENT ON COLUMN invitations.status IS '초대 상태 (PENDING, ACCEPTED, APPROVED, EXPIRED)';
COMMENT ON COLUMN invitations.expires_at IS '초대 코드 만료일시';
COMMENT ON COLUMN invitations.created_at IS '초대 코드 생성일시';
COMMENT ON COLUMN invitations.accepted_at IS '초대 수락일시';

-- =================================================================
-- 4. 소식 책자 (Books)
-- =================================================================

CREATE TABLE books (
  id            NUMBER GENERATED AS IDENTITY PRIMARY KEY,
  family_id     NUMBER NOT NULL,
  name          VARCHAR2(200) NOT NULL,
  pdf_url       VARCHAR2(500),
  image_url     VARCHAR2(500),
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_books_family FOREIGN KEY (family_id) REFERENCES families(id)
);

COMMENT ON TABLE books IS '가족별 소식 책자 (PDF/이미지 링크 포함)';
COMMENT ON COLUMN books.id IS '책자 ID';
COMMENT ON COLUMN books.family_id IS '가족 ID';
COMMENT ON COLUMN books.name IS '책자 이름';
COMMENT ON COLUMN books.pdf_url IS 'PDF 파일 경로(URL)';
COMMENT ON COLUMN books.image_url IS '대표 이미지 경로(URL)';
COMMENT ON COLUMN books.created_at IS '생성일시';
COMMENT ON COLUMN books.updated_at IS '수정일시';

-- =================================================================
-- 5. PortOne 결제 관련 테이블 (PortOne Payment Tables)
-- =================================================================

-- PortOne 결제 정보 테이블
CREATE TABLE payments (
    payment_id              VARCHAR2(50) PRIMARY KEY,
    order_id                VARCHAR2(50) NOT NULL,
    user_id                 NUMBER NOT NULL,
    family_id               NUMBER,
    portone_transaction_id  VARCHAR2(100),
    billing_key_id          VARCHAR2(50),
    amount                  NUMBER(10,2) NOT NULL,
    currency                VARCHAR2(3) DEFAULT 'KRW',
    payment_method          VARCHAR2(20),
    status                  VARCHAR2(20) DEFAULT 'PENDING',
    type                    VARCHAR2(20) NOT NULL,
    pg_provider             VARCHAR2(20),
    pg_transaction_id       VARCHAR2(100),
    approval_number         VARCHAR2(50),
    failure_code            VARCHAR2(20),
    failure_message         VARCHAR2(500),
    card_info               VARCHAR2(100),
    buyer_name              VARCHAR2(100),
    buyer_email             VARCHAR2(100),
    buyer_phone             VARCHAR2(20),
    product_name            VARCHAR2(200),
    receipt_url             VARCHAR2(500),
    request_ip              VARCHAR2(45),
    requested_at            TIMESTAMP,
    approved_at             TIMESTAMP,
    failed_at               TIMESTAMP,
    cancelled_at            TIMESTAMP,
    created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payments_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_payments_family FOREIGN KEY (family_id) REFERENCES families(id)
);

-- PortOne 빌링키 정보 테이블
CREATE TABLE billing_keys (
    billing_key_id          VARCHAR2(50) PRIMARY KEY,
    user_id                 NUMBER NOT NULL,
    family_id               NUMBER,
    customer_uid            VARCHAR2(100) UNIQUE NOT NULL,
    portone_key             VARCHAR2(100),
    pg_provider             VARCHAR2(20),
    card_name               VARCHAR2(50),
    card_number             VARCHAR2(20),
    card_type               VARCHAR2(20),
    payment_method_info     VARCHAR2(500),
    status                  VARCHAR2(20) DEFAULT 'INACTIVE',
    issued_at               TIMESTAMP,
    expired_at              TIMESTAMP,
    last_used_at            TIMESTAMP,
    created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_billing_keys_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_billing_keys_family FOREIGN KEY (family_id) REFERENCES families(id)
);

-- PortOne 테이블 코멘트
COMMENT ON TABLE payments IS 'PortOne 결제 정보를 관리하는 테이블';
COMMENT ON TABLE billing_keys IS 'PortOne 빌링키 정보를 관리하는 테이블 (정기결제용)';

-- payments 컬럼 코멘트
COMMENT ON COLUMN payments.payment_id IS '결제 고유 ID (UUID)';
COMMENT ON COLUMN payments.order_id IS '주문 ID (merchant_uid)';
COMMENT ON COLUMN payments.user_id IS '결제 사용자 ID';
COMMENT ON COLUMN payments.family_id IS '가족 그룹 ID';
COMMENT ON COLUMN payments.portone_transaction_id IS 'PortOne 거래 ID (imp_uid)';
COMMENT ON COLUMN payments.billing_key_id IS '빌링키 ID (정기결제인 경우)';
COMMENT ON COLUMN payments.amount IS '결제 금액';
COMMENT ON COLUMN payments.currency IS '통화 코드';
COMMENT ON COLUMN payments.payment_method IS '결제 수단 (CARD, KAKAOPAY 등)';
COMMENT ON COLUMN payments.status IS '결제 상태 (PENDING, APPROVED, FAILED, CANCELLED)';
COMMENT ON COLUMN payments.type IS '결제 타입 (ONETIME, SUBSCRIPTION)';
COMMENT ON COLUMN payments.pg_provider IS 'PG사 코드';
COMMENT ON COLUMN payments.pg_transaction_id IS 'PG사 거래 ID';
COMMENT ON COLUMN payments.approval_number IS '승인 번호';
COMMENT ON COLUMN payments.failure_code IS '실패 코드';
COMMENT ON COLUMN payments.failure_message IS '실패 메시지';
COMMENT ON COLUMN payments.card_info IS '카드 정보 (마스킹)';
COMMENT ON COLUMN payments.buyer_name IS '구매자 이름';
COMMENT ON COLUMN payments.buyer_email IS '구매자 이메일';
COMMENT ON COLUMN payments.buyer_phone IS '구매자 전화번호';
COMMENT ON COLUMN payments.product_name IS '상품명';
COMMENT ON COLUMN payments.receipt_url IS '영수증 URL';
COMMENT ON COLUMN payments.request_ip IS '요청 IP';
COMMENT ON COLUMN payments.requested_at IS '요청 일시';
COMMENT ON COLUMN payments.approved_at IS '승인 일시';
COMMENT ON COLUMN payments.failed_at IS '실패 일시';
COMMENT ON COLUMN payments.cancelled_at IS '취소 일시';

-- billing_keys 컬럼 코멘트
COMMENT ON COLUMN billing_keys.billing_key_id IS '빌링키 고유 ID (UUID)';
COMMENT ON COLUMN billing_keys.user_id IS '빌링키 소유자 ID';
COMMENT ON COLUMN billing_keys.family_id IS '가족 그룹 ID';
COMMENT ON COLUMN billing_keys.customer_uid IS '고객 고유 ID (PortOne에서 사용)';
COMMENT ON COLUMN billing_keys.portone_key IS 'PortOne 빌링키';
COMMENT ON COLUMN billing_keys.pg_provider IS 'PG사 코드';
COMMENT ON COLUMN billing_keys.card_name IS '카드사명';
COMMENT ON COLUMN billing_keys.card_number IS '마스킹된 카드번호';
COMMENT ON COLUMN billing_keys.card_type IS '카드 타입 (CREDIT, DEBIT)';
COMMENT ON COLUMN billing_keys.payment_method_info IS '결제 수단 정보 (JSON)';
COMMENT ON COLUMN billing_keys.status IS '빌링키 상태 (ACTIVE, INACTIVE, EXPIRED, DELETED)';
COMMENT ON COLUMN billing_keys.issued_at IS '발급 일시';
COMMENT ON COLUMN billing_keys.expired_at IS '만료 일시';
COMMENT ON COLUMN billing_keys.last_used_at IS '마지막 사용 일시';
