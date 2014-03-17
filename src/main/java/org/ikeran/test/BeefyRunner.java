package org.ikeran.test;

import java.lang.reflect.Method;
import org.ikeran.test.internal.TestClass;
import org.ikeran.test.internal.TestExtractor;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

/**
 *
 * @author dhasenan
 */
public class BeefyRunner extends Runner {
    protected final Class<?> clazz;
    protected Method before, after, beforeClass, afterClass;
	private final TestExtractor extractor;
	private TestClass tests;

    public BeefyRunner(Class<?> clazz) {
        this.clazz = clazz;
		this.extractor = new TestExtractor();
    }
    
    protected TestClass getTests() {
	    if (tests == null) {
		    tests = extractor.findTests(clazz);
	    }
	    return tests;
    }
    
    @Override
    public Description getDescription() {
	    return getTests().getDescription();
    }

    @Override
    public void run(RunNotifier rn) {
	    TestClass testClass = getTests();
	    testClass.run(rn);
    }
}
