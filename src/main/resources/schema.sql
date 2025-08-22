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

-- 결제 내역 테이블 (이니시스 PG)
CREATE TABLE payment_histories (
                                  id                    NUMBER GENERATED AS IDENTITY PRIMARY KEY,
                                  subscription_id       NUMBER NOT NULL,
                                  amount                NUMBER(10,0) NOT NULL,
                                  currency              VARCHAR2(3) DEFAULT 'KRW',
                                  payment_type          VARCHAR2(20) NOT NULL CHECK (payment_type IN ('SUBSCRIPTION', 'REFUND', 'PENALTY')),
                                  status                VARCHAR2(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'CANCELLED')),
                                  inicis_tid            VARCHAR2(100),
                                  inicis_mid            VARCHAR2(100),
                                  inicis_authcode       VARCHAR2(100),
                                  payment_method        VARCHAR2(20),
                                  card_number           VARCHAR2(20),
                                  approved_at           TIMESTAMP,
                                  failed_reason         VARCHAR2(500),
                                  created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  CONSTRAINT fk_payment_histories_subscription FOREIGN KEY (subscription_id) REFERENCES subscriptions(id)
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
COMMENT ON TABLE payment_histories IS '이니시스 구독 결제 내역 및 상태를 관리하는 테이블';

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
COMMENT ON COLUMN subscriptions.plan_name IS '구독 플랜명 (MONTHLY)';
COMMENT ON COLUMN subscriptions.plan_price IS '구독 월 요금 (원, 정수)';
COMMENT ON COLUMN subscriptions.status IS '구독 상태 (ACTIVE, CANCELLED, PAUSED)';
COMMENT ON COLUMN subscriptions.billing_cycle IS '결제 주기 (MONTHLY, YEARLY)';
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

-- payment_histories
COMMENT ON COLUMN payment_histories.id IS '결제 내역 내부 고유 ID (대체키, 자동증가)';
COMMENT ON COLUMN payment_histories.subscription_id IS '결제 대상 구독 ID';
COMMENT ON COLUMN payment_histories.amount IS '결제 금액 (원, 정수)';
COMMENT ON COLUMN payment_histories.currency IS '결제 통화 (KRW, USD 등)';
COMMENT ON COLUMN payment_histories.payment_type IS '결제 유형 (SUBSCRIPTION: 구독료, REFUND: 환불, PENALTY: 연체료)';
COMMENT ON COLUMN payment_histories.status IS '결제 상태 (PENDING: 대기, SUCCESS: 성공, FAILED: 실패, CANCELLED: 취소)';
COMMENT ON COLUMN payment_histories.inicis_tid IS '이니시스 거래 고유번호';
COMMENT ON COLUMN payment_histories.inicis_mid IS '이니시스 상점 ID';
COMMENT ON COLUMN payment_histories.inicis_authcode IS '이니시스 승인번호';
COMMENT ON COLUMN payment_histories.payment_method IS '결제 수단 (CARD, BANK 등)';
COMMENT ON COLUMN payment_histories.card_number IS '카드번호 마스킹 처리';
COMMENT ON COLUMN payment_histories.approved_at IS '결제 승인일시';
COMMENT ON COLUMN payment_histories.failed_reason IS '결제 실패 사유';
COMMENT ON COLUMN payment_histories.created_at IS '결제 요청일시';
