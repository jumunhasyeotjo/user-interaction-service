#!/bin/bash
# download_env_from_ssm.sh
# Parameter Store에서 환경변수를 다운로드하여 .env 파일로 저장
# ./script/download_env_from_ssm.sh user

# 색상 코드
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 사용법 출력
usage() {
    echo "Usage: ./download_env_from_ssm.sh <service_name> [output_file] [environment] [region]"
    echo ""
    echo "Examples:"
    echo "  ./download_env_from_ssm.sh hub" ##권장
    echo "  ./download_env_from_ssm.sh order .env-parameter-store"
    echo "  ./download_env_from_ssm.sh user .env.downloaded prod"
    echo "  ./download_env_from_ssm.sh gateway .env.gw dev us-east-1"
    exit 1
}

# 파라미터 설정
SERVICE_NAME=$1
OUTPUT_FILE=${2:-.env-parameter-store}
ENVIRONMENT=${3:-dev}
REGION=${4:-ap-northeast-2}
PROJECT="jumunhasyeo"
PREFIX="/${PROJECT}/${ENVIRONMENT}/${SERVICE_NAME}"

# 인자 체크
if [ -z "$SERVICE_NAME" ]; then
    usage
fi

# 시작 메시지
echo ""
echo -e "${BLUE}📥 Downloading parameters from AWS Parameter Store...${NC}"
echo -e "${GREEN}   Service: $SERVICE_NAME${NC}"
echo -e "${GREEN}   Environment: $ENVIRONMENT${NC}"
echo -e "${GREEN}   Region: $REGION${NC}"
echo -e "${GREEN}   Prefix: $PREFIX/${NC}"
echo -e "${GREEN}   Output: $OUTPUT_FILE${NC}"
echo ""

# 파일 존재 확인 및 백업
if [ -f "$OUTPUT_FILE" ]; then
    BACKUP_FILE="${OUTPUT_FILE}.backup.$(date +%Y%m%d-%H%M%S)"
    echo -e "${YELLOW}⚠️  $OUTPUT_FILE already exists!${NC}"
    echo -e "${YELLOW}   Creating backup: $BACKUP_FILE${NC}"
    cp "$OUTPUT_FILE" "$BACKUP_FILE"
    echo ""
fi

# Parameter Store에서 파라미터 가져오기
echo -e "${BLUE}🔍 Fetching parameters...${NC}"

PARAMS=$(aws ssm get-parameters-by-path \
    --path "$PREFIX/" \
    --recursive \
    --with-decryption \
    --region "$REGION" \
    --output json 2>&1)

# AWS CLI 에러 체크
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Error: Failed to fetch parameters from AWS${NC}"
    echo -e "${RED}   $PARAMS${NC}"
    echo ""
    echo -e "${YELLOW}💡 Common issues:${NC}"
    echo "   1. Check AWS credentials (aws configure)"
    echo "   2. Verify IAM permissions (ssm:GetParametersByPath)"
    echo "   3. Confirm the prefix exists: $PREFIX/"
    exit 1
fi

# 파라미터 개수 확인
PARAM_COUNT=$(echo "$PARAMS" | jq -r '.Parameters | length')

if [ "$PARAM_COUNT" -eq 0 ]; then
    echo -e "${YELLOW}⚠️  No parameters found at: $PREFIX/${NC}"
    echo ""
    echo -e "${BLUE}📋 Available prefixes:${NC}"
    aws ssm get-parameters-by-path \
        --path "/${PROJECT}/${ENVIRONMENT}/" \
        --recursive \
        --region "$REGION" \
        --query 'Parameters[].Name' \
        --output table 2>/dev/null || echo "   (Unable to list)"
    exit 1
fi

echo -e "${GREEN}✅ Found $PARAM_COUNT parameters${NC}"
echo ""

# .env 파일 생성 시작
echo "# Generated from AWS Parameter Store" > "$OUTPUT_FILE"
echo "# Service: $SERVICE_NAME" >> "$OUTPUT_FILE"
echo "# Environment: $ENVIRONMENT" >> "$OUTPUT_FILE"
echo "# Region: $REGION" >> "$OUTPUT_FILE"
echo "# Downloaded at: $(date '+%Y-%m-%d %H:%M:%S')" >> "$OUTPUT_FILE"
echo "# Prefix: $PREFIX/" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

# 파라미터 파싱 및 저장
echo "======================================================================"
echo -e "${BLUE}📝 Writing to $OUTPUT_FILE...${NC}"
echo "======================================================================"

SUCCESS=0
FAILED=0

# JSON 파싱하여 .env 형식으로 변환
echo "$PARAMS" | jq -r '.Parameters[] | "\(.Name)=\(.Value)"' | while IFS='=' read -r full_name value; do
    # Prefix 제거하여 키 이름만 추출
    # 예: /jumunhasyeo/dev/hub/POSTGRES_HOST → POSTGRES_HOST
    key="${full_name#$PREFIX/}"

    # 값에 공백이나 특수문자가 있으면 따옴표로 감싸기
    if [[ "$value" =~ [[:space:]] ]] || [[ "$value" =~ [\$\`\!\@\#\%\^\&\*\(\)] ]]; then
        value="\"$value\""
    fi

    # .env 파일에 추가
    echo "${key}=${value}" >> "$OUTPUT_FILE"

    # 화면 출력 (값 마스킹)
    # SECRET, PASSWORD, KEY, TOKEN 등이 포함된 키는 값 숨기기
    if [[ "$key" =~ (SECRET|PASSWORD|KEY|TOKEN|CREDENTIAL) ]]; then
        masked_value="***MASKED***"
        printf "${GREEN}✅ %-40s = %s${NC}\n" "$key" "$masked_value"
    else
        printf "${GREEN}✅ %-40s = %s${NC}\n" "$key" "$value"
    fi

    ((SUCCESS++))
done

echo "======================================================================"
echo ""
echo -e "${GREEN}🎉 Download Summary:${NC}"
echo -e "${GREEN}   ✅ Downloaded: $SUCCESS parameters${NC}"
echo -e "${GREEN}   📄 Output file: $OUTPUT_FILE${NC}"
echo ""

# 파일 내용 미리보기
echo -e "${BLUE}📋 File preview (first 10 lines):${NC}"
echo "----------------------------------------------------------------------"
head -n 15 "$OUTPUT_FILE" | while IFS= read -r line; do
    # 민감한 정보 마스킹
    if [[ "$line" =~ ^(.*SECRET.*|.*PASSWORD.*|.*KEY.*|.*TOKEN.*|.*CREDENTIAL.*)= ]]; then
        key="${line%%=*}"
        echo "${key}=***MASKED***"
    else
        echo "$line"
    fi
done
echo "----------------------------------------------------------------------"
echo ""

echo -e "${BLUE}💡 Usage:${NC}"
echo "   # Load into current shell"
echo "   export \$(cat $OUTPUT_FILE | grep -v '^#' | xargs)"
echo ""
echo "   # Use with docker-compose"
echo "   docker-compose --env-file $OUTPUT_FILE up"
echo ""
echo "   # Copy to .env"
echo "   cp $OUTPUT_FILE .env"
echo ""

# 검증 제안
echo -e "${YELLOW}🔍 Verify parameters:${NC}"
echo "   cat $OUTPUT_FILE | grep -v '^#' | wc -l  # Count variables"
echo "   diff .env $OUTPUT_FILE                    # Compare with existing .env"
echo ""