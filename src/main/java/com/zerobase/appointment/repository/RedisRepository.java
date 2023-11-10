package com.zerobase.appointment.repository;

import com.zerobase.appointment.exception.MemberException;
import com.zerobase.appointment.type.ErrorCode;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

  private final StringRedisTemplate redisTemplate;

  private final long EMAIL_VERIFICATION_LIMIT_IN_SECONDS = 60 * 15;

  private String createAuthKey(String email) {
    return "AUTH:" + email;
  }

  public void saveAuthCode(String email, String AuthCode) {
    String key = createAuthKey(email);
    redisTemplate.opsForValue()
        .set(key, AuthCode,
            Duration.ofSeconds(EMAIL_VERIFICATION_LIMIT_IN_SECONDS));
  }

  public String getAuthCode(String email) {
    String key = createAuthKey(email);
    return redisTemplate.opsForValue().get(key);
  }

  public void removeAuthCode(String email) {
    String key = createAuthKey(email);
    redisTemplate.delete(key);
  }

  public boolean hasKey(String email) {
    String key = createAuthKey(email);
    Boolean keyExists = redisTemplate.hasKey(key);
    return Boolean.TRUE.equals(keyExists);
  }

  public long getExpirationTimeMillis(String email) {
    String key = createAuthKey(email);
    Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
    if (ttl==null||ttl == 0 || ttl < 0) {
      throw new MemberException(ErrorCode.AUTHCODE_NOT_FOUND);
    }
    long currentTimeMillis = System.currentTimeMillis();
    return currentTimeMillis + TimeUnit.SECONDS.toMillis(ttl);
  }
}
