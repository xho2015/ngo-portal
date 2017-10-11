package ngo.front.common.service;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class ContextHolder implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		applicationContext = context;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		checkApplicationContext();
		return (T) applicationContext.getBean(name);
	}

	private static void checkApplicationContext() {
		if (applicationContext == null) {
			throw new IllegalStateException("applicaitonContext not injected, please define ContextHolder from applicationContext.xml");
		}
	}

}
