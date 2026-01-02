# 병원 예약 시스템

온라인으로 병원 진료 예약을 할 수 있는 웹 애플리케이션입니다.

## 프로젝트 개요

환자가 진료과와 의사를 선택하고, 예약 가능한 시간을 확인하여 진료 예약을 할 수 있는 시스템입니다.

### 개발 기간 및 인원
- 2025.11.30 - 2025.12.06 | 개인 프로젝트

---

## 기술 스택

### Backend
- Java 21
- Spring Boot 3.5.8
- Spring Data JPA
- Hibernate

### Database
- Oracle 21c

### Frontend
- Thymeleaf (Template Engine)
- TypeScript 5.9.3
- HTML5, CSS3
- Vanilla JavaScript

### Build Tool
- Gradle 8.14.3

---

## 주요 기능

### 1. 진료과별 의사 조회
- 진료과 선택 시 소속 의사 목록 표시
- 의사별 휴무일 정보 제공
- 메인 화면
<img src="https://github.com/user-attachments/assets/1311ac22-65b3-46c0-9283-3fb8cd0ebdb2" alt="image" width="700" />


### 2. 예약 가능 시간 조회
- 선택한 날짜의 예약 가능한 시간대 표시 (30분 단위)
- 평일: 09:00~18:00 (점심시간 13:00~14:00 제외)
- 토요일: 09:00~14:00 (점심시간 없음)
- 일요일 및 의사 개인 휴무일 자동 제외
 <img src="https://github.com/user-attachments/assets/21a300a2-4f11-4dff-baf8-434c241cc87b" alt="image" width="700" />


### 3. 예약 생성 및 중복 방지
- 실시간 예약 가능 시간 확인
- 중복 예약 자동 차단
- 예약 완료 시 예약 정보 확인창 제공
  
<img src="https://github.com/user-attachments/assets/682cfcfd-3b77-4dab-adad-794fba4df51d" alt="image" width="700" />


---

## 시스템 아키텍처

### 계층 구조
```
┌─────────────────────────────────┐
│ Presentation                    │  ← Controller, Thymeleaf, HTML/CSS/TS
├─────────────────────────────────┤
│ Business Logic                  │  ← Service
├─────────────────────────────────┤
│ Data Access                     │  ← Repository (JPA)
├─────────────────────────────────┤
│ Database                        │  ← Oracle
└─────────────────────────────────┘
```

### 주요 설계 원칙
- **관심사의 분리**: 병원 운영 시간은 설정 파일(`application.yml`)로 관리
- **DRY 원칙**: 공통 운영 시간 중복 제거
- **확장 가능한 구조**: 의사별 개인 휴무일만 데이터베이스에 저장

---

## 데이터베이스 설계

### ERD
```
DEPARTMENT (진료과)
├─ dept_id (PK)
├─ dept_name
└─ description

DOCTOR (의사)
├─ doctor_id (PK)
├─ dept_id (FK → DEPARTMENT)
├─ name
├─ day_off (휴무일)
└─ created_at

USER (환자)
├─ user_id (PK)
├─ email
├─ password
├─ name
├─ phone
├─ birth_date
└─ created_at

RESERVATION (예약)
├─ reservation_id (PK)
├─ user_id (FK → USER)
├─ doctor_id (FK → DOCTOR)
├─ reservation_datetime
├─ status (PENDING, CONFIRMED, CANCELLED)
├─ created_at
└─ updated_at
```

---

## 실행 방법

### 1. 사전 요구사항
- Java 21 이상
- Oracle Database 21c
- Node.js (TypeScript 컴파일용)

### 2. 데이터베이스 설정
```sql
-- 사용자 생성
CREATE USER reservation_user IDENTIFIED BY res1234;
GRANT CONNECT, RESOURCE TO reservation_user;

-- 테이블 자동 생성 (Spring Boot 실행 시)
```

### 3. 애플리케이션 실행
```bash
# Backend 실행
./gradlew bootRun

# Frontend 컴파일 (수정 시)
cd frontend
npx tsc
cp dist/reservation.js ../src/main/resources/static/js/
```

