package org.ikeran.test.internal;

import java.util.Collection;
import org.junit.runner.notification.RunNotifier;

/**
 *
 * @author dhasenan
 */
public class RunSomeTests {
    public void run(Collection<TestMethod> methods, RunNotifier notifier) {
        for (TestMethod method : methods) {
            for (Test test : method.tests) {
                
            }
        }
    }
}
