package com.wyf.vfs.impl;

import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

/**
 * @author wangyaofeng
 * @projectName vfs
 * @description:  session threadlocal
 * @date 2019/6/5 15:14
 */
public class TransactionContext {

    private static final ThreadLocal<Object> transactionContext=new NamedThreadLocal<>("current session");

    private TransactionContext() {
    }

    public static Object currentSession() throws IllegalStateException {
        return transactionContext.get();
    }

    @Nullable
    public static Object setCurrentSession(@Nullable Object proxy) {
        Object old = transactionContext.get();
        if (proxy != null) {
            transactionContext.set(proxy);
        } else {
            transactionContext.remove();
        }
        return old;
    }

}
