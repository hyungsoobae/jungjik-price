# 정직 (JUNGJIK) - 무신사 상품 가격 추적기

> 무신사 상품 URL을 등록하면 가격 변동을 자동으로 수집하고, Slack으로 알림을 보내주는 가격 추적 서비스

---

## 🧩 프로젝트 소개

관심 있는 무신사 상품을 등록해두면:

- 매일 정해진 시각에 가격을 자동 수집
- 가격 변동이 감지되면 Slack으로 즉시 알림
- 가격 이력 차트와 최저/평균/최고가 통계를 웹 UI로 확인

관심 상품이 할인됐을 때 놓치지 않기 위해 만들었습니다.

---

## 🛠 기술 스택

| 분류 | 기술 |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| ORM | Spring Data JPA + Hibernate |
| Query | QueryDSL 5 |
| DB | PostgreSQL 16 |
| Migration | Flyway |
| Template | Thymeleaf + Thymeleaf Layout Dialect |
| Scraping | Jsoup |
| ID 전략 | TSID (hypersistence-utils) |
| 알림 | Slack Incoming Webhook |
| Build | Gradle 8 |
| Infra | Docker Compose |

---

## 🏗 아키텍처 & 주요 설계 결정

### 패키지 구조

```
com.zz95.jungjik
├── api/           # REST API 컨트롤러 (요청/응답 DTO 포함)
├── domain/        # 핵심 도메인 (Entity, Service, Repository)
│   ├── price/
│   └── product/
├── scraping/      # 스크래핑 인터페이스 및 구현체
│   └── musinsa/
├── schedule/      # 스케줄러
├── ui/            # 뷰 컨트롤러 (Thymeleaf)
└── global/        # 공통 (에러 처리, 설정, Slack, Interceptor)
```

### 스크래퍼 전략 패턴
`PriceScraper` 인터페이스를 통해 쇼핑몰별 스크래퍼를 분리했습니다. `ScraperResolver`가 URL을 보고 적절한 스크래퍼를 선택합니다. 추후 다른 쇼핑몰을 추가할 때 기존 코드를 건드리지 않고 새 구현체만 추가하면 됩니다.

### 비동기 가격 수집
가격 수집은 `@Async("scrapingExecutor")`로 처리합니다. 스케줄러가 상품 목록을 순회하며 각 수집 작업을 스레드 풀(`core: 3, max: 5`)에 위임하므로, 상품 수가 늘어도 스케줄러 스레드가 블로킹되지 않습니다.

### 트랜잭션 경계 분리
`@Async` 메서드 안에서 직접 트랜잭션을 열면 `LazyInitializationException`이나 detached 엔티티 문제가 생기기 쉽습니다. 가격 이력 저장·업데이트 로직은 `PriceUpdateService`로 분리해 별도 트랜잭션에서 처리하고, 결과는 `PriceUpdateResult` DTO로 반환해 트랜잭션 종료 후에도 안전하게 사용합니다.

### 페이지네이션 전략
- **최신순(LATEST)**: No-offset 커서 기반 페이징. `id`를 커서로 사용해 대량 데이터에서도 성능이 일정합니다.
- **가격순/등락순**: OFFSET 기반 페이징. 정렬 기준이 바뀌면 커서 기반 적용이 복잡해지므로 분리했습니다.

### TSID
`AUTO_INCREMENT` 대신 TSID(Time-Sorted ID)를 사용합니다. 시간 순서가 내재되어 있어 No-offset 커서 페이징과 궁합이 좋고, UUID보다 짧아 URL에 노출해도 깔끔합니다.

### Admin API 보호
`/api/admin/**` 경로는 `AdminInterceptor`가 `X-Admin-Token` 헤더를 검증합니다. `MessageDigest.isEqual`을 사용해 타이밍 공격(Timing Attack)을 방지합니다.

---

## 🚀 로컬 실행 방법

**사전 요구사항**: Java 21, Docker & Docker Compose, Slack Incoming Webhook URL

**1. 환경 변수 설정**

프로젝트 루트에 `.env` 파일 생성:

```env
POSTGRES_DB=jungjik
POSTGRES_USER=jungjik
POSTGRES_PASSWORD=your_password
DATASOURCE_URL=jdbc:postgresql://localhost:5432
```

`src/main/resources/application.yaml` 생성 (`application-sample.yaml` 참고):

```yaml
slack:
  webhook:
    user-url: ${SLACK_WEBHOOK_USER_URL}
    admin-url: ${SLACK_WEBHOOK_ADMIN_URL}

admin:
  api:
    token: ${ADMIN_API_TOKEN}

scraping:
  user-agents:
    - "Mozilla/5.0 ..."
```

**2. 실행**

```bash
docker-compose up -d
./gradlew bootRun
```

Flyway가 자동으로 테이블을 생성합니다. `http://localhost:8080/products` 접속

---

## 📡 주요 API

| Method | URL | 설명 |
|--------|-----|------|
| `POST` | `/api/products` | 상품 등록 |
| `GET` | `/api/products` | 상품 목록 (검색, 정렬, 페이징) |
| `GET` | `/api/products/{id}` | 상품 상세 |
| `DELETE` | `/api/products/{id}` | 상품 삭제 |
| `GET` | `/api/products/{id}/price-histories` | 가격 이력 조회 |
| `POST` | `/api/admin/prices/collect` | 전체 상품 가격 수동 수집 |
| `POST` | `/api/admin/prices/collect/{id}` | 단일 상품 가격 수동 수집 |

Admin API는 요청 헤더에 `X-Admin-Token: {token}` 필요

---

## ⏰ 스케줄

| 주기 | 동작 |
|------|------|
| 매일 12:00 | 활성화된 전체 상품 가격 수집 → 변동 시 Slack 알림 |