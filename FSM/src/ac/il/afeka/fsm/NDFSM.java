package ac.il.afeka.fsm;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

public class NDFSM {

	protected TransitionMapping transitions;
	protected Set<State> states;
	protected Set<State> acceptingStates;
	protected State initialState;
	protected Alphabet alphabet;
	
	/**
	 * Builds a NDFSM from a string representation (encoding) 
	 *  
	 * @param encoding	the string representation of a NDFSM
	 * @throws Exception if the encoding is incorrect or if the transitions contain invalid states or symbols
	 */
	public NDFSM(String encoding) throws Exception {
		parse(encoding);
		
		transitions.verify(states,alphabet);
	}
	
	/**
	 * Build a NDFSM from its components
	 * 
	 * @param states			the set of states for this machine
	 * @param alphabet			this machine's alphabet
	 * @param transitions		the transition mapping of this machine
	 * @param initialState		the initial state (must be a member of states)
	 * @param acceptingStates	the set of accepting states (must be a subset of states)
	 * @throws Exception if the components do not represent a valid non deterministic machine
	 */
	public NDFSM(Set<State> states, Alphabet alphabet, Set<Transition> transitions, State initialState,
			Set<State> acceptingStates) throws Exception {
		
		initializeFrom(states, alphabet, transitions, initialState, acceptingStates);
		this.transitions.verify(this.states,alphabet);
	}

	protected void initializeFrom(Set<State> states, Alphabet alphabet, Set<Transition> transitions, State initialState, Set<State> acceptingStates) {

		this.states = states;
		this.alphabet = alphabet;
		this.transitions = createMapping(transitions);
		this.initialState = initialState;
		this.acceptingStates = acceptingStates;
	}

	protected NDFSM() { }
	
	/** Overrides this machine with the machine encoded in string.
	 * 
	 *  <p>Here's an example of the encoding:</p>
	 <pre>
	0 1/a b/0 , a , 0; 0,b, 1 ;1, a, 0 ; 1, b, 1/0/ 1
	</pre>
	<p>This is the encoding of a finite state machine with two states (identified as 0 and 1), 
	an alphabet that consists of the two characters 'a' and 'b', and four transitions:</p>
	<ol>
	<li>From state 0 on character a it moves to state 0</li>
	<li>from state 0 on character b it moves to state 1,</li>
	<li>from state 1 on character a it moves to state 0,</li>
	<li>from state 1 on character b it moves to state 1.</li>
	</ol>
	<p>The initial state of this machine is 0, and the set of accepting states consists of 
	just one state 1. Here is the format in general:</p>
	  
	 <pre>
	 {@code
	<states> / <alphabet> / <transitions> / <initial state> / <accepting states>
	}
	</pre>
	
	where:
	
	<pre>
	{@code
	<alphabet> is <char> <char> ...
	
	<transitions> is <transition> ; <transition> ...
	
	<transition> is from , char, to
	
	<initial state> is an integer
	
	<accepting states> is <state> <state> ...
	
	<state> is an integer
	}
	</pre>
	
	@param string the string encoding 
	@throws Exception if the string encoding is invalid
	*/
	public void parse(String string) throws Exception {
		
		Scanner scanner = new Scanner(string);
		
		scanner.useDelimiter("\\s*/");	
		
		Map<Integer, State> states = new HashMap<Integer, State>();
		
		for(Integer stateId : IdentifiedState.parseStateIdList(scanner.next())) {
			states.put(stateId, new IdentifiedState(stateId));
		}

		Alphabet alphabet = Alphabet.parse(scanner.next());
		
		Set<Transition> transitions = new HashSet<Transition>();
		
		for (TransitionTuple t: TransitionTuple.parseTupleList(scanner.next())) {
			transitions.add(new Transition(states.get(t.fromStateId()), t.symbol(), states.get(t.toStateId())));
		}
		
		State initialState = states.get(scanner.nextInt());
		
		Set<State> acceptingStates = new HashSet<State>();

		if (scanner.hasNext())
			for(Integer stateId : IdentifiedState.parseStateIdList(scanner.next())) {
				acceptingStates.add(states.get(stateId));
			}
		
		scanner.close();
		
		initializeFrom(new HashSet<State>(states.values()), alphabet, transitions, initialState, acceptingStates);
		this.transitions.verify(this.states, alphabet);
	}

	protected TransitionMapping createMapping(Set<Transition> transitions) {
		return new TransitionRelation(transitions);
	}
		
