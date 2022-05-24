package com.smrp.smartmedicinealarm.utils;

import com.smrp.smartmedicinealarm.config.EmbeddedRedisConfig;
import com.smrp.smartmedicinealarm.config.RedisConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Import({EmbeddedRedisConfig.class, RedisConfig.class})
@SpringBootTest
@ActiveProfiles("test")
class RedisUtilsTest {
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    StringRedisTemplate redisTemplate;


    @Test
    @DisplayName("레디스 유틸 테스트")
    public void crudeTest(){
        String key = "name";
        String value = "joon";

        //생성
        redisUtils.addData(key, value);

        //조회
        assertThat(redisUtils.getData(key)).isEqualTo(value);

        //업데이트
        redisUtils.addData(key, "update");
        assertThat(redisUtils.getData(key)).isEqualTo("update");

        // 제거
        redisUtils.deleteData(key);

        //검증
        assertThat(redisUtils.getData(key)).isNull();

    }
    @Test
    @DisplayName("레디스 유틸 테스트 타임 아웃 설정")
    public void setDataAndTime() throws InterruptedException {
        //데이터 추가
        redisUtils.addData("name", "joon", Duration.ofSeconds(2));
        //조회
        assertThat(redisUtils.getData("name"))
                .isNotNull().as("데이터 저장 후 2초이내 조회");

        Thread.sleep(2000);
        // 2초 후 검증
        assertThat(redisUtils.getData("name")).as("2 초 후 조회")
                .isNull();

    }
}