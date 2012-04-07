package net.trivial.wf;

import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.trivial.wf.iface.Action;
import net.trivial.wf.iface.Message;


public class Workflow {

	private final static String DELIM = ",";
	private final static String WORKFLOW_STATES = "workflow.states"; 
	private final static String WORKFLOW_TRANSITION = "workflow.transition.";
	private final static String WORKFLOW_TRANSITION_KEYS = "workflow.transition.keys.";
	private final static String WORKFLOW_STATE_ACTION="workflow.transition.action.";
	private final static String WORKFLOW_STATE_ACTION_ARGS=WORKFLOW_STATE_ACTION + "args.";
	private static final String WORKFLOW_MODEL = "workflow.model";
	
	private final StateMachine<String, String> workflow = new StateMachine<String, String>();
	private final Map<String, Action> actions = new HashMap<String, Action>();
	
	private final String className;
	private final String initialState;
	
	public Workflow(String fileName) throws Exception {

		Properties properties = new Properties();

		properties.load(new FileInputStream(fileName));

		className = properties.getProperty(WORKFLOW_MODEL);
		Class.forName(className).getConstructor(String.class);
		
		/* populate states */
		String states = properties.getProperty(WORKFLOW_STATES);
		List<String> stateList = new LinkedList<String>(Arrays.asList(states.split(DELIM)));

		if (stateList.size() < 2) {
			throw new Exception("At least two states should be specified in " + WORKFLOW_STATES);
		}

		initialState = stateList.get(0);
		
		/* getting the transitions, except for the last, ending state */
		for (int i = 0; i < stateList.size() - 1; i++) {

			Action action = null;
			
			String startState = stateList.get(i);
			String stateAction = properties.getProperty(WORKFLOW_STATE_ACTION + startState);

			String actionArgs = properties.getProperty(WORKFLOW_STATE_ACTION_ARGS + startState);
			
			if (actionArgs != null) {
				
				String[] args = null;
				
				if(actionArgs.contains(DELIM)) {
					args = actionArgs.split(DELIM);
				}else{
					args = new String[] { actionArgs };
				}
				
				final Class<?>[] classArgTypes = { String[].class };
				final Object[] objectArgs = { args };

				Constructor<?> c = Class.forName(stateAction).getConstructor(classArgTypes);
				action = ((Action) c.newInstance(objectArgs));
			} else {
				
				Constructor<?> c = Class.forName(stateAction).getConstructor();
				action = ((Action) c.newInstance());
			}

			String keys = properties.getProperty(WORKFLOW_TRANSITION_KEYS + startState);
			String[] keyArray = keys.split(DELIM);

			for (String key : keyArray) {

				String endState = properties.getProperty(WORKFLOW_TRANSITION + key + "." + startState);

				if (!stateList.contains(endState)) {

					throw new Exception("Destination state " + endState
							+ " for key " + WORKFLOW_TRANSITION + key + "."
							+ startState
							+ " was not specified in initial state list");
				}

				workflow.put(startState, key, endState);
				actions.put(startState, action);
			}
		}
	}
	
	public Object newModelInstance() 
		throws NoSuchMethodException,
			SecurityException, ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Constructor<?> c = Class.forName(className).getConstructor(String.class);
		return c.newInstance(initialState);
	}
	
	public void doProcess(Message object, Object... args) throws Exception {

		String s = object.getState();

		Action p = actions.get(s);

		if(p == null) throw new Exception("Action not found for state " + s);
		
		String t = p.execute(object, args);

		String ns = workflow.get(s, t);

		if (ns == null) return;

		object.setState(ns);

	}
}