package org.example.aspect;

import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.IbatisTest;
import org.ibatis.mybatis.utils.DBEnv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
public class IbatisAspect {
    public static Logger log = LoggerFactory.getLogger(IbatisAspect.class);
    public static Logger log1 = LoggerFactory.getLogger(Enhancer.class);
    public static Logger log12 = LoggerFactory.getLogger(InvocationHandler.class);
    public static SqlMapClientImpl sqlMapClientSlave = IbatisTest.init(IbatisTest.resource, DBEnv.SlavesOne.Driver, DBEnv.SlavesOne.Url, DBEnv.SlavesOne.UserName, DBEnv.SlavesOne.Password);
    public static Map<String, Method> RelectedMethodWithArgTsCache = new ConcurrentHashMap();


    // 定义一个切点，匹配所有实现了InterfaceA接口方法的类
//    @Pointcut("execution(* com.ibatis.sqlmap.client.SqlMapExecutor+.insert(..))"
//            + "||"
//            + "execution(* com.ibatis.sqlmap.client.SqlMapExecutor+.update(..))" + "||"
//            + "execution(* com.ibatis.sqlmap.client.SqlMapExecutor+.delete(..))" + "||"
//            + "execution(* com.ibatis.sqlmap.client.SqlMapExecutor+.queryForObject(..))" + "||"
//            + "execution(* com.ibatis.sqlmap.client.SqlMapExecutor+.queryForList(..))" + "||"
//            + "execution(* com.ibatis.sqlmap.client.SqlMapExecutor+.queryWithRowHandler(..))" + "||"
//            + "execution(* com.ibatis.sqlmap.client.SqlMapExecutor+.queryForPaginatedList(..))" + "||"
//            + "execution(* com.ibatis.sqlmap.client.SqlMapExecutor+.queryForMap(..))"+ "||"
//            + "execution(* com.ibatis.sqlmap.client.SqlMapExecutor+.startBatch(..))"+ "||"
//            + "execution(* com.ibatis.sqlmap.client.SqlMapExecutor+.executeBatch(..))"+ "||"
//            + "execution(* com.ibatis.sqlmap.client.SqlMapExecutor+.executeBatchDetailed(..))"
//    )
    @Pointcut("execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.insert(..))"
            + "||"
            + "execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.update(..))" + "||"
            + "execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.delete(..))" + "||"
            + "execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.queryForObject(..))" + "||"
            + "execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.queryForList(..))" + "||"
            + "execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.queryWithRowHandler(..))" + "||"
            + "execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.queryForPaginatedList(..))" + "||"
            + "execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.queryForMap(..))" + "||"
            + "execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.startBatch(..))" + "||"
            + "execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.executeBatch(..))" + "||"
            + "execution(* com.ibatis.sqlmap.engine.impl.SqlMapClientImpl.executeBatchDetailed(..))"
    )
    public void methodsOnInterfaceA() {

    }

    // 在此切点上应用通知，比如一个前置通知
    @Around("methodsOnInterfaceA()")
    public Object AroundMethodExecution(ProceedingJoinPoint pjp) throws Throwable {
        if (sqlMapClientSlave != pjp.getTarget()) {
            String m = pjp.getSignature().getName();
            String as = "#";
            for (Object arg : pjp.getArgs()) {
                as += arg.getClass() + "&";
            }
            Method currentSlave = null;
            synchronized (RelectedMethodWithArgTsCache) {
                if (RelectedMethodWithArgTsCache.containsKey(m + as)) {
                    currentSlave = RelectedMethodWithArgTsCache.get(m + as);
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
                    RelectedMethodWithArgTsCache.put(m + as, currentSlave);
                }
            }

            log.info("before " + m);
            Object r = pjp.proceed(pjp.getArgs());
            log.info("after " + m);
            log.info("before Slave " + m);
            try {
                Object sr = currentSlave.invoke(sqlMapClientSlave, pjp.getArgs());
                log.info("before Slave " + m + "====" + sr);

            } catch (Throwable e) {
                e.printStackTrace();
            }
            log.info("after Slave" + m);
            return r;
        } else {
            return pjp.proceed(pjp.getArgs());
        }
    }
}