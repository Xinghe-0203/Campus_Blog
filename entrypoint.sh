#!/bin/sh
# ============================================
# 校园博客论坛系统 - 应用启动脚本
# 用途：环境变量验证、数据库连接检查、应用启动
# ============================================

# ============================================
# 颜色定义（用于输出美化）
# ============================================
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# ============================================
# 日志函数
# ============================================
log_info() {
    echo "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo "${RED}[ERROR]${NC} $1"
}

# ============================================
# 环境变量验证
# ============================================
validate_env() {
    log_info "验证环境变量..."

    # 必需的环境变量
    REQUIRED_VARS="DB_HOST DB_PORT DB_NAME DB_USERNAME DB_PASSWORD JWT_SECRET"

    for var in $REQUIRED_VARS; do
        value=$(eval echo \$$var)
        if [ -z "$value" ] || [ "$value" = "your_${var,,}_here" ] || [ "$value" = "your_password_here" ]; then
            log_error "环境变量 $var 未设置或为默认值"
            log_error "请在 .env 文件或 Docker 环境变量中配置"
            return 1
        fi
    done

    # JWT_SECRET 长度验证（至少 32 位）
    if [ ${#JWT_SECRET} -lt 32 ]; then
        log_error "JWT_SECRET 长度必须至少为 32 位"
        return 1
    fi

    log_info "环境变量验证通过"
    return 0
}

# ============================================
# 数据库连接检查
# ============================================
wait_for_db() {
    log_info "等待数据库连接..."

    # 最多等待 120 秒
    MAX_WAIT=120
    COUNTER=0

    while [ $COUNTER -lt $MAX_WAIT ]; ]; do
        # 尝试连接数据库
        result=$(mysqladmin ping -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USERNAME" -p"$DB_PASSWORD" --silent 2>&1)

        if [ $? -eq 0 ]; then
            log_info "数据库连接成功"
            return 0
        fi

        COUNTER=$((COUNTER + 5))
        log_warn "等待数据库就绪... ($COUNTER/$MAX_WAIT 秒)"
        sleep 5
    done

    log_error "数据库连接超时（${MAX_WAIT}秒）"
    return 1
}

# ============================================
# 应用启动
# ============================================
start_app() {
    log_info "启动校园博客论坛系统..."

    # 设置 JVM 参数
    JAVA_OPTS="${JAVA_OPTS:- -Xms512m -Xmx1024m -XX:+UseG1GC}"

    # 打印配置信息（不显示敏感数据）
    echo ""
    echo "=========================================="
    echo "  Campus Blog Forum System"
    echo "  Version: v1.30"
    echo "=========================================="
    echo "  Server Port: ${SERVER_PORT:-8825}"
    echo "  Database:   ${DB_NAME}@${DB_HOST}:${DB_PORT}"
    echo "  API Docs:   http://localhost:${SERVER_PORT:-8825}/api/doc.html"
    echo "=========================================="
    echo ""

    # 启动应用
    exec java $JAVA_OPTS -jar app.jar
}

# ============================================
# 主流程
# ============================================
main() {
    # 验证环境变量
    if ! validate_env; then
        log_error "环境变量验证失败，退出启动"
        exit 1
    fi

    # 等待数据库就绪（仅在检测到数据库时）
    if [ "$DB_HOST" != "localhost" ] && [ "$DB_HOST" != "127.0.0.1" ]; then
        wait_for_db || log_warn "数据库检查失败，继续启动（可能在容器内部检查）"
    fi

    # 启动应用
    start_app
}

# 执行主流程
main