### 4. 접속
```
http://localhost:8080/reservations/new
```

### 5. 테스트 데이터 초기화
```
http://localhost:8080/api/v1/test/init
```

---

## 프로젝트 구조
```
reservation-system/
├── src/main/
│   ├── java/com/example/demo/
│   │   ├── api/              # REST API Controller
│   │   ├── config/           # 설정 클래스
│   │   ├── controller/       # View Controller
│   │   ├── domain/           # Entity
│   │   ├── repository/       # JPA Repository
│   │   └── service/          # Business Logic
│   └── resources/
│       ├── static/
│       │   ├── css/         # 스타일시트
│       │   └── js/          # JavaScript
│       ├── templates/       # Thymeleaf 템플릿
│       └── application.yml  # 설정 파일
└── frontend/
    ├── src/
    │   └── reservation.ts   # TypeScript 소스
    └── dist/
        └── reservation.js   # 컴파일된 JavaScript
```

---

## 주요 기술적 의사결정

### 1. DOCTOR_SCHEDULE 테이블 제거
**문제점:**
- 모든 의사가 동일한 운영 시간을 가짐에도 개별 레코드로 저장
- 6명 의사 × 5개 레코드 = 30개 중복 데이터

**해결:**
- 공통 운영 시간 → `application.yml`로 관리
- 개인 휴무일만 `DOCTOR.day_off` 컬럼에 저장
- 데이터 중복 제거 및 유지보수성 향상

### 2. 설정 파일 기반 운영 시간 관리
```yaml
hospital:
  schedule:
    weekday:
      start-time: "09:00"
      end-time: "18:00"
    saturday:
      start-time: "09:00"
      end-time: "14:00"
    lunch:
      start-time: "13:00"
      end-time: "14:00"
```

**장점:**
- 코드 재컴파일 없이 운영 시간 변경 가능
- 관심사의 분리 (설정 vs 비즈니스 로직)

---

## 핵심 구현 내용

### 1. 예약 가능 시간 계산 로직
```java
public List<LocalTime> getAvailableSlots(Long doctorId, LocalDate date) {
    // 1. 휴무일 체크 (일요일, 의사 개인 휴무일)
    // 2. 운영 시간 설정 (평일/토요일 구분)
    // 3. 30분 단위 슬롯 생성
    // 4. 점심시간 제외 (평일만)
    // 5. 예약된 시간 제거
    // 6. 최종 예약 가능 시간 반환
}
```

### 2. 중복 예약 방지
```java
private void validateDuplicateReservation(Long doctorId, LocalDateTime reservationTime) {
    List<String> occupiedStatuses = List.of("CONFIRMED", "PENDING");
    List<Reservation> duplicates = reservationRepository
        .findByDoctorIdAndReservationDatetimeAndStatusIn(
            doctorId, reservationTime, occupiedStatuses);
    
    if (!duplicates.isEmpty()) {
        throw new IllegalStateException("이미 예약된 시간입니다.");
    }
}
```

---

## API 명세

### 진료과 목록 조회
```
GET /api/v1/departments
Response: List<Department>
```

### 진료과별 의사 목록 조회
```
GET /api/v1/departments/{deptId}/doctors
Response: List<Doctor>
```

### 예약 가능 시간 조회
```
GET /api/v1/doctors/{doctorId}/availability?date=YYYY-MM-DD
Response: List<String> (예: ["09:00", "09:30", ...])
```

### 예약 생성
```
POST /api/v1/reservations
Content-Type: application/x-www-form-urlencoded

Parameters (Query String 또는 Form Data):
- userId: Long (환자 ID)
- doctorId: Long (의사 ID)
- reservationTime: String (예약 시간, ISO 형식: "2025-01-15T09:00")

Response: Long (예약 ID)
```

##  

이 프로젝트는 Spring Boot와 JPA를 학습하기 위한 개인 프로젝트입니다.
