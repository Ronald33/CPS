import java.util.ArrayList;
import java.util.Arrays;

import ronald.tesis.comparative.Comparative;
import ronald.tesis.generator.Helper;

public class App
{
	private int _B_map = 13;
	private int _L_map = 11;
	private int _B_reduce = 11;
	private int _L_reduce = 18;
	private double _T_map = 0.003;
	private double _T_reduce = 0.004;
	
	public void test(String path) throws Exception
	{
		int tests_size = 1;
		int min_id = 1;
		int max_id = 1001;
		int number_of_tasks = 5000;
//		ronald.tesis.generator.Helper.writeWeights(min_id, max_id, number_of_tasks);
		int repetitions = 10;
		int historicals[] = new int[]{5, 10, 20, 50, 100, 200, 400, 600, 800, 1000};
//		int historicals[] = new int[]{5, 10, 20, 50, 100};
		int historicals_size = historicals.length;
		int tests[] = new int[historicals_size];
		Arrays.fill(tests, tests_size);
		int min_ids[] = new int[historicals_size];
		Arrays.fill(min_ids, min_id);
		int max_ids[] = new int[historicals_size];
		int tasks_map[] = new int[historicals_size];
		Arrays.fill(tasks_map, number_of_tasks);
		int tasks_reduce[] = new int[historicals_size];
		Arrays.fill(tasks_reduce, number_of_tasks);
		ArrayList<ArrayList<String>> jobs = new ArrayList<>();
		for(int i = 0; i < historicals_size; i++) { jobs.add(ronald.tesis.generator.Helper.jobs); max_ids[i] = historicals[i] + tests[i]; }
		int Bs_map[] = new int[historicals_size];
		Arrays.fill(Bs_map, this._B_map);
		int Ls_map[] = new int[historicals_size];
		Arrays.fill(Ls_map, this._L_map);
		double Ts_map[] = new double[historicals_size];
		Arrays.fill(Ts_map, this._T_map);
		int Bs_reduce[] = new int[historicals_size];
		Arrays.fill(Bs_reduce, this._B_reduce);
		int Ls_reduce[] = new int[historicals_size];
		Arrays.fill(Ls_reduce, this._L_reduce);
		double Ts_reduce[] = new double[historicals_size];
		Arrays.fill(Ts_reduce, this._T_reduce);
		Comparative.multiTest(path, repetitions, historicals, tests, min_ids, max_ids, tasks_map, tasks_reduce, jobs, Bs_map, Ls_map, Ts_map, Bs_reduce, Ls_reduce, Ts_reduce);
	}
	
	public void testRandoms(String path) throws Exception
	{
		int tests_size = 1;
		int min_id = 1;
		int max_id = 2;
		int min_job_id = 1;
		int max_job_id = 1001;
		int number_of_tasks = 5000;
//		ronald.tesis.generator.Helper.writeRandomJobs(min_id, max_id, number_of_tasks, min_job_id, max_job_id);
		int repetitions = 10;
		int historicals[] = new int[]{20, 50, 100, 200, 300, 400, 500};
//		int historicals[] = new int[]{20};
		int historicals_size = historicals.length;
		int tests[] = new int[historicals_size];
		Arrays.fill(tests, tests_size);
		int min_ids[] = new int[historicals_size];
		Arrays.fill(min_ids, min_id);
		int max_ids[] = new int[historicals_size];
		int tasks_map[] = new int[historicals_size];
		Arrays.fill(tasks_map, number_of_tasks);
		int tasks_reduce[] = new int[historicals_size];
		Arrays.fill(tasks_reduce, number_of_tasks);
		ArrayList<ArrayList<String>> jobs = new ArrayList<>();
		for(int i = 0; i < historicals_size; i++) { max_ids[i] = 2; jobs.add(ronald.tesis.generator.Helper.getNameJobs(min_job_id, historicals[i] + tests[i])); }
		int Bs_map[] = new int[historicals_size];
		Arrays.fill(Bs_map, this._B_map);
		int Ls_map[] = new int[historicals_size];
		Arrays.fill(Ls_map, this._L_map);
		double Ts_map[] = new double[historicals_size];
		Arrays.fill(Ts_map, this._T_map);
		int Bs_reduce[] = new int[historicals_size];
		Arrays.fill(Bs_reduce, this._B_reduce);
		int Ls_reduce[] = new int[historicals_size];
		Arrays.fill(Ls_reduce, this._L_reduce);
		double Ts_reduce[] = new double[historicals_size];
		Arrays.fill(Ts_reduce, this._T_reduce);
		Comparative.multiTest(path, repetitions, historicals, tests, min_ids, max_ids, tasks_map, tasks_reduce, jobs, Bs_map, Ls_map, Ts_map, Bs_reduce, Ls_reduce, Ts_reduce);
	}
	
	public static void main(String[] args)
	{
		try
		{
			System.out.println("Processing ...");
			String id = Helper.getUniqueId();
			App app = new App();
//			app.test("with_samr_" + id);
			app.testRandoms("rwiths_" + id);
			System.out.println("ID: " + id);
			System.out.println("Finished ...");
		}
		catch(Exception e) { e.printStackTrace(); }
	}
}
