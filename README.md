# Code Snacc Backend

## 🖥️ プロジェクト紹介

---
小さく美味しいコード一切れ

### 🗓️ 開発期間
２０２５年４月２１日 ~ ２０２５年７月１０日

### ⚙️ 使用技術
- kotlin
- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- MySQL

### 🌐 他のサイト
![Static Badge](https://img.shields.io/badge/Notion-project-a97bff?logo=notion&link=https%3A%2F%2Fwww.notion.so%2FCodeSnacc-1dcad8559b2d8076b1dcc5a8c7641961)
![Static Badge](https://img.shields.io/badge/Notion-frontend-92e0d0?logo=notion&link=https%3A%2F%2Fgithub.com%2Fyareach8345%2Fcode-scann-frontend)
![Static Badge](https://img.shields.io/badge/Notion-portfolio-aaaaaa?logo=notion&link=https%3A%2F%2Fwww.notion.so%2FPortfolio-JP-257ad8559b2d80a28d1ac84052eea6f2)

## 使用方法
### データベースの準備
下のファイルのスクリプトを実行してテーブルを生成してください。

[sql file for initialize table](docs/sql/schema-initial.sql)

※ 本スクリプトはMySQLの環境を前提にして作成されました。

### Configuration
運用に必要な設定を行います。

```properties
spring.application.name=code-snacc-backend
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=validate
```
| property                            | value             |
|-------------------------------------|-------------------|
| spring.datasource.url               | データベースに連結するためのurl |
| spring.datasource.username          | データベースのユーザーネーム    |
| spring.datasource.password          | データベースのパスワード      |
| spring.datasource.driver-class-name | データベースドライバー       |

MySQL以外の他のデータベースを使った場合、上記のファイルにあるスクリプトの実行やリポジトリのメソッド利用時に問題が発生する恐れがあります。


### 文書を読む
* [シナリオ](https://www.notion.so/Code-Snacc-1ddad8559b2d80dd86cee72e8ad5e2c6?source=copy_link)
* [エンドポイント](https://www.notion.so/CodeSnacc-1dcad8559b2d8076b1dcc5a8c7641961?source=copy_link#1dcad8559b2d80e7bcaced2dff6314d5)