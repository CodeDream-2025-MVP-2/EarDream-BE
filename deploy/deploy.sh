#!/bin/bash

# EarDream MVP 배포 스크립트
# Usage: ./deploy.sh [OPTIONS]
# Options:
#   --env ENV_NAME    Environment (dev/prod, default: prod)
#   --health-check    Enable health check after deployment
#   --rollback        Rollback to previous version

set -e

# 설정 변수
APP_NAME="eardream"
APP_DIR="/opt/eardream"
JAR_NAME="eardream.jar"
SERVICE_NAME="eardream"
LOG_DIR="/var/log/eardream"
PID_FILE="/var/run/eardream.pid"

# 기본 설정
ENVIRONMENT="prod"
HEALTH_CHECK=true
ROLLBACK=false

# 컬러 출력 함수
print_info() {
    echo -e "\033[36m[INFO]\033[0m $1"
}

print_success() {
    echo -e "\033[32m[SUCCESS]\033[0m $1"
}

print_error() {
    echo -e "\033[31m[ERROR]\033[0m $1"
}

print_warning() {
    echo -e "\033[33m[WARNING]\033[0m $1"
}

# 도움말 출력
show_help() {
    echo "EarDream MVP 배포 스크립트"
    echo ""
    echo "사용법: $0 [OPTIONS]"
    echo ""
    echo "옵션:"
    echo "  --env ENV_NAME      환경 설정 (dev/prod, 기본값: prod)"
    echo "  --health-check      배포 후 헬스체크 실행"
    echo "  --rollback          이전 버전으로 롤백"
    echo "  --help             이 도움말 출력"
    echo ""
}

# 명령행 인자 처리
while [[ $# -gt 0 ]]; do
    case $1 in
        --env)
            ENVIRONMENT="$2"
            shift 2
            ;;
        --health-check)
            HEALTH_CHECK=true
            shift
            ;;
        --rollback)
            ROLLBACK=true
            shift
            ;;
        --help)
            show_help
            exit 0
            ;;
        *)
            print_error "알 수 없는 옵션: $1"
            show_help
            exit 1
            ;;
    esac
done

# 필수 디렉토리 생성
setup_directories() {
    print_info "필수 디렉토리 설정 중..."
    
    sudo mkdir -p $APP_DIR
    sudo mkdir -p $LOG_DIR
    sudo mkdir -p $APP_DIR/backup
    
    # 권한 설정
    sudo chown -R $USER:$USER $APP_DIR
    sudo chown -R $USER:$USER $LOG_DIR
    
    print_success "디렉토리 설정 완료"
}

# 백업 생성
create_backup() {
    if [ -f "$APP_DIR/$JAR_NAME" ]; then
        print_info "현재 버전 백업 중..."
        BACKUP_NAME="$JAR_NAME.backup.$(date +%Y%m%d_%H%M%S)"
        cp "$APP_DIR/$JAR_NAME" "$APP_DIR/backup/$BACKUP_NAME"
        print_success "백업 완료: $BACKUP_NAME"
        
        # 오래된 백업 파일 정리 (최근 5개만 유지)
        cd "$APP_DIR/backup"
        ls -t $JAR_NAME.backup.* | tail -n +6 | xargs -r rm --
        print_info "오래된 백업 파일 정리 완료"
    fi
}

# 애플리케이션 중지
stop_application() {
    print_info "애플리케이션 중지 중..."
    
    if systemctl is-active --quiet $SERVICE_NAME; then
        sudo systemctl stop $SERVICE_NAME
        print_success "서비스 중지 완료"
    else
        print_warning "서비스가 실행되고 있지 않습니다"
    fi
    
    # PID 파일이 있으면 프로세스 강제 종료
    if [ -f "$PID_FILE" ]; then
        PID=$(cat $PID_FILE)
        if ps -p $PID > /dev/null; then
            print_warning "프로세스를 강제 종료합니다 (PID: $PID)"
            kill -9 $PID
        fi
        rm -f $PID_FILE
    fi
}

# 애플리케이션 시작
start_application() {
    print_info "애플리케이션 시작 중..."
    
    # JAR 파일 권한 설정
    chmod +x "$APP_DIR/$JAR_NAME"
    
    # 서비스 시작
    sudo systemctl start $SERVICE_NAME
    sudo systemctl enable $SERVICE_NAME
    
    print_success "서비스 시작 완료"
    
    # 시작 대기
    print_info "애플리케이션 초기화 대기 중..."
    sleep 30
}

# 헬스체크
health_check() {
    if [ "$HEALTH_CHECK" = true ]; then
        print_info "헬스체크 실행 중..."
        
        MAX_ATTEMPTS=10
        ATTEMPT=1
        
        while [ $ATTEMPT -le $MAX_ATTEMPTS ]; do
            if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
                print_success "✅ 헬스체크 통과 - 애플리케이션이 정상 작동 중입니다"
                return 0
            fi
            
            print_info "헬스체크 재시도 ($ATTEMPT/$MAX_ATTEMPTS)..."
            sleep 10
            ((ATTEMPT++))
        done
        
        print_error "❌ 헬스체크 실패 - 애플리케이션이 응답하지 않습니다"
        return 1
    fi
}

# 롤백 실행
rollback() {
    print_warning "이전 버전으로 롤백을 실행합니다..."
    
    # 가장 최근 백업 파일 찾기
    LATEST_BACKUP=$(ls -t "$APP_DIR/backup/$JAR_NAME.backup."* 2>/dev/null | head -n 1)
    
    if [ -z "$LATEST_BACKUP" ]; then
        print_error "롤백할 백업 파일을 찾을 수 없습니다"
        exit 1
    fi
    
    print_info "백업 파일 복원 중: $(basename $LATEST_BACKUP)"
    
    stop_application
    cp "$LATEST_BACKUP" "$APP_DIR/$JAR_NAME"
    start_application
    
    if health_check; then
        print_success "🔄 롤백 완료 - 이전 버전으로 복원되었습니다"
    else
        print_error "❌ 롤백 후 헬스체크 실패"
        exit 1
    fi
}

# 메인 배포 함수
deploy() {
    print_info "=== EarDream MVP 배포 시작 (환경: $ENVIRONMENT) ==="
    
    setup_directories
    create_backup
    stop_application
    
    # 새 JAR 파일이 있는지 확인
    if [ ! -f "/tmp/$JAR_NAME" ]; then
        print_error "배포할 JAR 파일을 찾을 수 없습니다: /tmp/$JAR_NAME"
        exit 1
    fi
    
    # JAR 파일 이동
    print_info "새 버전 배포 중..."
    mv "/tmp/$JAR_NAME" "$APP_DIR/$JAR_NAME"
    
    start_application
    
    if health_check; then
        print_success "🚀 배포 완료 - 애플리케이션이 성공적으로 배포되었습니다"
    else
        print_error "❌ 배포 실패 - 헬스체크 실패로 롤백을 시작합니다"
        rollback
        exit 1
    fi
    
    print_info "=== 배포 완료 ==="
}

# 메인 실행
main() {
    # 루트 권한 확인
    if [ "$EUID" -eq 0 ]; then
        print_error "이 스크립트는 root 권한으로 실행하지 마세요"
        exit 1
    fi
    
    # sudo 권한 확인
    if ! sudo -n true 2>/dev/null; then
        print_error "sudo 권한이 필요합니다"
        exit 1
    fi
    
    if [ "$ROLLBACK" = true ]; then
        rollback
    else
        deploy
    fi
}

# 스크립트 실행
main