# java-janggi

Java로 구현한 콘솔 기반 장기(韓國將棋) 게임입니다.

## 실행 환경

- Java 21
- Gradle 8.7
- H2 Database (임베디드)

## 실행 방법

```bash
./gradlew run
```

## 주요 기능

- 판차림 선택 (왼상/오른상/안상/바깥상 4가지)
- 7종 기물의 이동 규칙 구현 (차·마·상·사·장·포·졸)
- 궁성 영역 제한 (장·사는 궁 밖으로 이동 불가)
- 기물 점수 계산 (초나라 1.5점 선공 보정 포함)
- 게임 저장 및 불러오기 (H2 DB, 매 턴 자동 저장)

## 설계

### 이동 규칙 — 전략 패턴

기물의 이동 방식을 `MoveRule` 인터페이스로 추상화하고, 각 규칙을 독립적인 구현체로 분리했습니다.

| 구현체 | 설명 |
|--------|------|
| `NormalMoveRule` | 직선 슬라이딩 (거리 제한 파라미터로 조절) |
| `StepMoveRule` | 경로를 순서대로 밟는 이동 (마·상) |
| `PalaceMoveRule` | 궁성 내 단일 이동 |
| `PalaceSlidingMoveRule` | 궁성 내 슬라이딩 (차의 대각선) |
| `CannonMoveRule` | 포다리 규칙 적용 이동 |

새로운 기물을 추가할 때 기존 코드를 수정하지 않고 `MoveRule` 구현체 조합만으로 확장할 수 있습니다.

### 기물 — 조합 우선 설계

상속 대신 `PieceAction`(이동 규칙 목록)을 주입하는 방식으로 기물을 구성합니다. 각 기물 클래스는 자신의 이동 규칙 생성 책임만 가집니다.

```
Piece (interface)
  └── AbstractPiece
        ├── Chariot, Cannon, Horse, Elephant
        ├── Guard, General
        └── Soldier
```

### 패키지 구조

```
janggi/
├── controller   # 게임 흐름 제어
├── domain       # 핵심 도메인 (board, piece, movement, team)
├── db           # DAO, DB 초기화
├── repository   # DB 접근 조율
├── dto          # 뷰 전달 객체
└── view         # 콘솔 입출력
```
