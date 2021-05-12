package ac.il.afeka.fsm;

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


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatesInGroups other = (StatesInGroups) obj;
		if (statesGroup == null) {
			if (other.statesGroup != null)
				return false;
		} else if (!statesGroup.equals(other.statesGroup))
			return false;
		return true;
	}
}
