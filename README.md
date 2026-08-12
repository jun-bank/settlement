# settlement

jun-bank 업무 배포 3 중 `settlement` 서버의 워킹 스켈레톤이다. 도메인 로직 없이
라우팅과 첫 배포를 실증하기 위한 최소 Spring Boot(Kotlin) 서버로, `GET /`은
`I'm settlement server` 평문을 반환하고 `GET /actuator/health`가 준비성 프로브를 제공한다.

## 실행

```
./gradlew bootRun      # 로컬 (8080)
docker build -t junbank-settlement . && docker run -p 8080:8080 junbank-settlement
```
