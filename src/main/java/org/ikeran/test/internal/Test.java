package org.ikeran.test.internal;

import org.junit.runner.Description;

/**
 *
 * @author dhasenan
 */
public class Test {
    public Object[] parameters;
    public final TestMethod parent;

    public Test(TestMethod parent) {
        this.parent = parent;
    }

    private Description description;

    public Description getDescription() {
        if (description == null) {
			String name;
			if (parameters == null || parameters.length == 0) {
				name = parent.method.getName();
			} else {
				name = String.format("%s%s", parent.method.getName(), Formatter.format(parameters));
			}
            description = Description.createSuiteDescription(name);
        }
        return description;
    }
}
