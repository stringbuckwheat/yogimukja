package com.memil.yogimukja.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestaurantReclassificationTasklet implements Tasklet {
    private final JdbcTemplate jdbcTemplate;

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String sql = """
                UPDATE restaurant
                SET type = CASE
                    WHEN name LIKE '%카페%' OR name LIKE '%까페%' OR name LIKE '%커피%' OR name LIKE '%베이크%' OR 
                         name LIKE '%투썸플레이스%' OR name LIKE '%디저트%' OR name LIKE '%할리스%' OR 
                         name LIKE '%빙수%' OR name LIKE '%탕후루%' OR name LIKE '%도너츠%' OR 
                         name LIKE '%도넛%' OR name LIKE '%더치앤빈%' OR name LIKE '%노티드%' OR 
                         name LIKE '%젤라또%' OR name LIKE '%파스쿠찌%' OR name LIKE '%베이커리%' OR 
                         name LIKE '%베이글%' OR name LIKE '%요거트%' OR name LIKE '%공차%' OR 
                         name LIKE '%케이크%' OR name LIKE '%쥬씨%' OR name LIKE '%바나프레소%' OR 
                         name LIKE '%와플%' OR name LIKE '%빽다방%' OR name LIKE '%버블티%' OR 
                         name LIKE '%케잌%' OR name LIKE '%cafe%' OR name LIKE '%CAFE%' OR 
                         name LIKE '%폴 바셋%' OR name LIKE '%아이스크림%' OR name LIKE '%더벤티%' OR 
                         name LIKE '%로네펠트%' OR name LIKE '%츄러스%' OR name LIKE '%호떡%' OR 
                         name LIKE '%요거프레소%' OR name LIKE '%샌드위치%' OR name LIKE '%붕어빵%' OR 
                         name LIKE '%엔젤리너스%' OR name LIKE '%매머드 익스프레스%' 
                    THEN '카페/디저트'
                    
                    WHEN name LIKE '%양꼬치%' OR name LIKE '%마라탕%' OR name LIKE '%짬뽕%' OR 
                         name LIKE '%짜장%' OR name LIKE '%란콰이펑%' 
                    THEN '중식'
                    
                    WHEN name LIKE '%어시장%' OR name LIKE '%수산%' OR name LIKE '%돈까스%' OR
                         name LIKE '%돈가스%' OR name LIKE '%초밥%'
                    THEN '돈까스/회/일식'
                    
                    WHEN name LIKE '%피자%' OR name LIKE '%피제리아%' OR name LIKE '%파파존스%' 
                    THEN '피자'
                    
                    WHEN name LIKE '%타코%' OR name LIKE '%쌀국수%' OR name LIKE '%우육면%' OR 
                         name LIKE '%포메인%' OR name LIKE '%탄탄면%' OR name LIKE '%쟈니덤플링%' OR 
                         name LIKE '%케밥%' OR name LIKE '%분짜%' 
                    THEN '세계음식'
                    
                    WHEN name LIKE '%KFC%' OR name LIKE '%맘스터치%' OR name LIKE '%버거%' OR 
                         name LIKE '%이삭토스트%' OR name LIKE '%케이에프씨%' OR name LIKE '%써브웨이%' OR 
                         name LIKE '%핫도그%' OR name LIKE '%에그박스%' OR name LIKE '%퀴즈노스%'
                    THEN '패스트푸드'
                    
                    WHEN name LIKE '%치킨%' OR name LIKE '%통닭%' OR name LIKE '%닭강정%' OR 
                         name LIKE '%가마로%' OR name LIKE '%누나홀닭%' OR name LIKE '%푸라닭%' OR 
                         name LIKE '%지코바%' OR name LIKE '%BHC%' OR name LIKE '%순살만%' OR 
                         name LIKE '%BBQ%' 
                    THEN '치킨'
                    
                    WHEN name LIKE '%포차%' OR name LIKE '%맥주%' OR name LIKE '%서울브루어리%' OR 
                         name LIKE '%노가리%' OR name LIKE '%위스키%' OR name LIKE '%투다리%' OR 
                         name LIKE '%와인%' OR name LIKE '%펀 비어킹%' OR name LIKE '%포장마차%' OR 
                         name LIKE '%주점%' 
                    THEN '술집'
                    
                    WHEN name LIKE '%파스타%' THEN '양식'
                    
                    WHEN name LIKE '%곱창%' OR name LIKE '%본죽%' OR name LIKE '%부안집%' OR 
                         name LIKE '%삼겹%' OR name LIKE '%쭈꾸미%' OR name LIKE '%족발%' OR 
                         name LIKE '%꼬막%' OR name LIKE '%순대국%' OR name LIKE '%고기집%' OR 
                         name LIKE '%김치%' OR name LIKE '%추어탕%' OR name LIKE '%갈비찜%' OR 
                         name LIKE '%찌개%' OR name LIKE '%채선당%' OR name LIKE '%감자탕%' OR 
                         name LIKE '%도시락%' OR name LIKE '%고깃집%' OR name LIKE '%백반%' OR 
                         name LIKE '%해장국%' OR name LIKE '%갈비%' OR name LIKE '%보쌈%' OR 
                         name LIKE '%국밥%' OR name LIKE '%꼼장어%' OR name LIKE '%코다리%' OR 
                         name LIKE '%수제비%' OR name LIKE '%골뱅이%' OR name LIKE '%두부%' OR 
                         name LIKE '%냉면%' OR name LIKE '%수제비%' OR name LIKE '%빈대떡%' OR 
                         name LIKE '%닭한마리%' OR name LIKE '%칼국수%' OR name LIKE '%닭발%' OR 
                         name LIKE '%찜닭%' OR name LIKE '%이베리코%' OR name LIKE '%육회%' OR 
                         name LIKE '%한우%' OR name LIKE '%생선구이%' OR name LIKE '%순대%' OR 
                         name LIKE '%보리밥%' OR name LIKE '%멸치국수%' OR name LIKE '%멸치 국수%' OR 
                         name LIKE '%동래집%' OR name LIKE '%풍천장어%' OR name LIKE '%동막골%' OR 
                         name LIKE '%낙지%' OR name LIKE '%막창%' OR name LIKE '%비빔밥%' OR 
                         name LIKE '%고기%' OR name LIKE '%대창%' OR name LIKE '%구이%' OR 
                         name LIKE '%내가찜한닭%' OR name LIKE '%도리탕%' 
                    THEN '한식'
                    
                    WHEN name LIKE '%떡볶이%' OR name LIKE '%김밥%' OR name LIKE '%청년다방%' OR 
                         name LIKE '%닭꼬치%' OR name LIKE '%치즈밥%' 
                    THEN '분식'
                    ELSE type
                END
                WHERE type = '기타';
                """;

        jdbcTemplate.update(sql);

        return RepeatStatus.FINISHED;
    }
}

