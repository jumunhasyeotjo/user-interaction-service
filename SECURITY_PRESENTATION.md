## 1. 아키텍처 개요 (2분)

### 1.1 전체 구성도 요약

우리의 시스템은 **AWS 클라우드 기반의 마이크로서비스 아키텍처(MSA)**로 설계되었습니다. 고가용성과 보안을 위해 VPC 내 Public/Private Subnet을 분리하고, Multi-AZ(가용 영역) 환경을 구축하였습니다.

- **진입점**: Route 53과 Internet Gateway를 통해 트래픽이 유입되며, 모든 요청은 ACM이 적용된 ALB(Application Load Balancer)를 거칩니다.
- **컴퓨팅**: ECS Fargate를 사용하여 서버리스 컨테이너 환경에서 Spring Boot 애플리케이션들이 구동됩니다. (Spring Gateway, Eureka, Zipkin 및 비즈니스 서비스들)
- **메시징 & 데이터**: 서비스 간 비동기 통신을 위해 AWS MSK(Kafka)를 사용하며, 데이터 계층은 RDS(관계형 DB)와 ElastiCache(Redis)로 구성됩니다.
- **운영 파이프라인**: GitHub Actions와 ECR을 통해 CI/CD가 자동화되어 있으며, CloudWatch와 AWS Chatbot을 통해 실시간 모니터링 중입니다.

> 🗣️ 발표 포인트: "이 아키텍처의 핵심은 외부 접근을 철저히 통제하면서도, 내부 서비스 간에는 Kafka와 Eureka를 통해 유연한 연결을 보장한다는 점입니다."
> 

---

## 2. 네트워크 보안 (3분)

### 2.1 망 분리 (Public vs Private Subnet)

- **DMZ (Public Subnet)**: 외부와 직접 통신이 필요한 리소스(ALB, Bastion Host, NAT Gateway)만 배치했습니다.
- **App/Data Layer (Private Subnet)**: 실제 비즈니스 로직이 도는 컨테이너(ECS)와 데이터베이스(RDS, ElastiCache)는 외부 인터넷에서 직접 접근이 불가능한 Private Subnet에 격리되어 있습니다.
- **Outbound 통제**: Private Subnet의 인스턴스가 업데이트 등을 위해 외부로 나갈 때는 NAT Gateway를 통해서만 통신합니다.

### 2.2 심층 방어 (Defense in Depth)

- **WAF (Web Application Firewall)**: ALB 앞단에 WAF를 배치하여 SQL Injection, XSS 등 일반적인 웹 공격을 1차적으로 차단합니다.
- **Security Group (SG)**: 다이어그램의 화살표 흐름처럼, 각 계층은 필요한 포트만 허용합니다.
    - ALB → API Gateway (8080)
    - API Gateway → Microservices
    - Microservices → RDS/Redis (3306, 6379)

> 🗣️ 발표 포인트: "단순히 방화벽 하나에 의존하는 것이 아니라, WAF → VPC 격리 → Security Group으로 이어지는 3단계 방어 체계를 구축했습니다."
> 

---

## 3. 데이터 보안 (2분)

### 3.1 암호화 (Encryption)

- **전송 중 암호화 (In-Transit)**:
    - 클라이언트 ↔ ALB 구간은 **ACM(AWS Certificate Manager)**을 통해 HTTPS(TLS)로 암호화됩니다.
    - 내부 서비스 간 통신 및 DB 연결 시 보안 프로토콜을 준수합니다.
- **저장 데이터 암호화 (At-Rest)**:
    - RDS 및 ElastiCache는 AWS KMS를 활용하여 디스크 레벨에서 데이터가 암호화되어 저장됩니다.

### 3.2 기밀 정보 관리

- **Secrets Manager**: DB 패스워드, API Key 등 민감한 정보는 소스코드(GitHub)에 절대 포함되지 않으며, **AWS Secrets Manager**를 통해 애플리케이션 구동 시점에 안전하게 주입받습니다.

> 🗣️ 발표 포인트: "데이터는 이동 중일 때나 저장되어 있을 때나 항상 암호화되며, 개발자조차 운영 DB 패스워드를 알 필요가 없는 구조를 만들었습니다."
> 

---

## 4. 접근 제어 및 감사 (2분)

### 4.1 IAM 및 권한 분리

