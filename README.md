# Code Snacc Backend

일본어 문서는 아래 파일음 참고해주세요.

日本語の文書はこちらのファイルをご覧ください。
[日本語の文書](README-JP.md)

## 🖥️ 프로젝트 개요

---
자그마한 코드 한입

### 🗓️ 개발기간
2025년 4월 21일 ~ 2025년 7월 10일

### ⚙️ 사용기술
- kotlin
- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- MySQL

### 🌐 더보기
![Static Badge](https://img.shields.io/badge/Notion-project-a97bff?logo=notion&link=https%3A%2F%2Fwww.notion.so%2FCodeSnacc-1dcad8559b2d8076b1dcc5a8c7641961)
![Static Badge](https://img.shields.io/badge/Notion-frontend-92e0d0?logo=notion&link=https%3A%2F%2Fgithub.com%2Fyareach8345%2Fcode-scann-frontend)
![Static Badge](https://img.shields.io/badge/Notion-portfolio-aaaaaa?logo=notion&link=https%3A%2F%2Fwww.notion.so%2FPortfolio-JP-257ad8559b2d80a28d1ac84052eea6f2)

## 사용법
### 데이터베이스 준비
아래의 스크립트 파일의 명령을 실행하여 테이블을 생성합니다.

[sql file for initialize table](docs/sql/schema-initial.sql)

※ 위 스크립트는 MySQL 환경을 전제로 작성되었습니다.

### Configuration
운용에 필요한 설정을 진행합니다.

```properties
spring.application.name=code-snacc-backend
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=validate
```
| property                            | value                |
|-------------------------------------|----------------------|
| spring.datasource.url               | 데이터 베이스에 연결하기 위한 url |
| spring.datasource.username          | 데이터 베이스 유저네임         |
| spring.datasource.password          | 데이터 베이스 비밀번호         |
| spring.datasource.driver-class-name | 데이터 베이스 드라이버         |

MySQL이외의 다른 데이터베이스를 사용할 경우 위 스크립트 파일의 실행과 레포지토리 메서드 사용시 문제가 발생할 수 있습니다.


### 문서 읽기
* [시나리오](https://www.notion.so/Code-Snacc-30fad8559b2d804cb82dd138f5202e53)
* [엔드포인트](https://www.notion.so/CodeSnacc-30fad8559b2d8085bffbd0d90b65dfc8?source=copy_link#30fad8559b2d809fa9b5d62fb7312b9e)
