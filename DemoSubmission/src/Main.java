import java.util.Arrays;
import java.util.List;

import ac.il.afeka.Submission.Submission;
import ac.il.afeka.fsm.NDFSM;


public class Main implements Submission, Assignment3 {

	@Override
	public List<String> submittingStudentIds() {
		return Arrays.asList("208722710", "318353356","315000539");
	}

	@Override
	public String convert(String ndfsm) throws Exception {
		return new NDFSM(ndfsm).toDFSM().encode();
	}
}
