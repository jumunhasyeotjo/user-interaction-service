#!/bin/bash
# upload_env_to_ssm.sh
# ./script/upload_env_to_ssm.sh hub .env-ops

# 색상 코드
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 사용법 출력
usage() {
    echo "Usage: ./upload_env_to_ssm.sh <service_name> [env_file] [environment] [region]"
    echo ""
    echo "Examples:"
    echo "  ./upload_env_to_ssm.sh hub"
    echo "  ./upload_env_to_ssm.sh order .env.order"
    echo "  ./upload_env_to_ssm.sh user .env prod"
    echo "  ./upload_env_to_ssm.sh gateway .env dev us-east-1"
    exit 1
}

# 파라미터 설정
SERVICE_NAME=$1
ENV_FILE=${2:-.env}
ENVIRONMENT=${3:-dev}
REGION=${4:-ap-northeast-2}
PROJECT="jumunhasyeo"
PREFIX="/${PROJECT}/${ENVIRONMENT}/${SERVICE_NAME}"

# 인자 체크
if [ -z "$SERVICE_NAME" ]; then
    usage
fi

# 파일 존재 확인
if [ ! -f "$ENV_FILE" ]; then
    echo -e "${RED}❌ Error: $ENV_FILE not found!${NC}"
    exit 1
fi

# 환경변수 파싱 및 명령어 미리보기
echo ""
echo -e "${BLUE}📄 Reading $ENV_FILE...${NC}"
echo -e "${GREEN}   Service: $SERVICE_NAME${NC}"
echo -e "${GREEN}   Environment: $ENVIRONMENT${NC}"
echo -e "${GREEN}   Region: $REGION${NC}"

# 파라미터 개수 세기
PARAM_COUNT=0
while IFS='=' read -r key value || [ -n "$key" ]; do
    if [[ -z "$key" || "$key" =~ ^[[:space:]]*# ]]; then
        continue
    fi
    ((PARAM_COUNT++))
done < "$ENV_FILE"

echo -e "${GREEN}✅ Found $PARAM_COUNT parameters${NC}"

# 실행될 명령어 미리보기
echo ""
echo "================================================================================"
echo -e "${YELLOW}📋 Following AWS CLI commands will be executed:${NC}"
echo "================================================================================"

while IFS='=' read -r key value || [ -n "$key" ]; do
    # 빈 줄과 주석 제외
    if [[ -z "$key" || "$key" =~ ^[[:space:]]*# ]]; then
        continue
    fi

    # 공백 제거
    key=$(echo "$key" | xargs)
    value=$(echo "$value" | xargs)

    # 따옴표 제거
    value="${value%\"}"
    value="${value#\"}"
    value="${value%\'}"
    value="${value#\'}"

    PARAM_NAME="${PREFIX}/${key}"

    # AWS CLI 명령어 출력 (값 필터링 없이)
    echo ""
    echo "aws ssm put-parameter --name \"${PARAM_NAME}\" --value \"${value}\" --type String --overwrite --region ${REGION}"

done < "$ENV_FILE"

echo ""
echo "================================================================================"

# 사용자 확인
echo ""
echo -e "${YELLOW}⚠️  Do you want to proceed with the upload?${NC}"
echo -e "${YELLOW}   Type 'yes' to continue, anything else to cancel.${NC}"
echo ""
read -p "👉 Your answer: " response

# 응답 확인
if [[ "$response" != "yes" ]]; then
    echo ""
    echo -e "${RED}❌ Upload cancelled by user.${NC}"
    exit 0
fi

# 업로드 시작
echo ""
echo -e "${GREEN}✅ Proceeding with upload...${NC}"
echo ""
echo -e "${BLUE}📤 Uploading $PARAM_COUNT parameters to Parameter Store${NC}"
echo -e "${BLUE}   Prefix: ${PREFIX}/${NC}"
echo "======================================================================"

SUCCESS=0
FAILED=0

# 실제 업로드
while IFS='=' read -r key value || [ -n "$key" ]; do
    # 빈 줄과 주석 제외
    if [[ -z "$key" || "$key" =~ ^[[:space:]]*# ]]; then
        continue
    fi

    # 공백 제거
    key=$(echo "$key" | xargs)
    value=$(echo "$value" | xargs)

    # 따옴표 제거
    value="${value%\"}"
    value="${value#\"}"
    value="${value%\'}"
    value="${value#\'}"

    PARAM_NAME="${PREFIX}/${key}"

    # Parameter Store에 업로드
    if aws ssm put-parameter \
        --name "$PARAM_NAME" \
        --value "$value" \
        --type String \
        --overwrite \
        --region $REGION \
        --description "Environment variable for ${SERVICE_NAME} service" \
        --no-cli-pager \
        > /dev/null 2>&1; then
        printf "${GREEN}✅ %-40s → %s${NC}\n" "$key" "$PARAM_NAME"
        ((SUCCESS++))
    else
        printf "${RED}❌ %-40s → Failed${NC}\n" "$key"
        ((FAILED++))
    fi
done < "$ENV_FILE"

echo "======================================================================"
echo ""
echo -e "${GREEN}🎉 Upload Summary:${NC}"
echo -e "${GREEN}   ✅ Success: $SUCCESS${NC}"
if [ $FAILED -gt 0 ]; then
    echo -e "${RED}   ❌ Failed:  $FAILED${NC}"
fi
echo ""
echo -e "${BLUE}📋 Verify with:${NC}"
echo "   aws ssm get-parameters-by-path --path $PREFIX/ --recursive --region $REGION"
echo ""
echo -e "${BLUE}🌐 View in AWS Console:${NC}"
echo "   https://${REGION}.console.aws.amazon.com/systems-manager/parameters/?region=${REGION}&tab=Table#list_parameter_filters=Path:Recursive:${PREFIX}"