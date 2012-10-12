package nl.runnable.alfresco.actions;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;

public class GenericCollectionTypeExample {

	public void test(final List<List<String>> strs) {
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		ReflectionUtils.doWithMethods(GenericCollectionTypeExample.class, new MethodCallback() {

			@Override
			public void doWith(final Method method) throws IllegalArgumentException, IllegalAccessException {
				if (method.getName().equals("test")) {
					final ParameterizedType paramType = (ParameterizedType) method.getGenericParameterTypes()[0];
					final Type argType = paramType.getActualTypeArguments()[0];
					if (argType instanceof Class<?>) {
						final Class<?> cls = (Class<?>) argType;
						System.out.println(cls.getName());
					}
				}
			}
		});
	}

}
