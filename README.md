# Spring Boot 3.3 + PostgreSQL 18 Benchmark Project

Spring Boot와 PostgreSQL(pgbench schema) 환경에서 단일 서버 환경 내 최대 TPS(Transactions Per Second) 및 Latency 최적화를 검증하기 위한 벤치마크 프로젝트입니다.

## 🛠 Tech Stack
- **Framework:** Spring Boot 3.3.4 (Java 21)
- **Database:** PostgreSQL 18.6
- **Connection Pool:** HikariCP
- **Load Test Tool:** ApacheBench (`ab`)

## ⚡ Performance Optimization Summary

| 단계 | 주요 변경 항목 | Concurrency (`-c`) | Keep-Alive (`-k`) | TPS | Mean Latency |비고 |
|:---|:---|:---:|:---:|:---:|:---:|:---|
| **Base** | Scale=10, shared_buffers=1GB | 10 | OFF | 14,304 TPS | 0.699 ms | 인메모리 급 성능 |
| **Step 1** | Scale=500 (5천만 건), shared_buffers=1GB | 10 | OFF | 11,058 TPS | 0.904 ms | 디스크 I/O 병목 발생 |
| **Step 2** | shared_buffers=4GB, Pool=30, AccessLog OFF | 10 | OFF | 13,198 TPS | 0.758 ms | DB 버퍼 캐시 확충으로 성능 회복 |
| **Step 3** | Concurrency 확장 (`-c 50`) | 50 | OFF | 14,888 TPS | 3.358 ms | Hikari Queue & Row Lock 대기 발생 |
| **Final** | HTTP Keep-Alive 적용 (`-k`) | 50 | ON | **18,568 TPS** | **2.693 ms** | TCP Overhead 제거, 100만 건 무결점 처리 |

## 💡 Key Learnings & Troubleshooting
1. **Access Log I/O 병목 제거:** Tomcat Access Log 및 Spring Application Logger (`logging.level.ACCESS_LOG=OFF`) 비활성화를 통한 CPU/I/O 자원 확보.
2. **PostgreSQL Buffer Pool Tuning:** `pgbench.scale=500` (5,000만 건) 대용량 환경에서 `shared_buffers`를 1GB → 4GB로 확장하여 TPS +19.3% 회복.
3. **HTTP Connection Overhead:** HTTP Keep-Alive (`-k`) 적용으로 Socket TIME_WAIT 방지 및 TPS +24.7% 향상.
4. **Row Lock Contention Analysis:** `pgbench_branches` (500개 행) / `pgbench_tellers` (5,000개 행)에 대한 동시 UPDATE 과정에서 발생하는 Lock Wait 병목 분석.

## 🚀 How to Run

### 1. Build
```bash
mvn clean package -DskipTests
```

### 2. Setting
```bash
cd target
mkdir config
cp classes/application.properties config
# config/application.properties 파일 편집
```

### 3. Running
```bash
java -jar target/pgbench-0.0.1.jar
```

### 4. at Web Browser
http://127.0.0.1/

### 5. 부하 테스트
```bash
ab -c 50 -t 60 -n 1000000 -l http://127.0.0.1:8080/
```
