## 목차
1. 요기먹자?
   * [API 문서](https://documenter.getpostman.com/view/31325959/2sAXxLCEcn)
   * 기술 스택
   * ERD
2. 주요 기능
   * 대용량 INSERT/UPDATE 작업을 위한 Spring Batch 구현
     * 511,273건의 데이터 Batch 작업에 4.72분 소요
     * Batch 처리 과정
   * Redis 캐싱을 사용한 빠른 응답
   * Scheduler를 활용한 점심 추천 식당 리스트 비동기 알림 전송
3. 트러블 슈팅
   * Batch 작업 중 발생한 동시성 문제 해결
   * Batch 처리 속도 향상을 위한 JDBC Template 도입


# 요기먹자🍜🍣🥗
요기먹자는 **위치 기반 맛집 추천 및 리뷰 서비스**의 백엔드 API입니다.
[(API 문서)](https://documenter.getpostman.com/view/31325959/2sAXxLCEcn)

[서울시 일반음식점 인허가 정보 API](https://data.seoul.go.kr/dataList/OA-16094/S/1/datasetView.do)를 기반으로,
* 위치 기반 추천: 사용자의 위치 정보를 기반으로 가깝고 평점이 좋은 식당을 추천합니다.
* 식당 리뷰: 사용자는 식당 리뷰를 작성하고 별점을 매길 수 있습니다.
* 점심 추천 기능: 매일 11시 30분에 사용자 위치 근방의 식당을 추천합니다.


## 기술 스택
* `Spring Boot(3.3)`
  * Webflux
  * Spring Batch
  * Spring Security
  * JWT
  * proj4j
* `JPA/Hibernate`, `QueryDsl`
* `Postgres(16.4)`, `Redis`

## ERD
![yogimukja_erd](https://github.com/user-attachments/assets/0794f9b5-4cc9-4425-ab8f-08f75cf5890c)

# 주요 기능🛠️
## 1) 대용량 INSERT/UPDATE 작업을 위한 `Spring Batch` 구현
`Spring Batch`, `Webflux`, `Scheduler`를 활용하고, 매일 오전 3시에 스케줄러를 실행하여 최신 데이터를 보장합니다.

### ⭐️ `511,273건`의 데이터 Batch 작업에 `4.72분` 소요
![스크린샷 2024-10-03 15 50 59](https://github.com/user-attachments/assets/f2bb9837-8175-4541-924a-b8d93fbb5cb0)
![스크린샷 2024-10-03 15 55 20](https://github.com/user-attachments/assets/2c5a48ed-34b6-4055-8ef5-4c716d729038)

(신규 INSERT 시(빈 테이블)의 소요 시간입니다.)

### ⚙️ Batch 처리 과정
   - `Reader`
     - [서울시 일반음식점 인허가 정보 API](https://data.seoul.go.kr/dataList/OA-16094/S/1/datasetView.do) 사용
     - **Webflux**를 사용하여 **비동기**로 데이터 가져오기
     - 최초 요청으로 전체 API 데이터 범위를 알아낸 후 `ConcurrentLinkedQueue`에 범위 정보를 저장하는 방식으로 **동시성 문제 해결**     
     

   - `Processor`
     - API에서 가져온 **데이터 가공**
     - 유효하지 않은(ex. 폐업한지 5년 이상된 가게 혹은 키즈카페 등의 음식점) **데이터 필터링**
     - 위치 정보 WGS84 **좌표계 변환**, 폐업일 **파싱** 등을 거쳐 `RestaurantPayload` 객체 생성 
     

   - `Writer`
     - 가공한 데이터로 Bulk INSERT/UPDATE 수행
     - 간략한 기존 레스토랑 데이터(관리 ID와 API 업데이트 시점)를 `existingRestaurantMap`에 저장
       - 이를 통해 신규 추가/업데이트할 레스토랑 구분
     - 한 번의 Chunk에서 받은 식당 데이터를 순회하며
       - `existingRestaurantMap`에 해당 관리ID가 없으면 신규 식당으로 간주, 삽입 목록에 추가
       - map에 정보가 존재하지만 기존 식당 데이터의 수정시각보다 api 수정 시각이 최근일 경우, 업데이트 목록에 추가
     - 데이터를 모두 분류한 후, `JdbcTemplate`을 사용하여 각 목록을 bulk insert/update
     

   - `RestaurantReclassificationTasklet`
     - 특정 단어, 브랜드명을 포함한 식당명을 기반으로 식당 카테고리 재분류
     - ex) '투썸 플레이스', '빙수' 등을 포함하는 식당명은 '카페/디저트' 카테고리로 UPDATE

## 2) Redis 캐싱을 사용한 빠른 응답
사용자가 자주 접근하는 정보와 변하지 않는 정보를 Redis에 캐싱하여 조회 시 응답 속도 개선 
캐시 별 유효기간 세분화로 최적화 

* 최근 조회수가 높은 식당 상세 정보
  * 식당 상세 조회 시 <u>Redis에 조회수를 기록</u>하고, **1시간 내에 10회 이상 조회 시** `식당 상세 정보 캐싱`
  * 상세 조회 시 캐시된 데이터를 우선적으로 반환하고, 없을 시에만 DB에서 조회 
  * 유효기간: 1H
* '어제' 리뷰 점수가 높은 식당 10개 리스트
  * 메인 페이지 '랭킹'에 띄울 목적이라 자주 접근하는 정보라고 판단 
  * 유효기간: 24H
* 서울 행정구역 정보 캐싱 
  * 변경 가능성이 낮기 때문에 캐시 유효기간을 두지 않아 성능 최적화 

## 3) Scheduler를 활용한 점심 추천 식당 리스트 비동기 알림 전송
매일 `11시 30분`에 사용자 위치에 따른 **점심 식당 추천 리스트** 전송     

![lunch_recommend](https://github.com/user-attachments/assets/50a88573-8758-4374-82f3-90b8e50aa308)


* `CompletableFuture`로 각 사용자의 점심 추천 메시지를 **비동기적으로 전송**하도록 구현 
* 사용자 맞춤 추천
  * 사용자의 **위치 정보**를 바탕으로 **해당 반경 내의 식당** 리스트 추출
  * Discord `WebHook URL`을 사용하여 추천 정보 전달
* 점심 식사로 부적합한 업종은 필터링
  * ex) '술집', '카페/디저트' 등

# 💡 트러블 슈팅
## 1) Batch 작업 중 발생한 `동시성 문제 해결`
   * 배경/원인
     * API 데이터를 받아오는 작업을 병렬 처리하기 위해 TaskExecutor를 사용하도록 구현
     * 이때, **동일 범위에 대한 요청이 여러 번 발생**하는 문제 발생
     * 작업 범위를 관리하는 동시성 제어가 제대로 이루어지지 않아서 생긴 문제
   * 해결: `ConcurrentLinkedQueue` 도입
     * 초기 요청으로 전체 API 데이터 범위를 알아낸 후, 이를 통해 `Range`를 생성하여 `ConcurrentLinkedQueue`에 저장
     * 각 스레드는 작업 시작 시 Queue에서 Range를 하나씩 가져와(`poll()`) 해당 범위에 대한 API 요청을 수행하도록 수정하여 해결

## 2) Batch 처리 속도 향상을 위한 `JDBC Template` 도입

  * 배경
    * 초기에는 `JPA`의 `saveAll()` 메서드를 사용하여 Bulk Insert/Update하는 방식 고려
    * 그러나 실행해보니 <u>속도가 느리고</u>, 콘솔에서 **Bulk Insert/Update가 이루어지지 않음**을 확인
  * 원인
    * JPA의 @Id에 auto_increment 속성 사용 시, Bulk Insert/Update가 제대로 작동하지 않는다는 사실 인지
  * 해결: `JDBC Template` 도입
    * JDBC Template으로 여러 SQL 쿼리를 한 번에 묶어서 실행하는 것을 보장하기로 결정
    * 데이터베이스와의 통신 횟수를 줄이고 성능 향상