# PR #2 — CD 신설: 위성 배포 대상이 되다

이 문서는 정산(settlement)이 실제 배포 경로에 올라온 과정을 다룬다. 정산은 배포 대상 넷 중 하나로 ADR-027이 이미 정해 뒀지만(정산→.158), 정작 CD(배포 요청 워크플로)가 없어 배포될 수 없었다. 이 작업이 그 마지막 칸을 채운다.

## 문제와 배경

정산 서비스는 스켈레톤 상태로 빌드 CI(이미지 빌드)만 있었고, 배포 요청을 발행하는 CD가 없었다. 그리고 정산은 core·gateway와 배포 형태가 다르다 — **원격 위성**이다. .9의 main이 서명 RPC로 .158의 agent에 명령을 보내면 그 agent가 실행한다. 다만 CD가 만드는 것은 "서명된 배포 요청 하나"뿐이고, 그것이 로컬 배포든 원격 위성 배포든 계약은 같다 — 배포 모드는 agent 쪽이 정한다.

## 어떻게 — core 원형의 정확한 복제

CD는 core의 deploy.yml을 원형으로 **바이트 수준으로 복제**했다. 입력 하드닝(4항 AND 가드로 브랜치·fork 우회 차단, 표현식 보간을 env로 격리, SHA 40hex 검증), compose 동봉(한 번 읽은 버퍼에서 sha256과 base64를 함께 산출한 뒤 self-assertion 5종을 통과해야 manifest 발행), 배포 대상 SHA로 checkout — 이 모두가 core와 같다. 다른 것은 target·이미지 저장소·concurrency 그룹·요청 ID 접두 5줄뿐이다.

정본 compose는 최소형이다 — 정산은 스켈레톤이라 상태 볼륨이나 환경변수가 없어서, 서비스 하나(`app`)에 이미지·포트·healthcheck만 있다.

```yaml
# deploy/compose.yml — 최소 정본(agent allowlist 통과 형태)
services:
  app:
    image: ${CORE_IMAGE}                    # 전역 DEPLOY_IMAGE_ENV 계약(agent가 digest 주입)
    ports: ["${DEPLOY_HOST_PORT}:8080"]
    restart: unless-stopped
    healthcheck:
      test: ["CMD-SHELL", "wget -qO- http://127.0.0.1:8080/actuator/health || exit 1"]
      ...
```

이미지 변수가 `${SETTLEMENT_IMAGE}`가 아니라 `${CORE_IMAGE}`인 건 agent의 검증기가 **전역 단일** `DEPLOY_IMAGE_ENV`(현재 `CORE_IMAGE`)를 모든 대상에 적용하기 때문이다 — gateway도 같은 이유로 `${CORE_IMAGE}`를 쓴다. per-target 변수를 쓰면 정확 일치 검증에 걸린다.

## 검증

core와의 diff가 의도된 5줄(compose는 헤더 1줄)만임을 확인했고, agent의 실제 검증기(`compose.Validate`)로 정본이 allowlist를 통과함을, self-assertion 스모크로 manifest 조립(3.6KB)을 확인했다. 실배포는 위성(.158)에 agent가 배선된 뒤(infra#36)에야 가능했다.

## 다음으로의 연결 — 실배포로 닫히다

이 CD가 머지되며 이미지가 GHCR에 올랐고, infra#36에서 .158에 위성 agent가 배선된 뒤 실제 배포가 완주됐다 — main → 서명 RPC → .158 위성 → fence-confirm → pull·up으로 `settlement-app-1`이 healthy로 떴다. 오래 "위성 배포 실행 불가"로 남아 있던 칸(CDT-1)이 정산·원장의 실배포로 닫혔다. 분산 실행 층의 전체 이야기는 infra의 [위성 여정 문서](../../../infra/docs/devlog/pr-34-38-satellite-transport.md)에 있다.
