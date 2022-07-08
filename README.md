# 스마트한 약 알리미 리팩터링 버전 [졸프]
<hr>
<img src="./images/header.png" alt="스마트 한 약 알리미">

본 프로젝트는 졸업 프로젝트로 진행 했던 스마트한 약알리미 앱에 RestAPI 서버 리팩터링 버전으로    
기존 코드를 수정 및 보완 하였으며 Swagger를 사용하여 새롭게 문서화 하였습니다.


## 소개

해당 프로젝트는 건강 보험심사 평가원에서 3주간 개발했던 **노약자를 위한 복용 알리미** 앱에 후속 버전으로 노약자를 위한 복용 알리미 앱은 주 사용층이 몸이 불편한 **노약자**와  **비장애인** 이였다면 해당 프로젝트는 모든 사용자가 올바르게 약을 복용할 수 있도록 제작하게 되었습니다.

## 📘서비스 내용

해당 앱은 사용자가 자신이 복용하는 약에 대한 정보를 **딥러닝**과 **OCR 기능**을 사용하여 빠르게 찾아볼 수 있으며 복용알림을 통해  제 시간에 약을 복용 할 수 있도록 도와주는 앱이다.

### ☝️핵심 기능

1. **딥러닝** 모델을 사용하여 약캡처를 통해 알약 인식 후 등록
2. **OCR** 기능을 사용하여 처방전과 약봉투를 촬영하여 여러 종류에 약 등록
3. 심평원 데이터를 사용하여 평점에 따른 약국, 병원찾기 기능 제공
4. 자가문진 기능을 제공하여 증상에 따른 방문해야하는 진료과를 알려주는 기능
5. 모양, 색상, 제형, 분할 선 유무 등 약명 검색을 통해 약 정보

## 🪛기술 스택

> - Spring 
>   - spring boot, spring security, spring batch
>   - Jpa, QueryDsl
> - Swagger
> - DB 
>   - PostgreSQL, Redis, H2
> - Docker
>   - docker-compose
> - AWS EC2, RDS


### 실행방법
실행 후 약 정보가 데이터 베이스에 저장 되므로 1분 안밖으로 시간이 소요됩니다.

메일 기능 로그로 대체 되어 있으며 메일 기능 사용 시 yml에 mail 관련 속성 수정 요망

 서버 실행 후 -> [스웨거](http://localhost:8080/swagger-ui/)
```
> build
gradle clean build -x test

> 실행
docker-compose up
```
## 📖개발 내용

해당 프로젝트에서 Spring 서버 개발, 앱 디자인 및 기획, 약 분류를 위한 딥러닝 모델  설계, 등 업무를 맡아 진행하였습니다.

1. **데이터 설계**
    1. 사용자 정보, 복용 알람 테이블, 약정보 등 비교적 간단한 테이블 설계
    2. 약학정보원에서 제공하는 데이터를 편의에 맞게 검색할 수 있도록 내부 DB로 옮겨서 사용
2. **RestApi 개발**
    1. 약 조회 API
    2. 사용자 정보 조회 API
    3. 복용 알람 및  관심 약품 등록 API
3. **딥러닝 모델 설계**
    1. 약학정보원에서 제공하는 API중 약 이미지를 전처리 과정을 통해 데이터 준비
    2. 구글에서 만든 **MobileNet**을 사용하여 전이 학습

       기존 **VGG-16** 모델을 바탕으로 모델을 직접 설계하였지만 모델의 크기 증가 및 정확도 저하로 변경

4. **앱 디자인 및 기획**
    1. 심평원에서 만들었던 앱에 아쉬웠던 점을 보완 하여 기획
    2. AdobeXD를 통한 앱 디자인
