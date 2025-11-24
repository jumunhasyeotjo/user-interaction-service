# DEPLOYMENT_REPORT.md

## 네트워크 구성 확인

### VPC Resource Map
![vpc](./img/vpc.png)

### Security Groups 설정
![sg](./img/sg.png)

### NAT Gateway Status "Available"
![nat](./img/nat.png)

## 데이터베이스 구성 확인

### RDS 인스턴스 상세
![rds](./img/rds.png)

### Bastion에서 MySQL 접속 성공
![bastion](./img/query.png)

## 컨테이너 배포 확인
### ECR 이미지 목록
![ecr](./img/image.png)

### ECS Services 목록
![ecs](./img/ecs.png)

### CloudWatch Logs
![cloudwatch](./img/cloudwatch.png)

## 동작 검증
### API 호출 결과 (주문 생성 성공)
![swagger](./img/swagger.png)
### 이벤트 전파 확인
못했습니다 ㅠ
### MySQL 데이터 확인
![data](./img/data.png)
### 전체 아키텍처 체크리스트 완성본
![chk](./img/check.png)