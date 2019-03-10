import java.util.ArrayList;
import java.util.List;

public class Pareto_check {
	public static boolean check(List<Build_tour> pareto_sols, Build_tour solution){
		for(int i=0; i<pareto_sols.size(); i++) {
			if(solution.n >= pareto_sols.get(i).n && solution.J >= pareto_sols.get(i).J) {
				return false; //new solution is not a pareto solution
			}
		}
		return true; //new solution is a pareto solution
	}
	
	public static List<Build_tour> rm_ind(List<Build_tour> pareto_sols, Build_tour solution){
		List<Build_tour> dominated_sols_ind = new ArrayList<Build_tour>();
		for(int i=0; i<pareto_sols.size(); i++) {
			if(solution.n <= pareto_sols.get(i).n && solution.J <= pareto_sols.get(i).J) {
				dominated_sols_ind.add(pareto_sols.get(i));
			}
		}
		return dominated_sols_ind;
	}
}
