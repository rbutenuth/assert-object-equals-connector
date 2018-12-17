package de.codecentric.mule.assertobjectequals;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimpleMock<T> implements InvocationHandler {
    private Class<T> clazz;
    private T mock;
    private List<ResultEntry> results;

    @SuppressWarnings("unchecked")
    public SimpleMock(Class<T> clazz) {
        this.clazz = clazz;
        mock = (T) Proxy.newProxyInstance(SimpleMock.class.getClassLoader(), new Class<?>[] { clazz }, this);
        results = new ArrayList<>();
    }

    public void storeResult(Object result, String method, Object... args) {
        results.add(new ResultEntry(method, args, result));
    }

    public T getMockObject() {
        return mock;
    }

    @Override
    public Object invoke(@SuppressWarnings("unused") Object proxy, Method method, Object[] args) throws Throwable {
        for (ResultEntry e : results) {
            if (e.matches(method, args)) {
                return e.result;
            }
        }
        StringBuilder sb = new StringBuilder("Call to ");
        sb.append(clazz.getName()).append(".");
        sb.append(method.getName()).append("(");
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg == null) {
                    sb.append("null");
                } else if (arg instanceof String) {
                    sb.append('"').append(arg).append('"');
                } else {
                    sb.append(arg.toString());
                }
                if (i < args.length - 1) {
                    sb.append(", ");
                }
            }
        }
        sb.append(") not implemented");
        throw new UnsupportedOperationException(sb.toString());
    }

    private static class ResultEntry {
        private final String method;
        private final Object[] arguments;
        private final Object result;

        public ResultEntry(String method, Object[] arguments, Object result) {
            this.method = method;
            this.arguments = arguments == null ? new Object[0] : arguments.clone();
            this.result = result;
        }

        public boolean matches(Method calledMethod, Object[] methodArguments) {
            if (!method.equals(calledMethod.getName())) {
                return false;
            }
            Object[] methodArgs = methodArguments == null ? new Object[0] : methodArguments;
            if (arguments.length != methodArgs.length) {
                return false;
            }
            for (int i = 0; i < arguments.length; i++) {
                if (!Objects.equals(arguments[i], methodArgs[i])) {
                    return false;
                }
            }
            return true;
        }
    }
}
