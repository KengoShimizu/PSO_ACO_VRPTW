import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Nearest_neighbor_heuristic {
	List<List<Integer>> route = new ArrayList<List<Integer>>();
	List<Integer> capacity = new ArrayList<Integer>();
	List<Double> distance = new ArrayList<Double>();
	double n; //the number of customers and depots
	double J_nnh; //J_nnh is total traveling time (without considering waiting time)

	public static Nearest_neighbor_heuristic nnh(Load_problem problem){
		Nearest_neighbor_heuristic ini_sol = new Nearest_neighbor_heuristic();
		List<Integer> unsearved_customers = new ArrayList<Integer>(); //customers have not already served by a vehicle 
		List<Integer> route_ = new ArrayList<Integer>(Arrays.asList(0));
		ini_sol.route.add(route_);
		ini_sol.distance.add(0.0);
		ini_sol.capacity.add(0);
		for (int i=1; i<problem.customer.size(); i++) unsearved_customers.add(i);
		
		while(unsearved_customers.size() != 0) {
			int last_vehicle_ind = ini_sol.route.size()-1;
			int last_customer = ini_sol.route.get(last_vehicle_ind).get(ini_sol.route.get(last_vehicle_ind).size()-1);
			int counter = 0;
			for (int i=0; i<ini_sol.route.get(last_vehicle_ind).size(); i++) if (ini_sol.route.get(last_vehicle_ind).get(i) == 0) counter = counter + 1; //check if the route is finished or not
			switch(counter) {
			case 2: //construct a new route (the route was finished constructing)
				List<Integer> route_1 = new ArrayList<Integer>(Arrays.asList(0));
				ini_sol.route.add(route_1); //start from depot
				ini_sol.distance.add(0.0);
				ini_sol.capacity.add(0);
				last_vehicle_ind = ini_sol.route.size()-1; //last_vehicle_ind was changed because the new route was created
				
				//search customers do not violate constrains
				List<Integer> meet_const_customer = new ArrayList<Integer>();
				for (int i = 0; i < unsearved_customers.size(); i++) {
					if (ini_sol.distance.get(ini_sol.distance.size()-1) + problem.dist.get(last_customer).get(unsearved_customers.get(i)) <= problem.customer.get(unsearved_customers.get(i)).due_time) { //meet time constrain
						meet_const_customer.add(unsearved_customers.get(i));
					}
				}
				
				//find nearest route from depot from meet_const_customer
				double min_dist = problem.dist.get(0).get(meet_const_customer.get(0)); 
				int index = meet_const_customer.get(0);
				for (int i = 1; i < meet_const_customer.size(); i++) { //search nearest route from depot
					double x = problem.dist.get(0).get(meet_const_customer.get(i));
					if (x < min_dist) {	
						min_dist = x;	
						index = meet_const_customer.get(i);
					}
				}
				//find nearest route from depot from meet_const_customer
				
				//first customer is added to route and capacity and distance and remove from unsearved_customers
				ini_sol.capacity.set(ini_sol.capacity.size()-1, ini_sol.capacity.get(last_vehicle_ind) + problem.customer.get(index).demand); 
				if (problem.dist.get(0).get(index) + ini_sol.distance.get(ini_sol.distance.size()-1) < problem.customer.get(index).ready_time) {
					ini_sol.distance.set(ini_sol.distance.size()-1, (double) (problem.customer.get(index).ready_time + problem.customer.get(index).service_time));
				}
				else ini_sol.distance.set(ini_sol.distance.size()-1, problem.dist.get(0).get(index) + problem.customer.get(index).service_time);
				ini_sol.route.get(last_vehicle_ind).add(index);
				unsearved_customers.remove((Integer)index);
				break;
				
			default: //search next customer to be ridden
				//search customers do not violate constrains
				meet_const_customer = new ArrayList<Integer>();
				for (int i = 0; i < unsearved_customers.size(); i++) { //search nearest route from customer i
					if (ini_sol.distance.get(ini_sol.distance.size()-1) + problem.dist.get(last_customer).get(unsearved_customers.get(i)) <= problem.customer.get(unsearved_customers.get(i)).due_time) { //meet time constrain
						if (ini_sol.capacity.get(ini_sol.capacity.size()-1) + problem.customer.get(unsearved_customers.get(i)).demand <= problem.max_capacity) { //meet capacity constrain
							meet_const_customer.add(unsearved_customers.get(i));
						}
					}
				}
				
				switch(meet_const_customer.size()) {
				case 0: //there is no customer meets constrains
					//go back to depot
					ini_sol.route.get(last_vehicle_ind).add(0);
					ini_sol.distance.set(ini_sol.distance.size()-1, ini_sol.distance.get(ini_sol.distance.size()-1) + problem.dist.get(last_customer).get(0));
					break;
					
				default: //there are customers meet constrains
					
					//find nearest route from last_customer from meet_const_customer
					min_dist = problem.dist.get(last_customer).get(meet_const_customer.get(0)); 
					index = meet_const_customer.get(0);
					for (int i = 1; i < meet_const_customer.size(); i++) {
						double x = problem.dist.get(0).get(meet_const_customer.get(i));
						if (x < min_dist) {	
							min_dist = x;	
							index = meet_const_customer.get(i);
						}
					}
					//find nearest route from last_customer from meet_const_customer
					
					//customer is added to route and capacity and distance and remove from unsearved_customers
					ini_sol.capacity.set(ini_sol.capacity.size()-1, ini_sol.capacity.get(ini_sol.capacity.size()-1) + problem.customer.get(index).demand); 
					if (problem.dist.get(last_customer).get(index) + ini_sol.distance.get(ini_sol.distance.size()-1) < problem.customer.get(index).ready_time) {
						ini_sol.distance.set(ini_sol.distance.size()-1, (double) (problem.customer.get(index).ready_time + problem.customer.get(index).service_time));
					}
					else ini_sol.distance.set(ini_sol.distance.size()-1, ini_sol.distance.get(ini_sol.distance.size()-1) + problem.dist.get(last_customer).get(index) + problem.customer.get(index).service_time);
					ini_sol.route.get(last_vehicle_ind).add(index);
					unsearved_customers.remove((Integer)index);
				}
			}
		}
		ini_sol.route.get(ini_sol.route.size()-1).add(0);
		ini_sol.n = ini_sol.route.size() + problem.customer.size() - 1;
		for (int i=0; i<ini_sol.route.size(); i++) {
			for (int j=0; j<ini_sol.route.get(i).size()-1; j++) {
				ini_sol.J_nnh = ini_sol.J_nnh + problem.dist.get(ini_sol.route.get(i).get(j)).get(ini_sol.route.get(i).get(j+1));
			}
		}
		return ini_sol;
	}
}
