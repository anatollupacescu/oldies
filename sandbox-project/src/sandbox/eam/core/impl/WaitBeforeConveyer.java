package sandbox.eam.core.impl;

import sandbox.eam.ExecutionAccessManager;
import sandbox.eam.core.Processor;

public class WaitBeforeConveyer {

	public static void execute(Processor p) throws Exception {

		while (!ExecutionAccessManager.canEnter(p.toString())) {
			p.sleep();
		}
		
		if (!ExecutionAccessManager.allowAsk()) throw new Exception("Inconsistent behaviour");

		try {
			p.process();
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			ExecutionAccessManager.allowEnter(p.toString());
		}
	}
}
