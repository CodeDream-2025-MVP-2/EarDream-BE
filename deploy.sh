#!/bin/bash

# EarDream AWS EC2 배포 스크립트
# 사용법: ./deploy.sh [build|deploy|restart|logs]

# .env 파일 로드
if [ -f ".env" ]; then
    export $(cat .env | grep -v '^#' | xargs)
fi

# AWS EC2 설정
EC2_HOST="${EC2_HOST}"
EC2_USER="${EC2_USER}"
PEM_KEY="${PEM_KEY}"
APP_NAME="eardream-backend"
APP_PORT="8080"

# 색상 코드
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 로그 함수
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# PEM 키 확인
check_pem_key() {
    if [ ! -f "$PEM_KEY" ]; then
        log_error "PEM 키 파일을 찾을 수 없습니다: $PEM_KEY"
        exit 1
    fi
    
    # PEM 키 권한 확인 및 설정
    if [ "$(stat -f %A $PEM_KEY 2>/dev/null || stat -c %a $PEM_KEY 2>/dev/null)" != "400" ]; then
        log_warning "PEM 키 권한을 400으로 설정합니다..."
        chmod 400 "$PEM_KEY"
    fi
}

# SSH 연결 테스트
test_connection() {
    log_info "AWS EC2 연결 테스트 중..."
    if ssh -i "$PEM_KEY" -o ConnectTimeout=10 -o StrictHostKeyChecking=no "$EC2_USER@$EC2_HOST" "echo 'Connection successful'"; then
        log_success "AWS EC2 연결 성공"
    else
        log_error "AWS EC2 연결 실패"
        exit 1
    fi
}

# Spring Boot 애플리케이션 빌드
build_app() {
    log_info "Spring Boot 애플리케이션 빌드 중..."
    
    # Gradle 빌드
    if ./gradlew clean bootJar; then
        log_success "빌드 완료: build/libs/${APP_NAME}.jar"
    else
        log_error "빌드 실패"
        exit 1
    fi
}

# AWS EC2에 애플리케이션 배포
deploy_app() {
    log_info "AWS EC2에 애플리케이션 배포 중..."
    
    # 빌드된 JAR 파일 확인
    JAR_FILE="build/libs/${APP_NAME}.jar"
    if [ ! -f "$JAR_FILE" ]; then
        log_error "빌드된 JAR 파일을 찾을 수 없습니다: $JAR_FILE"
        log_info "먼저 빌드를 실행합니다..."
        build_app
    fi
    
    # 서버에 애플리케이션 디렉토리 생성
    ssh -i "$PEM_KEY" "$EC2_USER@$EC2_HOST" "mkdir -p /home/ubuntu/eardream"
    
    # JAR 파일 업로드
    log_info "JAR 파일 업로드 중..."
    if scp -i "$PEM_KEY" "$JAR_FILE" "$EC2_USER@$EC2_HOST:/home/ubuntu/eardream/"; then
        log_success "JAR 파일 업로드 완료"
    else
        log_error "JAR 파일 업로드 실패"
        exit 1
    fi
    
    # 시작 스크립트 생성
    log_info "시작 스크립트 생성 중..."
    ssh -i "$PEM_KEY" "$EC2_USER@$EC2_HOST" "cat > /home/ubuntu/eardream/start.sh << 'EOF'
#!/bin/bash
cd /home/ubuntu/eardream
export JAVA_OPTS=\"-Xms512m -Xmx1024m -Dserver.port=$APP_PORT\"
nohup java \$JAVA_OPTS -jar $APP_NAME.jar > app.log 2>&1 &
echo \$! > app.pid
echo \"EarDream 애플리케이션이 시작되었습니다. PID: \$(cat app.pid)\"
EOF"
    
    # 중지 스크립트 생성
    ssh -i "$PEM_KEY" "$EC2_USER@$EC2_HOST" "cat > /home/ubuntu/eardream/stop.sh << 'EOF'
#!/bin/bash
cd /home/ubuntu/eardream
if [ -f app.pid ]; then
    PID=\$(cat app.pid)
    if kill \$PID 2>/dev/null; then
        echo \"EarDream 애플리케이션이 중지되었습니다. PID: \$PID\"
        rm app.pid
    else
        echo \"프로세스를 찾을 수 없습니다: \$PID\"
    fi
else
    echo \"PID 파일을 찾을 수 없습니다.\"
fi
EOF"
    
    # 스크립트 실행 권한 부여
    ssh -i "$PEM_KEY" "$EC2_USER@$EC2_HOST" "chmod +x /home/ubuntu/eardream/*.sh"
    
    log_success "배포 완료"
}

