import java.util.ArrayList;
import java.util.List;

/**
 *
 */
/**
 * @author shimizukengo
 *
 */

public class Ant_colony_opt {
	List<Build_tour> pareto_sols = new ArrayList<Build_tour>();
	List<Integer> min_vehinum_tracker = new ArrayList<Integer>();
	List<Double> min_distance_tracker = new ArrayList<Double>();

	public static Ant_colony_opt ant_main(Load_problem problem, long start){
		Ant_colony_opt aco = new Ant_colony_opt();
		List<List<Double>> tau = new ArrayList<List<Double>>(); //tau = pheromone matrix

		//Initialize solution and pheromone
		Nearest_neighbor_heuristic ini_sol = new Nearest_neighbor_heuristic();
		ini_sol = Nearest_neighbor_heuristic.nnh(problem); //initial solution which is perfectly meet constrains
		double tau_0 = 1 / (ini_sol.n * ini_sol.J_nnh); //initial pheromone
		for (int i=0; i<problem.customer.size(); i++) {
			List<Double> phe_mat = new ArrayList<Double>();
			for (int j=0; j<problem.customer.size(); j++) {
				phe_mat.add(0.0);
			}
			tau.add(phe_mat);
		}
		for (int i=0; i<problem.customer.size()-1; i++) {
			for (int j=i+1; j<problem.customer.size(); j++) {
				tau.get(i).set(j, tau_0);
				tau.get(j).set(i, tau.get(i).get(j));
			}
		}
		//Initialize solution and pheromone

		int improve_check = 0;
		double fix_tau_0 = tau_0;
		for (int criteria=0; criteria<problem.iteration; criteria++) {
			long end = System.currentTimeMillis();
			long e_s = end - start;
			if (e_s > problem.time*1000) return aco;
			
			
			for (int i=1; i<=problem.ant_num; i++) {
				Build_tour solution = new Build_tour(); //ant i create a solution
				solution = Build_tour.buld_tour(problem, tau, fix_tau_0);
				if (aco.pareto_sols.size() == 0) aco.pareto_sols.add(solution);
				if (Pareto_check.check(aco.pareto_sols, solution) == true && aco.pareto_sols.size() != 0) { //the case where the solution is in pareto solutions
					List<Build_tour> dominated_sols_ind = new ArrayList<Build_tour>(Pareto_check.rm_ind(aco.pareto_sols, solution));
					for (int j=0; j<dominated_sols_ind.size(); j++) aco.pareto_sols.remove(dominated_sols_ind.get(j)); //remove dominated solutions from aco.pareto_sols
					aco.pareto_sols.add(solution); //new solution is added to pareto_sols
				}
			}

			double P_n = 0.0;
			double P_J = 0.0;
			for (int i=0; i<aco.pareto_sols.size(); i++) {
				P_n = P_n + aco.pareto_sols.get(i).n;
				P_J = P_J + aco.pareto_sols.get(i).J;
			}
			P_n = P_n / aco.pareto_sols.size(); //the average value in pareto solutions
			P_J = P_J / aco.pareto_sols.size(); //the average value in pareto solutions

			double tau_0t = 1 / (P_n * P_J); //calculate tau_0t
			if (tau_0t > tau_0) { //pareto solutions are better than before
				improve_check = 0;
				tau_0 = tau_0t; //update tau_0
				System.out.println("better solution was found");
			}

			else { //pareto solutions are not better than before (global update)
				improve_check = improve_check + 1;
				if (improve_check >= problem.improve) {
					return aco;
					
				}
				
				//evaporate pheromone
				for (int i=0; i<problem.customer.size(); i++) {
					for (int j=i+1; j<problem.customer.size(); j++) {
							tau.get(i).set(j, (1.0-problem.alpha) * tau.get(i).get(j));
							tau.get(j).set(i, tau.get(i).get(j));
					}
				}
				
				
				//global pheromon update
				for (int p=0; p<aco.pareto_sols.size(); p++) {
					for (int vehi=0; vehi<aco.pareto_sols.get(p).route.size(); vehi++) {
						for (int i=0; i<aco.pareto_sols.get(p).route.get(vehi).size()-1; i++) {
							int cus_i = aco.pareto_sols.get(p).route.get(vehi).get(i);
							int cus_j = aco.pareto_sols.get(p).route.get(vehi).get(i+1);
							tau.get(cus_i).set(cus_j, tau.get(cus_i).get(cus_j) + problem.alpha / P_J);
							tau.get(cus_j).set(cus_i, tau.get(cus_i).get(cus_j));
						}
					}
				}
			
				
			}

			//tracking minimum vehicle number, total time and total distance
			int min_vehinum = aco.pareto_sols.get(0).n - (problem.customer.size() - 1);
			double min_distance = aco.pareto_sols.get(0).J;
			for (int i=1; i<aco.pareto_sols.size(); i++) {
				min_vehinum = Math.min(min_vehinum, aco.pareto_sols.get(i).n - (problem.customer.size() - 1));
				min_distance = Math.min(min_distance, aco.pareto_sols.get(i).J);
			}

			aco.min_vehinum_tracker.add(min_vehinum);
			aco.min_distance_tracker.add(min_distance);
		}
		
		return aco;
	}
}
