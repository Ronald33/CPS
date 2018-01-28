import ronald.tesis.comparative.Comparative;
import ronald.tesis.generator.Helper;

public class App
{	
	public static void main(String[] args)
	{
		try
		{
			int tests_size = 1;
			int min_id = 1;
			int max_id = 1001;
			int number_of_tasks = 1000;
			int min_job_id = 1;
			int max_job_id = 1001;

			int B_map = 7, L_map = 11, B_reduce = 20, L_reduce = 13;
			double T_map = 0.005, T_reduce = 0.007;
			
//			ronald.tesis.generator.Helper.writeWeight(min_id, max_id, number_of_tasks);
//			ronald.tesis.generator.Helper.writeRandomJobs(min_id, max_id, number_of_tasks, min_job_id, max_job_id);
			
			int sizes[] = new int[]{10, 20, 30, 40, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000};
			int length_size = sizes.length;
			String id = Helper.getUniqueId();
			System.out.println("ID: " + id);
			Comparative comparatives[] = new Comparative[length_size];
			for(int i = 0; i < length_size; i++)
			{
				int files_size = sizes[i];
				Comparative c = new Comparative(files_size, tests_size, min_id, max_id, number_of_tasks, number_of_tasks, ronald.tesis.generator.Helper.jobs);
				c.setBMap(B_map);
				c.setLMap(L_map);
				c.setTMap(T_map);
				c.setBReduce(B_reduce);
				c.setLReduce(L_reduce);
				c.setTReduce(T_reduce);
				c.setId(id + "_" + (i + 1));
				comparatives[i] = c;
			}
			Comparative.multiTest("t10", 10, comparatives);
			
			System.out.println("Finished ...");
		}
		catch(Exception e) { e.printStackTrace(); }
	}
}
