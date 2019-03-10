/**
 *
 */
/**
 * @author shimizukengo
 *
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public class Load_problem{
	List<Customer> customer = new ArrayList<Customer>();
	List<List<Double>> dist = new ArrayList<List<Double>>();

	String benchmark;
	String experiment;

	//constrain information
	int max_vehi_num;
	int max_capacity;

	//parameters used in ACO
	int ant_num;
	float beta; //the weight in the probability formula
	float q0; //the probability to choose next node j
	float alpha; //parameter of evaporation (global update)
	float rho; //parameter of evaporation (local update)
	
	//ending criteria
	int iteration;
	float time;
	int improve;

	//the number of iteration
	String ite_num;

	public static Load_problem load_problem(String[] args){
		//this method loads the parameters used in ACO and information in VRPTW (coordinates, distances and so on)
		Load_problem problem = new Load_problem();

		problem.ite_num = args[2];

		//args[0] = name of benchmark
		String txt = ".txt";
		String path = "/Users/shimizukengo/Desktop/important/study_abroad/research/In/";
		String benchmark_path = path + args[0] + txt;
		problem.benchmark = args[0];

		//load benchmark information
		String inFilePath = benchmark_path;
		try(FileInputStream f = new FileInputStream(inFilePath);) {
			InputStreamReader fReader = new InputStreamReader(f,"UTF8");
			BufferedReader bufferedReader = new BufferedReader(fReader);
			String data;
			int count = 0;
			int count_customer = 0;
			List<String> line1 = new ArrayList<String>();
			while ((data = bufferedReader.readLine()) != null) { // read lines one by one
				line1.clear();
				data = data.replace("\n","");
				data = data.replace("\r","");
				String[] datas = data.split(" ");
				for (int i=0; i<datas.length; i++){
					if (datas[i].isEmpty() != true){
						line1.add(datas[i]);
					}
				}
				if (count == 4){
					problem.max_vehi_num = Integer.parseInt(line1.get(0));
					problem.max_capacity = Integer.parseInt(line1.get(1));
				}
				if (count > 8){
					problem.customer.add(new Customer(Integer.parseInt(line1.get(1)), Integer.parseInt(line1.get(2))));
					problem.customer.get(count_customer).demand = Integer.parseInt(line1.get(3));
					problem.customer.get(count_customer).ready_time = Integer.parseInt(line1.get(4));
					problem.customer.get(count_customer).due_time = Integer.parseInt(line1.get(5));
					problem.customer.get(count_customer).service_time = Integer.parseInt(line1.get(6));
					count_customer += 1;
				}
				count += 1;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		//args[1] = parameters
		String txt2 = ".txt";
		String path2 = "/Users/shimizukengo/Desktop/important/study_abroad/research/PSO_ACO_VRPTW/src/parameter/";
		String parafilename_path2 = path2 + args[1] + txt2;
		String inFilePath2 = parafilename_path2;
		try(FileInputStream f2 = new FileInputStream(inFilePath2);) {
			InputStreamReader f2Reader = new InputStreamReader(f2,"UTF8");
			BufferedReader bufferedReader2 = new BufferedReader(f2Reader);
			String data;
			int count = 0;
			while ((data = bufferedReader2.readLine()) != null) { // read lines one by one
				data = data.replace("\n","");
				data = data.replace("\r","");
				String[] datas = data.split(" ");
				if (count == 0){
					problem.ant_num = Integer.parseInt(datas[2]);
				}
				
				if (count == 1){
					problem.iteration = Integer.parseInt(datas[2]);
				}
				
				if (count == 2){
					problem.beta = Float.parseFloat(datas[2]);
				}

				if (count == 3){
					problem.q0 = Float.parseFloat(datas[2]);
				}

				if (count == 4){
					problem.alpha = Float.parseFloat(datas[2]);
				}
				
				if (count == 5){
					problem.rho = Float.parseFloat(datas[2]);
				}
				
				if (count == 6){
					problem.time = Float.parseFloat(datas[2]);
				}
				
				if (count == 7){
					problem.improve = Integer.parseInt(datas[2]);
				}

				if (count == 8){
					problem.experiment = String.valueOf(datas[2]);
				}

				count += 1;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i=0; i<problem.customer.size(); i++) {
			List<Double> dist_ = new ArrayList<Double>();
			for (int j=0; j<problem.customer.size(); j++) {
				dist_.add(0.0);
			}
			problem.dist.add(dist_);
		}
		for (int i=0; i<problem.customer.size()-1; i++) {
			for (int j=i+1; j<problem.customer.size(); j++) {
				problem.dist.get(i).set(j, (double) Math.sqrt(Math.pow(problem.customer.get(i).pos[0]-problem.customer.get(j).pos[0],2) + Math.pow(problem.customer.get(i).pos[1]-problem.customer.get(j).pos[1],2)));
				problem.dist.get(j).set(i, problem.dist.get(i).get(j));
			}
		}
		return problem;
	}
}