	/** Returns a version of this state machine with all the unreachable states removed.
	 * 
	 * @return NDFSM that recognizes the same language as this machine, but has no unreachable states.
	 */
	public NDFSM removeUnreachableStates() {

		Set<State> reachableStates = reachableStates();

		Set<Transition> transitionsToReachableStates = new HashSet<Transition>();
		
		for(Transition t : transitions.transitions()) {
			if (reachableStates.contains(t.fromState()) && reachableStates.contains(t.toState()))
				transitionsToReachableStates.add(t);
		}
		
		Set<State> reachableAcceptingStates = new HashSet<State>();
		for(State s : acceptingStates) {
			if (reachableStates.contains(s))
				reachableAcceptingStates.add(s);
		}
		
		NDFSM aNDFSM = (NDFSM)create();
		
		aNDFSM.initializeFrom(reachableStates, alphabet, transitionsToReachableStates, initialState, reachableAcceptingStates);
		
		return aNDFSM;
	}

	protected NDFSM create() {
		return new NDFSM();
	}

	// returns a set of all states that are reachable from the initial state
	
	private Set<State> reachableStates() {
		
		List<Character> symbols = new ArrayList<Character>();
		
		symbols.add(Alphabet.EPSILON);
		
		for(Character c : alphabet) {
			symbols.add(c);
		}
		
		Alphabet alphabetWithEpsilon = new Alphabet(symbols);
		
		Set<State> reachable = new HashSet<State>();

		Set<State> newlyReachable = new HashSet<State>();

		newlyReachable.add(initialState);

		while(!newlyReachable.isEmpty()) {
			reachable.addAll(newlyReachable);
			newlyReachable = new HashSet<State>();
			for(State state : reachable) {
				for(Character symbol : alphabetWithEpsilon) {
					for(State s : transitions.at(state, symbol)) {
						if (!reachable.contains(s))
							newlyReachable.add(s);
					}
				}
			}
		}
		
		return reachable;
	}

	/** Encodes this state machine as a string
	 * 
	 * @return the string encoding of this state machine
	 */
	public String encode() {
		return  State.encodeStateSet(states) + "/" +
				alphabet.encode() + "/" + 
				transitions.encode() + "/" + 
				initialState.encode() + "/" +
				State.encodeStateSet(acceptingStates);
	}
	
	/** Prints a set notation description of this machine.
	 * 
	 * <p>To see the Greek symbols on the console in Eclipse, go to Window -&gt; Preferences -&gt; General -&gt; Workspace 
	 * and change <tt>Text file encoding</tt> to <tt>UTF-8</tt>.</p>
	 * 
	 * @param out the output stream on which the description is printed.
	 */
	public void prettyPrint(PrintStream out) {
		out.print("K = ");
		State.prettyPrintStateSet(states, out);
		out.println("");
		
		out.print("\u03A3 = ");
		alphabet.prettyPrint(out);
		out.println("");
		
		out.print(transitions.prettyName() + " = ");
		transitions.prettyPrint(out);
		out.println("");
		
		out.print("s = ");
		initialState.prettyPrint(out);
		out.println("");
		
		out.print("A = ");
		State.prettyPrintStateSet(acceptingStates, out);
		out.println("");		
	}
	
	/** Returns a canonic version of this machine. 

<p>The canonic encoding of two minimal state machines that recognize the same language is identical.</p>

@return a canonic version of this machine. 
*/

	public NDFSM toCanonicForm() {
	
		Set<Character> alphabetAndEpsilon = new HashSet<Character>();
		
		for(Character symbol : alphabet) {
			alphabetAndEpsilon.add(symbol);
		}
		alphabetAndEpsilon.add(Alphabet.EPSILON);
		
		Set<Transition> canonicTransitions = new HashSet<Transition>();
		Stack<State> todo = new Stack<State>();
		Map<State, State> canonicStates = new HashMap<State, State>();
		Integer free = 0;
		
		todo.push(initialState);
		canonicStates.put(initialState, new IdentifiedState(free));
		free++;
		
		while (!todo.isEmpty()) {
			State top = todo.pop();
			for(Character symbol : alphabetAndEpsilon) {
				for(State nextState : transitions.at(top, symbol)) {
					if (!canonicStates.containsKey(nextState)) {
						canonicStates.put(nextState, new IdentifiedState(free));
						todo.push(nextState);
						free++;
					}
					canonicTransitions.add(new Transition(canonicStates.get(top), symbol, canonicStates.get(nextState)));
				}
			}			
		}

		Set<State> canonicAcceptingStates = new HashSet<State>();
		for(State s : acceptingStates) {
			if (canonicStates.containsKey(s)) // unreachable accepting states will not appear in the canonic form of the state machine
				canonicAcceptingStates.add(canonicStates.get(s));
		}
		
		NDFSM aNDFSM = create();
		
		aNDFSM.initializeFrom(new HashSet<State>(canonicStates.values()), alphabet, canonicTransitions, canonicStates.get(initialState), canonicAcceptingStates);

		return aNDFSM;
	}
	
