package org.example.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.aspect.dual.common.DBOptDualContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

public class IbatisAspectThreadCache {
    public static Logger log = LoggerFactory.getLogger(IbatisAspectThreadCache.class);
    private static ThreadLocal<DBOptDualContext> bindDual = new ThreadLocal<>();
    private void cleanDualContext() {
        if (bindDual.get() != null) {
            bindDual.get().syncTxRollbacl();
        }
    }

    private void exeAndCommitDualContext() {
        if (bindDual.get() != null) {
            bindDual.get().syncTxCommit();
        }
    }

    private void creatDualContext() {
        if (bindDual.get() != null) {
            bindDual.remove();
        }
        DBOptDualContext dualCtx = new DBOptDualContext();
        bindDual.set(dualCtx);
    }
    
}