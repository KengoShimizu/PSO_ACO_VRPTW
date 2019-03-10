/**
 *  How to use:
 *  Benchmark_filename Parameters_filename iteration_number
 */
/**
 * @author shimizukengo
 *
 */

public class Main{
	public static void main(String[] args) {
		
		//load problem and parameters
		Load_problem load = new Load_problem();
		load = Load_problem.load_problem(args);
		
		//time start
		long start = System.currentTimeMillis();
		
		//apply ACO
		Ant_colony_opt solutions = new Ant_colony_opt();
		solutions = Ant_colony_opt.ant_main(load,start);
		
		//time end
		long end = System.currentTimeMillis();
		long e_s = end - start;
		
		//output as .dat file
		Output.output_minimum_transition(load, e_s, solutions);
		Output.output_positions(load, e_s, solutions);
		
		System.out.println("fin");
	}
}