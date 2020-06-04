package hello;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Aspect
@Configuration
public class CacheAspect {
    // Map<String, Object> cache = new HashMap<>();

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(hello.anno.Cache)")
    public Object cache(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        Object cachedValue = redisTemplate.opsForValue().get(methodName);

        if (cachedValue != null) {
            System.out.println("Get value from cache!");
            System.out.println(cachedValue);
            return cachedValue;
        } else {
            System.out.println("Get value from database!");
            Object realValue = joinPoint.proceed();
            redisTemplate.opsForValue().set(methodName, realValue);
            return realValue;
        }
    }
}
