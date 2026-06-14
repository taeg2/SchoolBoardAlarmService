# SchoolBoardAlarmService
학교 게시판 알람 서비스


---
**2026-05-20**
## 학교 데이터 가져오기 성공 & 테스트 통과!
<img width="614" height="251" alt="image" src="https://github.com/user-attachments/assets/764a99be-c122-4103-9443-2217eb55c2cd" />

---
**2026-05-21**
## 텔레그램과 내 local 서버 연결 성공
**오늘 한 일**
1. 텔레그램 메시지를 전송받는 봇 생성
2. 텔레그램 명령어에 따른 수행하는 서비스 코드 생성 (RESTAPI CONTROLLER 처럼 특정 endpoint와 value 입력하면 명령 수행)
3. 텔레그램이 내 서버와 연결하기 위한 REST API 생성 (이 API로 모든 message를 JSON 형태로 담아서 보냄)

<img width="366" height="174" alt="image" src="https://github.com/user-attachments/assets/a08bf062-456e-44d3-8592-ee511ea90fec" />
<img width="363" height="99" alt="image" src="https://github.com/user-attachments/assets/fd5e1615-30ba-451b-9aee-b313d70e915d" />

error: 학교를 분명 입력 했는데 새로운 게시글에 대한 연락이 오지 않는 오류 발생
 
```java
UserUniv newUserUniv = UserUniv.builder()
                .user(user)
                .univ(univ)
                .build();
```
UserUniv를 build 하는데 isActive를 같이 전달하지 않아서 오류가 발생함.
DB에 값을 전달하지 않으면 자동으로 null로 들어간다는 사실을 까먹고 있었음.

solution:
```java
UserUniv newUserUniv = UserUniv.builder()
                .user(user)
                .univ(univ)
                .isActive(UnivStatus.ENROLLED)
                .build();
```

<img width="372" height="637" alt="image" src="https://github.com/user-attachments/assets/33d00a1a-8193-4c93-b3e2-24510558fb56" />

넣고 돌려보니까 바로 전송 잘 되는 걸 확인함.

**앞으로 할 일**
1. iptime 공유기에 내 도메인 만들고 nginx 써서 https 프로토콜 생성
2. DB H2 -> MySQL로 교체

---
2026.06.14
## iptime 공유기 포트 포워딩, 고정 주소로 mini pc에 접근 허용

MVP는 이미 전부 개발 끝났기 때문에 배포만 하면 끝.
iptime 공유기 포트 포워딩, 고정 ip 사용 -> 외부에서 미니 pc로 보내느 요청 수용 가능하게 하고 미니pc ip 고정함.
iptime 공유기 도메인 사용 할려다가 (u+ -> iptime)으로 들어오는 이중 공유기 시스템이라 iptime 도메인으로는 외부 ip로 접근이 불가능함.
따라서 duckdns를 사용해서 외부 ip로 접근 가능하도록 dns 시스템 구축

미니 pc 서버 돌리기 위해 docker 설치하고 docker-compose.yml, dockerfile, nginx.conf, .env 파일 만들어서 배포 준비 끝.

**배포 하던 중 발생한 문제**
window의 WSL을 사용해서 docker를 돌릴려고 하던 도중 docker 인증 때문에 window pc로는 배포가 불가능 함.
따라서 mini pc의 os 교체 진행 window -> ubuntu로 교체
docker 설치후 `docker-compose up -d` 했더니 잘 돌아감.

SSH를 사용해서 리눅스 pc에 원격 접속 후 docker 파일과, jar 파일 넘겨 받아서 docker compose up -d 실행 작동이 잘 되는 것을 확인함.


## 다른 학교도 추가적으로 크롤링 하기 위해 전략 패턴 도입

