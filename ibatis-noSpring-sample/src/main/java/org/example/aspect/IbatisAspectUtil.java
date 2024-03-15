package org.example.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

public class IbatisAspectUtil {
    public static Logger log = LoggerFactory.getLogger(IbatisAspectUtil.class);

    public static Method cacheMethod(Map<String, Method> RelectedMethodWithArgTsCache, ProceedingJoinPoint pjp, String mk) throws Throwable {
        Method currentSlave = null;
        synchronized (RelectedMethodWithArgTsCache) {
            if (RelectedMethodWithArgTsCache.containsKey(mk)) {
                currentSlave = RelectedMethodWithArgTsCache.get(mk);
            } else {
                // 获取执行方法的签名
                Signature signature = pjp.getSignature();
                if (!(signature instanceof MethodSignature)) {
                    throw new IllegalStateException("Not a method execution join point");
                }

                // 转换为MethodSignature并获取Method实例
                MethodSignature methodSignature = (MethodSignature) signature;
                Method method = methodSignature.getMethod();

                // 获取方法所在类的Class对象
                Class<?> targetClass = pjp.getTarget().getClass();

                // 手动反射调用（这里仅作演示，实际在环绕通知中直接调用joinPoint.proceed()即可）
                if (method.getDeclaringClass() != targetClass) {
                    // 处理如父类方法的情况，获取实际声明该方法的Class
                    method = targetClass.getMethod(method.getName(), method.getParameterTypes());
                }

                // 可能需要设置访问权限
                method.setAccessible(true);
                currentSlave = method;
                // 假设我们要模拟调用这个方法（通常在环绕通知中无需这样做，此处仅为演示）
//            Object result = method.invoke(pjp.getTarget(), joinPoint.getArgs());
                RelectedMethodWithArgTsCache.put(mk, currentSlave);
            }
        }
        return currentSlave;

    }
}