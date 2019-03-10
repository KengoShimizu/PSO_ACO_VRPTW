import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.lang.Math;

public class Build_tour {
	List<List<Integer>> route = new ArrayList<List<Integer>>();
	List<Integer> capacity = new ArrayList<Integer>();
	List<Double> distance = new ArrayList<Double>();
	int n; //the number of customers and depots
	double J=0.0; //J_nnh is total distance (without considering waiting time)
	
	public static Build_tour buld_tour(Load_problem problem, List<List<Double>> tau, double tau_0){
		Build_tour sol = new Build_tour();
		List<Integer> unsearved_customers = new ArrayList<Integer>(); //customers have not already served by a vehicle 
		List<Integer> route_ = new ArrayList<Integer>(Arrays.asList(0));
		sol.route.add(route_);
		sol.distance.add(0.0);
		sol.capacity.add(0);
		for (int i=1; i<problem.customer.size(); i++) unsearved_customers.add(i);
		long seed = Runtime.getRuntime().freeMemory();
		Random rnd = new Random(seed);
		
		while(unsearved_customers.size() != 0) {
			int last_vehicle_ind = sol.route.size()-1;
			int last_customer = sol.route.get(last_vehicle_ind).get(sol.route.get(last_vehicle_ind).size()-1);
			int counter = 0;
			for (int i=0; i<sol.route.get(last_vehicle_ind).size(); i++) if (sol.route.get(last_vehicle_ind).get(i) == 0) counter = counter + 1; //check if the route is finished or not
			List<Double> prob = new ArrayList<Double>();
			double prob_denom = 0.0; //denominator of probability
			List<Double> prob_nume = new ArrayList<Double>(); //numerator of probability
			List<Double> nu_L = new ArrayList<Double>(); //the formula considered time window
			switch(counter) {
			case 2: //construct a new route (the route was finished constructing)
				List<Integer> route_1 = new ArrayList<Integer>(Arrays.asList(0));
				sol.route.add(route_1); //start from depot
				sol.distance.add(0.0);
				sol.capacity.add(0);
				last_vehicle_ind = sol.route.size()-1; //last_vehicle_ind was changed because the new route was created
				
				//search customers do not violate constrains
				List<Integer> meet_const_customer = new ArrayList<Integer>();
				for (int i = 0; i < unsearved_customers.size(); i++) {
					if (sol.distance.get(sol.distance.size()-1) + problem.dist.get(last_customer).get(unsearved_customers.get(i)) <= problem.customer.get(unsearved_customers.get(i)).due_time) { //meet time constrain
						meet_const_customer.add(unsearved_customers.get(i));
					}
				}
				
				//calculate and nu_L (array)
				for (int i=0; i<meet_const_customer.size(); i++) {
					double ct_j = Math.max(problem.dist.get(0).get(meet_const_customer.get(i)),problem.customer.get(meet_const_customer.get(i)).ready_time);
					double delta_tij = ct_j;
					double d_ij = Math.max(1.0, delta_tij * problem.customer.get(meet_const_customer.get(i)).due_time);
					nu_L.add(1 / d_ij);
				}
				//calculate the numerator of the probability and the power of nu_L
				for(int i=0; i<nu_L.size(); i++) {
					nu_L.set(i, Math.pow(nu_L.get(i), problem.beta));
					prob_nume.add(tau.get(0).get(meet_const_customer.get(i)) * nu_L.get(i));
				}
				//calculate the denominator of the probability and search max value in probability
				double max_prob_nume = prob_nume.get(0);
				int index = 0;
				for (int i=0; i<nu_L.size(); i++) {
					prob_denom = prob_denom + prob_nume.get(i);
					if (max_prob_nume < prob_nume.get(i)) {
						max_prob_nume = prob_nume.get(i);
						index = i;
					}
 				}
				//calculate the probabilities
				for (int i=0; i<nu_L.size(); i++) prob.add(prob_nume.get(i)/prob_denom);
				
				if (rnd.nextDouble() < problem.q0) { //the case where the node j is chosen as next customer within probability q0
					int next = meet_const_customer.get(index); 
					sol.capacity.set(sol.capacity.size()-1, sol.capacity.get(last_vehicle_ind) + problem.customer.get(next).demand); 
					if (problem.dist.get(0).get(next) + sol.distance.get(sol.distance.size()-1) < problem.customer.get(next).ready_time) {
						sol.distance.set(sol.distance.size()-1, (double) (problem.customer.get(next).ready_time + problem.customer.get(next).service_time));
					}
					else sol.distance.set(sol.distance.size()-1, problem.dist.get(0).get(next) + problem.customer.get(next).service_time);
					sol.route.get(last_vehicle_ind).add(next);
					unsearved_customers.remove((Integer)next);
					//the ant drop pheromone as tracking
					tau.get(0).set(next,  (1.0-problem.rho) * tau.get(0).get(next) + problem.rho * tau_0);
					tau.get(next).set(0, tau.get(0).get(next));
				}
				
				else {//the case where the node j is chosen as next customer with probabilities 
					//choose the next node j at probabilities
					double prob_sum = 0.0;
					index = 0;
					List<Double> prob_tmp = new ArrayList<Double>(prob);
					Collections.sort(prob);
					for (int i=0; i<prob.size(); i++) {
						prob_sum = prob_sum + prob.get(i);
						if (prob_sum > rnd.nextDouble()) {
							index = prob_tmp.indexOf(prob.get(i));
							break;
						}
					}
					//the next customer is added to the route
					int next = meet_const_customer.get(index);
					sol.capacity.set(sol.capacity.size()-1, sol.capacity.get(last_vehicle_ind) + problem.customer.get(next).demand); 
					if (problem.dist.get(0).get(next) + sol.distance.get(sol.distance.size()-1) < problem.customer.get(next).ready_time) {
						sol.distance.set(sol.distance.size()-1, (double) (problem.customer.get(next).ready_time + problem.customer.get(next).service_time));
					}
					else sol.distance.set(sol.distance.size()-1, problem.dist.get(0).get(next) + problem.customer.get(next).service_time);
					sol.route.get(last_vehicle_ind).add(next);
					unsearved_customers.remove((Integer)next);
					//the ant drop pheromone as tracking
					tau.get(0).set(next,  (1.0-problem.rho) * tau.get(0).get(next) + problem.rho * tau_0);
					tau.get(next).set(0, tau.get(0).get(next));
				}
				
				break;
				
			default: //search next customer to be ridden
				//search customers do not violate constrains
				meet_const_customer = new ArrayList<Integer>();
				for (int i = 0; i < unsearved_customers.size(); i++) { //search nearest route from customer i
					if (sol.distance.get(sol.distance.size()-1) + problem.dist.get(last_customer).get(unsearved_customers.get(i)) <= problem.customer.get(unsearved_customers.get(i)).due_time) { //meet time constrain
						if (sol.capacity.get(sol.capacity.size()-1) + problem.customer.get(unsearved_customers.get(i)).demand <= problem.max_capacity) { //meet capacity constrain
							meet_const_customer.add(unsearved_customers.get(i));
						}
					}
				}
				
				switch(meet_const_customer.size()) {
				case 0: //there is no customer meets constrains
					//go back to depot
					sol.route.get(last_vehicle_ind).add(0);
					sol.distance.set(sol.distance.size()-1, sol.distance.get(sol.distance.size()-1) + problem.dist.get(last_customer).get(0));
					break;
					
				default: //there are customers meet constrains
					//calculate and nu_L (array)
					for (int i=0; i<meet_const_customer.size(); i++) {
						double ct_j = Math.max(sol.distance.get(last_vehicle_ind) + problem.dist.get(last_customer).get(meet_const_customer.get(i)), problem.customer.get(meet_const_customer.get(i)).ready_time);
						double delta_tij = ct_j - sol.distance.get(last_vehicle_ind);
						double d_ij = Math.max(1.0, delta_tij * (problem.customer.get(meet_const_customer.get(i)).due_time - sol.distance.get(last_vehicle_ind)));
						nu_L.add(1 / d_ij);
					}
					//calculate the numerator of the probability and the power of nu_L
					for(int i=0; i<nu_L.size(); i++) {
						nu_L.set(i, Math.pow(nu_L.get(i), problem.beta));
						prob_nume.add(tau.get(last_customer).get(meet_const_customer.get(i)) * nu_L.get(i));
					}
					//calculate the denominator of the probability
					max_prob_nume = prob_nume.get(0);
					index = 0;
					for (int i=0; i<nu_L.size(); i++) {
						prob_denom = prob_denom + prob_nume.get(i);
						if (max_prob_nume < prob_nume.get(i)) {
							max_prob_nume = prob_nume.get(i);
							index = i;
						}
	 				}
					//calculate the probabilities
					for (int i=0; i<nu_L.size(); i++) prob.add(prob_nume.get(i)/prob_denom);
					
					if (rnd.nextDouble() < problem.q0) { //the case where the node j is chosen as next customer within probability q0
						int next = meet_const_customer.get(index);
						sol.capacity.set(sol.capacity.size()-1, sol.capacity.get(last_vehicle_ind) + problem.customer.get(next).demand); 
						if (problem.dist.get(last_customer).get(next) + sol.distance.get(sol.distance.size()-1) < problem.customer.get(next).ready_time) {
							sol.distance.set(sol.distance.size()-1, (double) (problem.customer.get(next).ready_time + problem.customer.get(next).service_time));
						}
						else sol.distance.set(sol.distance.size()-1, sol.distance.get(last_vehicle_ind) + problem.dist.get(last_customer).get(next) + problem.customer.get(next).service_time);
						sol.route.get(last_vehicle_ind).add(next);
						unsearved_customers.remove((Integer)next);
						//the ant drop pheromone as tracking
						tau.get(last_customer).set(next,  (1.0-problem.rho) * tau.get(last_customer).get(next) + problem.rho * tau_0);
						tau.get(next).set(last_customer, tau.get(last_customer).get(next));
					}
					
					else {//the case where the node j is chosen as next customer with probabilities 
						//choose the next node j at probabilities
						double prob_sum = 0.0;
						index = 0;
						List<Double> prob_tmp = new ArrayList<Double>(prob);
						Collections.sort(prob);
						for (int i=0; i<prob.size(); i++) {
							prob_sum = prob_sum + prob.get(i);
							if (prob_sum > rnd.nextDouble()) {
								index = prob_tmp.indexOf(prob.get(i));
								break;
							}
						}
						
						//the next customer is added to the route
						int next = meet_const_customer.get(index);
						sol.capacity.set(sol.capacity.size()-1, sol.capacity.get(last_vehicle_ind) + problem.customer.get(next).demand); 
						if (problem.dist.get(last_customer).get(next) + sol.distance.get(sol.distance.size()-1) < problem.customer.get(next).ready_time) {
							sol.distance.set(sol.distance.size()-1, (double) (problem.customer.get(next).ready_time + problem.customer.get(next).service_time));
						}
						else sol.distance.set(sol.distance.size()-1, sol.distance.get(last_vehicle_ind) + problem.dist.get(last_customer).get(next) + problem.customer.get(next).service_time);
						sol.route.get(last_vehicle_ind).add(next);
						unsearved_customers.remove((Integer)next);
						//the ant drop pheromone as tracking
						tau.get(last_customer).set(next,  (1.0-problem.rho) * tau.get(last_customer).get(next) + problem.rho * tau_0);
						tau.get(next).set(last_customer, tau.get(last_customer).get(next));
					}
				}
			}
		}
		sol.route.get(sol.route.size()-1).add(0);
		sol.n = sol.route.size() + problem.customer.size() - 1;
		for (int i=0; i<sol.route.size(); i++) {
			for (int j=0; j<sol.route.get(i).size()-1; j++) {
				sol.J = sol.J + problem.dist.get(sol.route.get(i).get(j)).get(sol.route.get(i).get(j+1));
			}
		}
		return sol;
	}
}