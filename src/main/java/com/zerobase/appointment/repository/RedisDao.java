package com.zerobase.appointment.repository;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisDao {

  private final StringRedisTemplate redisTemplate;

  private final long EMAIL_VERIFICATION_LIMIT_IN_SECONDS = 60 * 15;

  public void saveAuthCode(String email, String AuthCode) {
    redisTemplate.opsForValue()
        .set(email, AuthCode,
            Duration.ofSeconds(EMAIL_VERIFICATION_LIMIT_IN_SECONDS));
  }

  public String getAuthCode(String email) {
    return redisTemplate.opsForValue().get(email);
  }

  public void removeAuthCode(String email) {

    redisTemplate.delete(email);
  }

  public boolean hasKey(String email) {
    Boolean keyExists = redisTemplate.hasKey(email);
    return keyExists != null && keyExists;
  }

  public long getExpirationTimeMillis(String email) {
    Long ttl = redisTemplate.getExpire(email, TimeUnit.SECONDS);
    if (ttl != null && ttl > 0) {
      long currentTimeMillis = System.currentTimeMillis();
      long expirationTimeMillis = currentTimeMillis + TimeUnit.SECONDS.toMillis(ttl);
      return expirationTimeMillis;
    } else {
      throw new RuntimeException("인증 코드가 만료되었거나 존재하지 않습니다.");
    }
  }
}