# 애플리케이션 재시작
restart_app() {
    log_info "애플리케이션 재시작 중..."
    
    # 기존 프로세스 중지
    ssh -i "$PEM_KEY" "$EC2_USER@$EC2_HOST" "/home/ubuntu/eardream/stop.sh"
    sleep 3
    
    # 새 프로세스 시작
    ssh -i "$PEM_KEY" "$EC2_USER@$EC2_HOST" "/home/ubuntu/eardream/start.sh"
    
    # 포트 확인
    sleep 5
    if ssh -i "$PEM_KEY" "$EC2_USER@$EC2_HOST" "ss -tlnp | grep :$APP_PORT"; then
        log_success "애플리케이션이 포트 $APP_PORT에서 실행 중입니다"
        log_info "접속 URL: http://$EC2_HOST:$APP_PORT"
    else
        log_warning "포트 $APP_PORT에서 실행 중인 프로세스를 찾을 수 없습니다"
    fi
}

# 로그 확인
show_logs() {
    log_info "애플리케이션 로그 확인 중..."
    ssh -i "$PEM_KEY" "$EC2_USER@$EC2_HOST" "tail -f /home/ubuntu/eardream/app.log"
}

# 상태 확인
check_status() {
    log_info "애플리케이션 상태 확인 중..."
    ssh -i "$PEM_KEY" "$EC2_USER@$EC2_HOST" "
        cd /home/ubuntu/eardream
        if [ -f app.pid ]; then
            PID=\$(cat app.pid)
            if ps -p \$PID > /dev/null; then
                echo '✅ 애플리케이션이 실행 중입니다. PID: '\$PID
                ss -tlnp | grep :$APP_PORT || echo '⚠️  포트 $APP_PORT에서 수신 대기하지 않습니다'
            else
                echo '❌ PID 파일은 있지만 프로세스가 실행되지 않습니다'
            fi
        else
            echo '❌ 애플리케이션이 실행되지 않습니다'
        fi
    "
}

# 메인 실행 로직
main() {
    case "$1" in
        "build")
            check_pem_key
            build_app
            ;;
        "deploy")
            check_pem_key
            test_connection
            deploy_app
            ;;
        "restart")
            check_pem_key
            test_connection
            restart_app
            ;;
        "logs")
            check_pem_key
            test_connection
            show_logs
            ;;
        "status")
            check_pem_key
            test_connection
            check_status
            ;;
        "full")
            check_pem_key
            test_connection
            build_app
            deploy_app
            restart_app
            ;;
        *)
            echo "사용법: $0 [build|deploy|restart|logs|status|full]"
            echo ""
            echo "명령어:"
            echo "  build   - Spring Boot 애플리케이션 빌드"
            echo "  deploy  - AWS EC2에 애플리케이션 배포"
            echo "  restart - 애플리케이션 재시작"
            echo "  logs    - 애플리케이션 로그 확인"
            echo "  status  - 애플리케이션 상태 확인"
            echo "  full    - 빌드 + 배포 + 재시작 (전체 배포)"
            echo ""
            echo "예시:"
            echo "  $0 full      # 전체 배포 프로세스"
            echo "  $0 build     # 빌드만 실행"
            echo "  $0 restart   # 재시작만 실행"
            exit 1
            ;;
    esac
}

# 스크립트 실행
main "$@"