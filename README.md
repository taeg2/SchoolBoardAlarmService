# SchoolBoardAlarmService

학교 공지사항을 자동으로 크롤링해서 텔레그램으로 알려주는 알림 봇 서비스입니다.

## 소개

학교 홈페이지 공지 게시판을 매번 직접 들어가서 확인하는 게 번거로워서 만든 프로젝트입니다. 정해진 시간마다 등록된 학교들의 게시판을 크롤링해서, 새로 올라온 글이 있으면 구독자에게 텔레그램 메시지로 바로 알려줍니다.

- 매일 **9시, 12시, 15시, 18시**에 자동 크롤링
- 텔레그램 봇으로 사용자별 구독 학교 관리 (`/start`, `/add`, `/list`, `/cancel`)
- 새 학교를 추가하기 쉽도록 **전략 패턴**으로 크롤링 로직 구조화

## 봇 사용 방법

### 1. `/start` — 봇 시작
처음 대화를 시작하면 사용자로 등록되고, 사용 가능한 명령어 안내를 받습니다.

<img width="500" alt="start 명령어 사용" src="https://github.com/user-attachments/assets/77f8277b-212f-444b-a403-dffb647efdac" />

### 2. `/add` — 등록되지 않은 학교 입력 시 예외 처리
DB에 등록되지 않은 학교 이름을 입력하면, 조용히 실패하는 대신 안내 메시지를 반환합니다.

<img width="500" alt="add 명령어 예외 처리" src="https://github.com/user-attachments/assets/1e9f7a9b-2c60-4608-be35-dbd9a3542c0d" />

### 3. `/add` — 학교 구독 추가
정확한 학교 이름을 입력하면 해당 학교 게시판 알림 구독이 등록됩니다.

<img width="500" alt="add 명령어로 학교 추가" src="https://github.com/user-attachments/assets/de2c25c1-d0a5-424c-a195-d5b25743947e" />

### 4. `/list` — 구독 중인 학교 목록 조회
현재 구독 중인 학교들을 한눈에 확인할 수 있습니다.

<img width="500" alt="list 명령어로 구독 목록 조회" src="https://github.com/user-attachments/assets/63a6b8fc-8870-4204-9b9f-a00a2fffa896" />

### 5. `/cancel` — 구독 취소
더 이상 알림을 받고 싶지 않은 학교는 구독을 취소할 수 있습니다.

<img width="500" alt="cancel 명령어로 구독 취소" src="https://github.com/user-attachments/assets/49f4ff18-cc45-4070-9e27-4987e71434d2" />

## 아키텍처

크롤링 대상 학교가 늘어나면서, 학교마다 게시판 HTML 구조가 전부 달라 크롤링 코드가 한 서비스 클래스에 몰려 비대해지는 문제가 있었습니다. 이를 해결하기 위해 학교별 크롤링 로직을 `UnivCrawlingStrategy` 인터페이스의 구현체로 분리하는 **전략 패턴**을 도입했습니다.

- `MasterCrawlerService`가 스케줄러 역할을 하며, 학교 코드에 맞는 크롤링 전략을 찾아 실행
- 학교를 추가할 때는 새로운 `UnivCrawlingStrategy` 구현체만 추가하면 되므로 확장에 용이함

## 기술 스택

- Java 17, Spring Boot
- Spring Data JPA, MySQL
- jsoup (크롤링)
- Telegram Bots API (Webhook 방식)
- Docker, Docker Compose, Nginx (HTTPS 리버스 프록시)

## 진행 중 / 앞으로의 계획

- **슬랙 챗봇 연동**: Slash Command 수신과 구독 등록(CRUD)까지는 구현했지만, 아직 비동기 처리를 적용하지 못해 요청이 몰릴 경우 슬랙이 스팸으로 처리할 수 있는 문제가 남아있어 실제 알림 발송까지는 연결하지 못한 상태입니다.
- **게시글 수정 감지**: 현재는 새 글 등록만 감지하는데, 기존 게시글이 수정됐을 때도 사용자에게 알려주는 기능을 추가하고 싶습니다.

## 개발 일지

프로젝트를 진행하며 겪은 문제와 해결 과정은 [docs/devlog.md](docs/devlog.md)에 정리되어 있습니다.
