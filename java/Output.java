import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Output {
	public static void output_minimum_transition(Load_problem problem, long e_s, Ant_colony_opt solutions){
		String make_filename = problem.benchmark+"_ant"+String.valueOf(problem.ant_num)+"_beta"+String.valueOf(problem.beta)+"_q0"+String.valueOf(problem.q0)+"_alpha"+String.valueOf(problem.alpha)+"_rho"+String.valueOf(problem.rho);
		Path path1 = Paths.get("/Users/shimizukengo/Desktop/important/study_abroad/research/ACO_VRPTW/outputs/"+problem.experiment);
		Path path2 = Paths.get(path1+"/"+make_filename);

		//If the folder does NOT exit, make this folder in this path
		if (Files.exists(path1) == false) {
			try{
				Files.createDirectory(path1);
			}catch(IOException e){
			    System.out.println(e);
			}
		}

		//If the folder does NOT exit, make this folder in this path
		if (Files.exists(path2) == false) {
			try{
				Files.createDirectory(path2);
			}catch(IOException e){
			    System.out.println(e);
			}
		}

		//make file
		String path = "/Users/shimizukengo/Desktop/important/study_abroad/research/ACO_VRPTW/outputs/"+problem.experiment+"/"+make_filename+"/"+make_filename+"_"+problem.ite_num+".dat";
		File newfile = new File(path);
		try{
		    newfile.createNewFile();
		    FileWriter filewriter = new FileWriter(newfile);

		    //write comment in .dat file
		    filewriter.write("#computetime:"+String.valueOf(e_s)+"[ms]\n");
			filewriter.write("#ant_num:"+String.valueOf(problem.ant_num)+"\titeration:"+String.valueOf(problem.iteration)+"\tbeta:"+String.valueOf(problem.beta)+"\tq0:"+String.valueOf(problem.q0)+"\talpha:"+String.valueOf(problem.alpha)+"\trho:"+String.valueOf(problem.rho)+"\n");
			filewriter.write("#iteration\tvehi_num\ttotal_distance\ttotal_time\n");

			//write data in .dat file
			for (int i=0; i<solutions.min_vehinum_tracker.size(); i++) {
				filewriter.write(String.valueOf(i)+"\t"+String.valueOf(solutions.min_vehinum_tracker.get(i))+"\t"+String.valueOf(solutions.min_distance_tracker.get(i))+"\n");
			}

		    filewriter.close();
		}catch(IOException e){
		    System.out.println(e);
		}
	}


	public static void output_positions(Load_problem problem, long e_s, Ant_colony_opt solutions){
		String make_filename = problem.benchmark+"_ant"+String.valueOf(problem.ant_num)+"_beta"+String.valueOf(problem.beta)+"_q0"+String.valueOf(problem.q0)+"_alpha"+String.valueOf(problem.alpha)+"_rho"+String.valueOf(problem.rho);
		Path path1 = Paths.get("/Users/shimizukengo/Desktop/important/study_abroad/research/ACO_VRPTW/outputs/"+problem.experiment);
		Path path2 = Paths.get(path1+"/"+make_filename);

		//If the folder does NOT exit, make this folder in this path
		if (Files.exists(path1) == false) {
			try{
				Files.createDirectory(path1);
			}catch(IOException e){
			    System.out.println(e);
			}
		}

		//If the folder does NOT exit, make this folder in this path
		if (Files.exists(path2) == false) {
			try{
				Files.createDirectory(path2);
			}catch(IOException e){
			    System.out.println(e);
			}
		}

		for (int p=0; p<solutions.pareto_sols.size(); p++) {
			String path = "/Users/shimizukengo/Desktop/important/study_abroad/research/ACO_VRPTW/outputs/"+problem.experiment+"/"+make_filename+"/"+make_filename+"_"+problem.ite_num+"_position"+p+".dat";
			int count = 0;
			if (solutions.pareto_sols.size() != 1) {
				if (solutions.pareto_sols.get(0).route.size() > solutions.pareto_sols.get(1).route.size()) {
					path = "/Users/shimizukengo/Desktop/important/study_abroad/research/ACO_VRPTW/outputs/"+problem.experiment+"/"+make_filename+"/"+make_filename+"_"+problem.ite_num+"_position1.dat";
					count = count + 1;
				}
				
			}
			if (count == 1) path = "/Users/shimizukengo/Desktop/important/study_abroad/research/ACO_VRPTW/outputs/"+problem.experiment+"/"+make_filename+"/"+make_filename+"_"+problem.ite_num+"_position0.dat";
			File newfile = new File(path);
			try{
			    newfile.createNewFile();
			    FileWriter filewriter = new FileWriter(newfile);

			    //write comment in .dat file
			    filewriter.write("#computetime:"+String.valueOf(e_s)+"[ms]\tvehi_num:"+String.valueOf(solutions.pareto_sols.get(p).route.size())+"\tdistance:"+String.valueOf(solutions.pareto_sols.get(p).J)+"\n");
				filewriter.write("#ant_num:"+String.valueOf(problem.ant_num)+"\titeration:"+String.valueOf(problem.iteration)+"\tbeta:"+String.valueOf(problem.beta)+"\tq0:"+String.valueOf(problem.q0)+"\talpha:"+String.valueOf(problem.alpha)+"\trho:"+String.valueOf(problem.rho)+"\n");
				filewriter.write("#position x\tposition y\n");


				for (int i=0; i<solutions.pareto_sols.get(p).route.size(); i++) {
					for (int j=0; j<solutions.pareto_sols.get(p).route.get(i).size(); j++) {
						filewriter.write(problem.customer.get(solutions.pareto_sols.get(p).route.get(i).get(j)).pos[0]+"\t"+problem.customer.get(solutions.pareto_sols.get(p).route.get(i).get(j)).pos[1]+"\n");
					}
				}


			    filewriter.close();
			}catch(IOException e){
			    System.out.println(e);
			}
		}
	}
}