- 최소 권한 원칙(Least Privilege)에 따라 ECS Task Role, 개발자 접근 권한을 세분화하여 부여했습니다.

### 4.2 Bastion Host를 통한 접근 통제

- **운영 접근**: 개발자나 관리자가 유지보수를 위해 내부 서버(Private Subnet)에 접근해야 할 경우, 오직 **Public Subnet의 Bastion Host**를 통해서만 SSH 접근이 가능합니다.
- 이 Bastion Host는 특정 IP에서만 접근 가능하도록 Security Group으로 엄격히 제한됩니다.

### 4.3 로깅 및 감사 (Auditing)

- **CloudTrail**: AWS 인프라 상의 모든 API 호출 기록을 저장하여 누가 언제 무엇을 변경했는지 추적합니다.
- **CloudWatch**: CPU, 메모리 등 리소스 모니터링뿐만 아니라, 애플리케이션 로그를 수집하여 이상 징후 발생 시 SNS와 AWS Chatbot(Slack)을 통해 즉시 알림을 발송합니다.

> 🗣️ 발표 포인트: "모든 접근 우회로는 차단되어 있으며, 시스템 상의 모든 변경 사항은 CloudTrail에 기록되어 사후 감사가 가능합니다."
> 

---

## 5. 고가용성 및 재해 복구 (2분)

### 5.1 Multi-AZ 아키텍처

- **이중화**: 다이어그램에서 보듯이, 모든 리소스(ECS, RDS, ElastiCache, ALB)는 두 개의 가용 영역(AZ)에 분산 배치되어 있습니다.
- 하나의 데이터센터에 화재나 장애가 발생해도, 트래픽은 자동으로 정상적인 AZ로 라우팅되어 서비스 중단을 막습니다.

### 5.2 자동 복구 (Auto-Healing)

- **ECS Fargate**: 컨테이너가 비정상 종료될 경우, ECS 스케줄러가 이를 감지하고 즉시 새로운 컨테이너를 실행합니다.
- **RDS Failover**: Primary DB 장애 시, Standby DB가 자동으로 Primary로 승격되어 데이터 손실 없이 서비스를 지속합니다.

> 🗣️ 발표 포인트: "단일 지점 장애(SPOF)가 없도록 설계되었으며, 하드웨어 장애 시에도 사용자의 개입 없이 시스템이 스스로 복구됩니다."
> 

---

## 6. Q&A 예상 질문 답변 (대본)

### Q1. "데이터베이스가 인터넷에 노출되어 있나요?"

> A1. "아니요, 전혀 노출되어 있지 않습니다. 다이어그램 하단을 보시면 RDS와 ElastiCache는 Private Subnet 깊숙이 위치해 있습니다. 외부 IP(Public IP) 자체가 할당되지 않으며, 오직 내부 애플리케이션 서버를 통해서만 접근이 가능합니다."
> 

### Q2. "서버(컨테이너)가 갑자기 죽으면 어떻게 되나요?"

> A2. "ECS Fargate의 Auto-Healing 기능이 작동합니다. 헬스 체크(Health Check)가 실패하는 즉시 비정상 컨테이너를 내리고, 깨끗한 상태의 새 컨테이너를 자동으로 실행하여 서비스를 복구합니다. 사용자는 찰나의 지연만 느낄 뿐 서비스 중단은 경험하지 않습니다."
> 

### Q3. "해커가 SSH로 침입하려고 하면 어떻게 방어하나요?"

> A3. "먼저, 실제 애플리케이션 서버(Fargate)는 SSH 포트 자체가 열려있지 않습니다. 유일한 SSH 진입점인 Bastion Host는 허용된 관리자 IP에서만 접속이 가능하며, SSH 키 파일이 없으면 접속할 수 없습니다. 또한 모든 접속 시도는 로그로 남습니다."
> 

### Q4. "개인정보보호법이나 컴플라이언스 이슈는 없나요?"

> A4. "네, 준수하고 있습니다. WAF를 통해 웹 공격을 방어하고, 저장되는 모든 개인정보는 암호화(KMS) 처리됩니다. 또한 DB 접근 제어와 CloudTrail 감사 로그를 통해 데이터 접근 이력을 투명하게 관리하고 있어 보안 규정을 충족합니다."
>