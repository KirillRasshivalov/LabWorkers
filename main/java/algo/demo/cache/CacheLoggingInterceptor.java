package algo.demo.cache;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

@Interceptor
@CacheLogging
public class CacheLoggingInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(CacheLoggingInterceptor.class);

    private static boolean enabled = false;

    @AroundInvoke
    public Object logCacheStatistics(InvocationContext context) throws Exception {
        Object result = context.proceed();

        if (enabled) {
            Method method = context.getMethod();
            String className = method.getDeclaringClass().getSimpleName();
            String methodName = method.getName();
            logger.info("Статистика по кэшу для " + className + "." + methodName + "() =" );
        }
        return result;
    }

    public static void enable() {
        enabled = true;
        logger.info("Кэширование включено.");
    }

    public static void disable() {
        enabled = false;
        logger.info("Кэширование выключено.");
    }
}
