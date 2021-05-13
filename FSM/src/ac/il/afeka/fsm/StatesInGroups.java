package ac.il.afeka.fsm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class StatesInGroups extends IdentifiedState {
	private Set<State> statesGroup;
	
	public StatesInGroups(Set<State> statesGroup) {
		super(0);
		if(statesGroup==null) 
			this.statesGroup= new HashSet<State>();
		else {
			this.statesGroup=new HashSet<State>();
			for (State state : statesGroup) {
				this.statesGroup.add(state);
			}
		}
	}

	public Set<State> getStatesGroup() {
		return statesGroup;
	}
	
	
	public boolean isEmptyGroup() {
		return this.statesGroup.isEmpty();
	}

	public void setStatesGroup(Set<State> statesGroup) {
		this.statesGroup = statesGroup;
	}

	@Override
	public boolean equals(Object obj) {
		StatesInGroups lala= (StatesInGroups)obj;
		if(this.getStatesGroup().size()!=lala.getStatesGroup().size())
			return false;
		
		ArrayList<State> arr1= new ArrayList<>();
		arr1.addAll(this.getStatesGroup());
		
		ArrayList<State> arr2= new ArrayList<>();
		arr2.addAll(lala.getStatesGroup());
		
		int counter=0;
		for (State state : arr1) {
			for (State state2 : arr2) {
				if(state.compareTo(state2)==0)
					counter++;
			}
		}
		if(counter==arr1.size())
			return true;
		
		return false;
	}
}
