# SchoolBoardAlarmService
학교 게시판 알람 서비스



**2026-05-20**
## 학교 데이터 가져오기 성공 & 테스트 통과!
<img width="614" height="251" alt="image" src="https://github.com/user-attachments/assets/764a99be-c122-4103-9443-2217eb55c2cd" />



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
