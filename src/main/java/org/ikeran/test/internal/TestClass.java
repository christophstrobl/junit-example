package org.ikeran.test.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

/**
 *
 * @author dhasenan
 */
public class TestClass {

	public final Class<?> classUnderTest;
	public Method before, after, beforeClass, afterClass;
	public List<TestMethod> tests = new ArrayList<TestMethod>();
	private Description description;

	public TestClass(Class<?> classUnderTest) {
		this.classUnderTest = classUnderTest;
	}

	public Description getDescription() {
		if (description == null) {
			description = Description.createSuiteDescription(classUnderTest);
			for (TestMethod method : tests) {
				description.addChild(method.getDescription());
			}
		}
		return description;
	}

	public void run(RunNotifier notifier) {
		Object underTest = tryCreate();
		Exception ex = tryInvoke(underTest, beforeClass);
		if (ex != null) {
			notifier.fireTestFailure(new Failure(description, ex));
		}
		for (TestMethod method : tests) {
			method.run(underTest, notifier);
		}
		ex = tryInvoke(underTest, afterClass);
		if (ex != null) {
			notifier.fireTestFailure(new Failure(description, ex));
		}
	}

	private Object tryCreate() {
		try {
			return classUnderTest.newInstance();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public Exception tryInvoke(Object o, Method method) {
		if (method != null) {
			try {
				method.invoke(o);
			} catch (Exception ex) {
				return ex;
			}
		}
		return null;
	}
}
