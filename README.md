# Java SAML Example

### Example SAML SSO Case
SP-Initiated SSO : Redirect/POST Bindings 을 사용합니다.

### Dependency
[OpenSAML2](https://wiki.shibboleth.net/confluence/display/OpenSAML/Home), [spring-security-saml2](https://github.com/spring-projects/spring-security-saml)  
OpenSAML, spring-security-saml2 없이 구현한 예제는 [링크](https://github.com/acafela/java-saml-example/tree/without-opensaml) 를 참고해주세요   

### 실행 환경

- Java 1.8+

### 실행 하기

#### 1. 소스 다운로드

```bash
git clone https://github.com/acafela/java-saml-example.git
cd java-saml-example
```

#### 2. Identity Provider 실행

```bash
./gradlew :saml-example-idp:bootRun
```

#### 3. Service Provider 실행

```bash
./gradlew :saml-example-sp:bootRun
```

#### 4. [Service provider - http://localhost:9106/user](http://localhost:9106/user) 접속

- _Identity Provider 인증 페이지로 리다이렉트 됩니다._

#### 5. Identity Provider 인증 페이지에서 ID/PWD 입력해 로그인

- 어드민 계정 ID/PWD : admin / admin123  
- 일반 사용자 계정 ID/PWD : user / user123  

  ![Java SAML Example 인증 화면](https://acafela.github.io//assets/capture/java-saml-example-capture1.PNG)

#### 6. 인증 성공후 IdP SAML Response - NameID, Attributes 값 확인

- admin 계정으로 로그인 시  
  ![Java SAML Example 인증 완료 화면1](https://acafela.github.io//assets/capture/java-saml-example-capture2.PNG)

- user 계정으로 로그인 시  
  ![Java SAML Example 인증 완료 화면2](https://acafela.github.io//assets/capture/java-saml-example-capture3.PNG)

### Reference

- [OASIS Doc](http://docs.oasis-open.org/security/saml/Post2.0/sstc-saml-tech-overview-2.0.html)
- [OpenConext/Mujina](https://github.com/OpenConext/Mujina)
- [pac4j](https://github.com/pac4j/pac4j)