	public boolean compute(String input) throws Exception {
		return toDFSM().compute(input);
	}
	
	
	
	
	/////////// our code /////////////////////////////////
	
	
//	public Set<State> eps(State state) {	
//		//get all states with eps transition
//		Set<State> statesWithEps = new HashSet<>();
//		statesWithEps.addAll(this.transitions.at(state, Alphabet.EPSILON)); 
//		
//		
//		//if from this state no eps transtions return this state only
//		if(statesWithEps.isEmpty()) {  
//			Set<State> states=new HashSet<>();
//			states.add(state);
//			return states;
//		}
//		
//		//go over all states and check eps for them
//		ArrayList<State> helper = new ArrayList<>();// helps to check all states
//		helper.add(state);
//		State cur=null;
//		while(!helper.isEmpty()) {
//			cur= helper.get(0);
//			helper.addAll(this.transitions.at(cur, Alphabet.EPSILON));
//			for (int i = 0; i < helper.size(); i++) {
//				if(!statesWithEps.contains(helper.get(i))) {
//					statesWithEps.add(helper.get(i));
//				}
//			}
//			helper.remove(0);
//		}
//		return statesWithEps;
//	}
//	
	
	public StatesInGroups eps(State state) {	
		//get all states with eps transition
		StatesInGroups statesWithEps = new StatesInGroups(null);
		statesWithEps.getStatesGroup().addAll(this.transitions.at(state, Alphabet.EPSILON)); 
		
		
		//if from this state no eps transtions return this state only
		if(statesWithEps.getStatesGroup().isEmpty()) {  
			Set<State> states=new HashSet<>();
			states.add(state);
			statesWithEps.setStatesGroup(states);
			return statesWithEps;
		}
		
		//go over all states and check eps for them
		ArrayList<State> helper = new ArrayList<>();// helps to check all states
		helper.add(state);
		State cur=null;
		while(!helper.isEmpty()) {
			cur= helper.get(0);
			helper.addAll(this.transitions.at(cur, Alphabet.EPSILON));
			for (int i = 0; i < helper.size(); i++) {
				if(!statesWithEps.getStatesGroup().contains(helper.get(i))) {
					statesWithEps.getStatesGroup().add(helper.get(i));
				}
			}
			helper.remove(0);
		}
		return statesWithEps;
	}
	
	
	
	public Set<State> acceptingStates(Set<State> set) {
		// creating an group of accepting groups of states
		Set<State> newAcceptingStates = new HashSet<>();

		// loop to find all groups that contain an accepting state
		for (State foundSet : set) { // go over all groups of states
			StatesInGroups x = (StatesInGroups)foundSet;
			Set<State> y=x.getStatesGroup();
			for (State state : y) {
				if (acceptingStates.contains(state)) {
					newAcceptingStates.add(foundSet);
				}
			}
		}
		return newAcceptingStates;
	}
	 
	
	
	public DFSM toDFSM() throws Exception {
		//Alphabet:
		Alphabet newAlphabet = this.alphabet;
		
		//S 
		StatesInGroups initialStates = this.eps(this.initialState);
		
		//delta + k
		Set<Transition> delta= new HashSet<>();
		Set<State> k = new HashSet<>(); 
		
		//loop on helper array and find out new states(groups of states) and add transitions to delta
		ArrayList<StatesInGroups> helper= new ArrayList<>();
		helper.add(initialStates);
		k.add(initialStates); //add first state to k
		
		while (!helper.isEmpty()) {
			StatesInGroups fromState = helper.get(0); // from state
			StatesInGroups toStateWithEps = new StatesInGroups(null);// to state
			
			//create the toState group
			for (Character c : this.alphabet) {
				for (State state : fromState.getStatesGroup()) { // go over each state in the current group																			
					// crate the group of toState from state+ c
					Set<State> statesGotFromCurrentStateByTransition = this.transitions.at(state, c); // for each new state need to check where it leads																				
					for (State s : statesGotFromCurrentStateByTransition)
						toStateWithEps.addNewStates(eps(s)); // add new states the current state leads to with eps function										
				}
				//check if add to helper
				boolean dontAddToHelper=false;
				for (StatesInGroups s : helper) { 
					if(s.equals(toStateWithEps))
						dontAddToHelper=true;
				}
				//check if add to k
				boolean dontAddToK=false;
				for (State s : k) { 		
					if(s.equals(toStateWithEps))
						dontAddToK=true;
				}
				//add to helper if the toState group is not already there
				if(!dontAddToK && !dontAddToHelper)
					helper.add(toStateWithEps);
				
				//add to k the new State if not already there
				if(!dontAddToK) 
					k.add(toStateWithEps);
				
				//add to delta the new transition
				delta.add(new Transition(fromState, c, toStateWithEps));
				toStateWithEps = new StatesInGroups(null);
			}
			helper.remove(0);
		}
			
		//A:
		Set<State> newAcceptingStates= this.acceptingStates(k);
		
		
		//here
		return new DFSM(k, newAlphabet, delta, initialStates, newAcceptingStates);
	}
}
