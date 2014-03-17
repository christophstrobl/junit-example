package org.ikeran.test.internal;

import java.lang.reflect.InvocationTargetException;
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
public class TestMethod {
    public final Method method;
    public Class<?> expectedException;
    public long maxElapsedMilliseconds = Integer.MAX_VALUE;
	public boolean ignore;
    public List<Test> tests = new ArrayList<Test>();

	/** When we were filling in the details about this test method, we caught this exception.
	 * Later, when we try running this, we'll use this stored exception to report a sensible
	 * error to junit. This is a bit circuitous, but it lets us build up our model in advance,
	 * before we have access to a RunNotifier. */
	public Exception thrownWhenFilling;

	private final TestClass parent;

	public TestMethod(TestClass parent, Method method) {
		this.parent = parent;
		this.method = method;
	}

    private Description description;

    public Description getDescription() {
        if (description == null) {
            description = Description.createTestDescription(parent.classUnderTest, method.getName());
        }
        for (Test test : tests) {
            description.addChild(test.getDescription());
        }
        return description;
    }

	void run(Object underTest, RunNotifier notifier) {
		for (Test test : tests) {
			Description d = test.getDescription();
			System.out.printf("starting test %s (class: %s method: %s)\n", d, parent.classUnderTest.getSimpleName(), method.getName());
			notifier.fireTestStarted(d);
			if (thrownWhenFilling != null) {
				System.out.printf("test doomed before it started: %s\n", thrownWhenFilling);
				notifier.fireTestFailure(new Failure(d, thrownWhenFilling));
				notifier.fireTestFinished(d);
				continue;
			}
			Exception e = parent.tryInvoke(underTest, parent.before);
			if (e != null) {
				notifier.fireTestFailure(new Failure(d, e));
				continue;
			}

			Object[] args = test.parameters;
			Throwable thrown = null;
			long before = System.currentTimeMillis();
			long after;
			try {
				method.invoke(underTest, args);
				after = System.currentTimeMillis();
			} catch (AssertionError err) {
				after = System.currentTimeMillis();
				notifier.fireTestAssumptionFailed(new Failure(d, err));
				continue;
			} catch (InvocationTargetException ite) {
				thrown = ite.getCause();
				after = System.currentTimeMillis();
			} catch (Exception ex) {
				// TODO signal internal failure or bad arguments
				notifier.fireTestFailure(new Failure(d, ex));
				continue;
			}

			if (thrown != null) {
				if (expectedException == null || !expectedException.isAssignableFrom(thrown.getClass())) {
					System.out.printf("expected exception: %s actual: %s\n", expectedException, thrown.getClass());
					notifier.fireTestFailure(new Failure(d, thrown));
					System.out.printf("failed test %s\n", d);
					continue;
				}
			} else if (expectedException != null) {
				notifier.fireTestFailure(new Failure(d, new ExpectedExceptionNotThrownException(expectedException)));
			}

			long diff = after - before;
			// It might be the case that we should add a margin for clock inaccuracies at fine granularity.
			// But I'd rather leave that in the hands of the users.
			if (diff > maxElapsedMilliseconds) {
				notifier.fireTestFailure(new Failure(d, new TestSLAExceededException(diff, maxElapsedMilliseconds)));
			}

			// TODO should I invoke @After with a failed test?
			e = parent.tryInvoke(underTest, parent.after);
			if (e != null) {
				notifier.fireTestFailure(new Failure(d, e));
				continue;
			}
			notifier.fireTestFinished(d);
			System.out.printf("finished test %s\n", d);
		}
	}
}
