# settlement 배포 축 개발 여정

이 폴더는 정산(settlement) repo의 배포(CD) 축 개발 과정을 기록한다. settlement의 업무 로직이 아니라 "settlement가 어떻게 배포되는가"를 다룬다.

## 이 저장소의 배포 축 역할

settlement는 카드사+뱅킹 시스템의 정산 서비스이고, 배포 대상 중 **원격 위성**(.158)으로 배포된다. .9의 오케스트레이터(main)가 HTTP+HMAC 서명 RPC로 배포 명령을 .158의 로컬 agent에 보내고, 그 agent가 자기 호스트에서 실행한다(core의 로컬 블루-그린, gateway의 재기동과 구별되는 세 번째 배포 형태). 이 분산 실행 층의 설계와 안전 장치(응답 HMAC·crash-safe 원장·fencing guard·자동 재개)는 infra 축이 소유하고, 이 repo가 소유하는 것은 "무엇을 배포할지의 발행"(CD)까지다.

## 문서 목록

| PR | 이슈 | 문서 | 한 줄 |
|---|---|---|---|
| #2 | #1 | [pr-02-cd.md](pr-02-cd.md) | CD 신설 — 위성 배포 대상이 되다 |

분산 실행 층(위성 전송·fencing·재개)의 전체 여정은 infra repo의 `docs/devlog/pr-34-38-satellite-transport.md`, 아키텍처는 docs repo의 `study/tech/infra-journey/`에 있다.
