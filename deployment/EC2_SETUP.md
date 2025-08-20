# EC2 서버 설정 가이드

## 1. Oracle Wallet 설정

EC2 서버에 Oracle Wallet 파일을 업로드하고 설정합니다.

```bash
# 1. Oracle wallet 디렉토리 생성
sudo mkdir -p /opt/oracle/wallet
sudo chown ubuntu:ubuntu /opt/oracle/wallet

# 2. 로컬에서 EC2로 wallet 파일 업로드
# 로컬 터미널에서 실행:
scp -i your-key.pem src/main/resources/wallet/* ubuntu@ec2-host:/opt/oracle/wallet/

# 3. 권한 설정
sudo chmod 600 /opt/oracle/wallet/*
```

## 2. Java 21 설치

```bash
# Java 21 설치
sudo apt update
sudo apt install openjdk-21-jdk -y

# 확인
java -version
```

## 3. systemd 서비스 설정

```bash
# 1. 서비스 파일 복사
sudo cp deployment/eardream.service /etc/systemd/system/

# 2. 서비스 리로드
sudo systemctl daemon-reload

# 3. 서비스 활성화
sudo systemctl enable eardream
```

## 4. 애플리케이션 디렉토리 생성

```bash
# 애플리케이션 디렉토리
sudo mkdir -p /opt/eardream
sudo chown ubuntu:ubuntu /opt/eardream

# 업로드 디렉토리
sudo mkdir -p /opt/eardream/uploads
sudo chown ubuntu:ubuntu /opt/eardream/uploads
```

## 5. 환경 변수 설정

GitHub Actions가 자동으로 `/etc/environment`에 환경 변수를 설정합니다.
수동 설정이 필요한 경우:

```bash
sudo tee /etc/environment >> /dev/null <<EOF
ORACLE_TNS_ADMIN=/opt/oracle/wallet
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
JWT_SECRET_KEY=your_jwt_secret
KAKAO_CLIENT_ID=your_kakao_client_id
KAKAO_CLIENT_SECRET=your_kakao_client_secret
KAKAO_REDIRECT_URI=your_redirect_uri
SPRING_PROFILES_ACTIVE=prod
EOF
```

## 6. 서비스 시작

```bash
# 서비스 시작
sudo systemctl start eardream

# 상태 확인
sudo systemctl status eardream

# 로그 확인
sudo journalctl -u eardream -f
```

## 7. 헬스체크

```bash
# Health endpoint 확인
curl http://localhost:8080/actuator/health
```

## 8. GitHub Secrets 설정

GitHub 저장소 Settings → Secrets에 다음 값들을 추가:

- `EC2_HOST`: EC2 퍼블릭 IP 또는 도메인
- `EC2_USERNAME`: ubuntu (기본값)
- `EC2_PRIVATE_KEY`: EC2 접속용 Private Key (전체 내용)
- `EC2_PORT`: 22 (SSH 포트)
- `DB_USERNAME`: Oracle DB 사용자명
- `DB_PASSWORD`: Oracle DB 비밀번호
- `JWT_SECRET_KEY`: JWT 서명용 키 (최소 256비트)
- `KAKAO_CLIENT_ID`: 카카오 앱 ID
- `KAKAO_CLIENT_SECRET`: 카카오 앱 시크릿
- `KAKAO_REDIRECT_URI`: 카카오 OAuth 리다이렉트 URI