import java.util.Arrays;
import java.util.List;
import ac.il.afeka.Submission.Submission;
import ac.il.afeka.fsm.Alphabet;
import ac.il.afeka.fsm.DFSM;
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
	
	public static void main(String[] args) throws Exception {
//		String encoding = "0 1 2 3 4 " 				// machine states
//				+ "/a b " 				// alphabet
//				+ "/0,,1;" 	// transections
//				+ "1,,2;"
//				+ "2,,3;"
//				+ "3,a,0;"
//				+ "3,b,4"
//				+ "/0"						// staring state
//				+ "/4";						// accepted state

//		String encoding = "0 1 2 3  " 				// machine states
//				+ "/a b " 				// alphabet
//				+ "/0,a,0;" 	// transections
//				+ "0,b,1;"
//				+ "0,b,2;"
//				+ "2,a,2;"
//				+ "2,b,1;"
//				+ "1,a,1;"
//				+ "1,b,2;"
//				+ "1,a,3;"
//				+ "3,b,2"
//				+ "/0"						// staring state
//				+ "/0 3";						// accepted state
		String encoding= "1 2 "
				+"/0 1"
				+"/1,0,1;"
				+"1,1,2;"
				+"2,0,2;"
				+"2,1,1"
				+"/1"
				+"/2";
		DFSM rr= new NDFSM(encoding).toDFSM();
		rr.prettyPrint(System.out);
	}
}
