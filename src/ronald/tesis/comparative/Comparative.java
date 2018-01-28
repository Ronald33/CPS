package ronald.tesis.comparative;

import java.util.ArrayList;

import ronald.tesis.clustering.birch.BIRCH;
import ronald.tesis.clustering.kmeans.KMeans;
import ronald.tesis.comparative.weights.WMap;
import ronald.tesis.comparative.weights.WReduce;
import ronald.tesis.point.Point;

public class Comparative
{
	private static String[] _schedulers = new String[]{"late", "esamr", "birch"};
	private static int _schedulers_size = _schedulers.length;
	
	private int _tests_size;
	private int _minId;
	private int _maxId;
	
	private int _historical_jobs[];
	private int _historical_files[];
	
	private BIRCH _birch_map;
	private BIRCH _birch_reduce;
	private KMeans _kmeans_map;
	private KMeans _kmeans_reduce;
	
	private ArrayList<ArrayList<Integer>> _files_useds = new ArrayList<>();
	
	private Point _centroids_birch_map[];
	private Point _centroids_birch_reduce[];
	private Point _centroids_kmeans_map[];
	private Point _centroids_kmeans_reduce[];
	
	private double _avg_error_late_maps[][];
	private double _avg_error_late_reduces[][];
	private double _avg_error_esamr_maps[][];
	private double _avg_error_esamr_reduces[][];
	private double _avg_error_birch_maps[][];
	private double _avg_error_birch_reduces[][];

	private double _avg_error_late_map;
	private double _avg_error_esamr_map;
	private double _avg_error_late_reduce;
	private double _avg_error_esamr_reduce;
	private double _avg_error_birch_map;
	private double _avg_error_birch_reduce;
	
	private String _id;
	private int _numberOfTasksMap;
	private int _numberOfTasksReduce;
	
	private double _T_map = ronald.tesis.clustering.birch.Config.T;
	private double _T_reduce = ronald.tesis.clustering.birch.Config.T;
	private int _B_map = ronald.tesis.clustering.birch.Config.B;
	private int _B_reduce = ronald.tesis.clustering.birch.Config.B;
	private int _L_map = ronald.tesis.clustering.birch.Config.L;
	private int _L_reduce = ronald.tesis.clustering.birch.Config.L;
	
	private ArrayList<String> _jobs;
	
	public Comparative(int historical_jobs[], int historical_files[], int tests_size, int min_id, int max_id, int number_of_tasks_map, int number_of_tasks_reduce, ArrayList<String> jobs) throws Exception
	{
		this(tests_size, min_id, max_id, number_of_tasks_map, number_of_tasks_reduce, jobs);
		this.setIndexHistoricalFiles(historical_jobs, historical_files);
	}
	
	public Comparative(int historical_size, int tests_size, int min_id, int max_id, int number_of_tasks_map, int number_of_tasks_reduce, ArrayList<String> jobs) throws Exception
	{
		this(tests_size, min_id, max_id, number_of_tasks_map, number_of_tasks_reduce, jobs);
		this.fillIndexHistoricalFiles(historical_size);
	}
	
	private Comparative(int tests_size, int min_id, int max_id, int number_of_tasks_map, int number_of_tasks_reduce, ArrayList<String> jobs)
	{
		this._id = Helper.getUniqueId();
		this._tests_size = tests_size;
		this._minId = min_id;
		this._maxId = max_id;
		this._numberOfTasksMap = number_of_tasks_map;
		this._numberOfTasksReduce = number_of_tasks_reduce;
		this._jobs = jobs;
		int jobs_size = this._jobs.size();
		this._avg_error_late_maps = new double[jobs_size][tests_size];
		this._avg_error_late_reduces = new double[jobs_size][tests_size];
		this._avg_error_esamr_maps = new double[jobs_size][tests_size];
		this._avg_error_esamr_reduces = new double[jobs_size][tests_size];
		this._avg_error_birch_maps = new double[jobs_size][tests_size];
		this._avg_error_birch_reduces = new double[jobs_size][tests_size];

		for(int i = 0; i < jobs_size; i++) { this._files_useds.add(new ArrayList<Integer>()); }
	}
	
	private String getFileMapById(int job_id, int file_id)
	{
		return Config.data + "/" + this._jobs.get(job_id) + "/map/" + file_id;
	}
	private String getFileReduceById(int job_id, int file_id)
	{
		return Config.data + "/" + this._jobs.get(job_id) + "/reduce/" + file_id;
	}
	
	private int getFileIdByJob(int job_id) throws Exception
	{
		ArrayList<Integer> files_useds_by_job = this._files_useds.get(job_id);
		int file_id = Helper.getRandomUnique(this._minId, this._maxId, files_useds_by_job);
		if(file_id == -1)
		{
			throw new Exception("El job " + this._jobs.get(job_id) + " ya uso todos los archivos disponibles (" + files_useds_by_job + ")");
		}
		return file_id;
	}
	
	private void executeClusteringAlgorithms() throws Exception
	{
		String map = "", reduce = "", map_id = this._id + "_map", reduce_id = this._id + "_reduce";
		ArrayList<Point> collect_maps_1d = new ArrayList<>();
		ArrayList<Point> collect_reduces_2d = new ArrayList<>();
		int length = this._historical_files.length;
		
		for(int i = 0; i < length; i++)
		{
			map = getFileMapById(this._historical_jobs[i], this._historical_files[i]);
			reduce = getFileReduceById(this._historical_jobs[i], this._historical_files[i]);
			ArrayList<Point> map_1d = ronald.tesis.point.Helper.fileToPoints(map, 1);
			ArrayList<Point> reduce_3d = ronald.tesis.point.Helper.fileToPoints(reduce, 3);
			collect_maps_1d.addAll(map_1d);
			collect_reduces_2d.addAll(ronald.tesis.point.Helper.fileToPoints(reduce, 2));
			if(i == 0)
			{
				this._birch_map = new BIRCH(this._T_map, this._B_map, this._L_map, map_id);
				this._birch_map.execute(map_1d);
				this._birch_reduce = new BIRCH(this._T_reduce, this._B_reduce, this._L_reduce, reduce_id);
				this._birch_reduce.execute(reduce_3d);
			}
			else
			{
				this._birch_map = new BIRCH(map_id);
				this._birch_map.execute(map_1d);
				this._birch_reduce = new BIRCH(reduce_id);
				this._birch_reduce.execute(reduce_3d);
			}
			this._birch_map.save();
			this._birch_reduce.save();
		}
		this._kmeans_map = new KMeans();
		this._kmeans_map.execute(collect_maps_1d);
		this._kmeans_reduce = new KMeans();
		this._kmeans_reduce.execute(collect_reduces_2d);
	}
	
	public void fillIndexHistoricalFiles(int historical_size) throws Exception
	{
		this._historical_jobs = new int[historical_size];
		this._historical_files = new int[historical_size];
		int jobs_size = this._jobs.size();
		
		ArrayList<Integer> jobs_useds = new ArrayList<>();
		for(int i = 0; i < historical_size; i++)
		{
			if(i < jobs_size) { this._historical_jobs[i] = Helper.getRandomUnique(0, jobs_size - 1, jobs_useds); }
			else { this._historical_jobs[i] = Helper.getRandom(0, jobs_size - 1); }
			this._historical_files[i] = getFileIdByJob(this._historical_jobs[i]);
		}
	}
	
	private void setIndexHistoricalFiles(int historical_jobs[], int historical_files[]) throws Exception
	{
		int length = historical_jobs.length;
		if(length != historical_files.length) { throw new Exception("The size of the historical files is different to historical jobs"); }
		
		for(int i = 0; i < length; i++)
		{
			int file_id = historical_files[i];
			ArrayList<Integer> files_useds_by_job = this._files_useds.get(historical_jobs[i]);
			if(!Helper.in_array(file_id, files_useds_by_job)) { files_useds_by_job.add(file_id); }
		}
		
		this._historical_jobs = historical_jobs;
		this._historical_files = historical_files;
	}
	private void setCentroids() throws Exception
	{
		this.executeClusteringAlgorithms();
		this._centroids_birch_map = this._birch_map.getCentroids();
		this._centroids_birch_reduce = this._birch_reduce.getCentroids();
		this._centroids_kmeans_map = this._kmeans_map.getCentroids();
		this._centroids_kmeans_reduce = this._kmeans_reduce.getCentroids();
	}
	public void test() throws Exception
	{
		this.setCentroids();
		fillAveragePresition();
	}
	
	public void saveOutput(String path) throws Exception
	{
		path = Config.folder_results + "/" + path;
		Helper.createFolder(path);
		Helper.writeFile(path + "/late_map.pcsv", Helper.arrayToCSV(this._avg_error_late_maps));
		Helper.writeFile(path + "/esamr_map.pcsv", Helper.arrayToCSV(this._avg_error_esamr_maps));
		Helper.writeFile(path + "/birch_map.pcsv", Helper.arrayToCSV(this._avg_error_birch_maps));
		Helper.writeFile(path + "/late_reduce.pcsv", Helper.arrayToCSV(this._avg_error_late_reduces));
		Helper.writeFile(path + "/esamr_reduce.pcsv", Helper.arrayToCSV(this._avg_error_esamr_reduces));
		Helper.writeFile(path + "/birch_reduce.pcsv", Helper.arrayToCSV(this._avg_error_birch_reduces));
		
		StringBuilder sb = new StringBuilder();
		sb.append("::::::::: Map :::::::::" + "\n");
		sb.append("LATE:\t" + this._avg_error_late_map + "\n");
		sb.append("ESAMR:\t" + this._avg_error_esamr_map + "\n");
		sb.append("ABIRCH:\t" + this._avg_error_birch_map + "\n");
		sb.append("::::::::: Reduce :::::::::" + "\n");
		sb.append("LATE:\t" + this._avg_error_late_reduce + "\n");
		sb.append("ESAMR:\t" + this._avg_error_esamr_reduce + "\n");
		sb.append("ABIRCH:\t" + this._avg_error_birch_reduce + "\n");
		Helper.writeFile(path + "/results.txt", sb.toString());
	}
	
	public void saveClusteringTechniques(String path) throws Exception
	{
		Helper.createFolder(path + "/kmeans/map");
		Helper.createFolder(path + "/kmeans/reduce");
		Helper.createFolder(path + "/birch/map");
		Helper.createFolder(path + "/birch/reduce");
		this._kmeans_map.saveReport(path + "/kmeans/map", false);
		this._kmeans_reduce.saveReport(path + "/kmeans/reduce", false);
		this._birch_map.saveReport(path + "/birch/map", false);
		this._birch_reduce.saveReport(path + "/birch/reduce", false);
	}
	
	private void fillAveragePresition() throws Exception
	{
		String map = "", reduce = "";
		int jobs_size = this._jobs.size();
		for(int i = 0; i < this._tests_size; i++)
		{
			for(int j = 0; j < jobs_size; j++)
			{
				int file_id = getFileIdByJob(j);
				map = getFileMapById(j, file_id);
				reduce = getFileReduceById(j, file_id);
				addWeightsAveragesMap(j, i, map);
				addWeightsAveragesReduce(j, i, reduce);
			}
		}
		this._avg_error_late_map = Helper.round(Helper.getAverageMatrix(this._avg_error_late_maps) * 100, Config.number_of_decimals);
		this._avg_error_esamr_map = Helper.round(Helper.getAverageMatrix(this._avg_error_esamr_maps) * 100, Config.number_of_decimals);
		this._avg_error_birch_map = Helper.round(Helper.getAverageMatrix(this._avg_error_birch_maps) * 100, Config.number_of_decimals);
		this._avg_error_late_reduce = Helper.round(Helper.getAverageMatrix(this._avg_error_late_reduces) * 100, Config.number_of_decimals);
		this._avg_error_esamr_reduce = Helper.round(Helper.getAverageMatrix(this._avg_error_esamr_reduces) * 100, Config.number_of_decimals);
		this._avg_error_birch_reduce = Helper.round(Helper.getAverageMatrix(this._avg_error_birch_reduces) * 100, Config.number_of_decimals);
	}
	
	private void addWeightsAveragesMap(int job_id, int index, String path) throws Exception
	{
		Point reference = Helper.getPointRandomInFile(path, 1, this._numberOfTasksMap, 1);
		WMap wlate = getWeightsMap();
		WMap wkm = getWeightsMap(reference, this._centroids_kmeans_map);
		WMap wbirch = getWeightsMap(reference, this._centroids_birch_map);
		double sum_error_late = 0, sum_error_km = 0, sum_error_birch = 0; 
		ArrayList<Point> weights = ronald.tesis.point.Helper.fileToPoints(path, 1);
		
		int size = weights.size();
		for(int i = 0; i < size; i++)
		{
			WMap wreal = new WMap(weights.get(i));
			
			int min_phase = wreal.getM1() > 0 ? 0 : 1;
			int phase = Helper.getRandom(min_phase, 1);
			int min_sp = phase == 0 ? 1 : 0;
			double sub_ps = Helper.getRandom(min_sp, 100) / 100;
			
			double ps_real = getPSMap(wreal, phase, sub_ps);
			double ps_late = getPSMap(wlate, phase, sub_ps);
			double ps_birch = getPSMap(wbirch, phase, sub_ps);
			double ps_km = getPSMap(wkm, phase, sub_ps);
			sum_error_late += Math.abs(ps_real - ps_late);
			sum_error_km += Math.abs(ps_real - ps_km);
			sum_error_birch += Math.abs(ps_real - ps_birch);
		}
		
		this._avg_error_late_maps[job_id][index] = sum_error_late / this._numberOfTasksMap;
		this._avg_error_esamr_maps[job_id][index] = sum_error_km / this._numberOfTasksMap;
		this._avg_error_birch_maps[job_id][index] = sum_error_birch / this._numberOfTasksMap;
	}
	
	private void addWeightsAveragesReduce(int job_id, int index, String path) throws Exception
	{
		Point reference_3d = Helper.getPointRandomInFile(path, 1, this._numberOfTasksReduce, 3);
		Point reference_2d = new Point(reference_3d.getValues()[0], reference_3d.getValues()[1]);
		
		WReduce wlate = getWeightsReduce(), 
				wkm = getWeightsReduce(reference_2d, this._centroids_kmeans_reduce), 
				wbirch = getWeightsReduce(reference_3d, this._centroids_birch_reduce);
		double sum_error_late = 0, sum_error_km = 0, sum_error_birch = 0; 
		
		ArrayList<Point> weights = ronald.tesis.point.Helper.fileToPoints(path, 3);
		int size = weights.size();
		for(int i = 0; i < size; i++)
		{
			WReduce wreal = new WReduce(weights.get(i));
			int phase = Helper.getRandom(0, 2);
			int min_sp = phase == 0 ? 1 : 0;
			double sub_ps = Helper.getRandom(min_sp, 100) / 100;
			double ps_real = getPSReduce(wreal, phase, sub_ps);
			double ps_late = getPSReduce(wlate, phase, sub_ps);
			double ps_birch = getPSReduce(wbirch, phase, sub_ps);
			double ps_km = getPSReduce(wkm, phase, sub_ps);
			sum_error_late += Math.abs(ps_real - ps_late); 
			sum_error_km += Math.abs(ps_real - ps_km); 
			sum_error_birch += Math.abs(ps_real - ps_birch);
		}
		
		this._avg_error_late_reduces[job_id][index] = sum_error_late / this._numberOfTasksReduce;
		this._avg_error_esamr_reduces[job_id][index] = sum_error_km / this._numberOfTasksReduce;
		this._avg_error_birch_reduces[job_id][index] = sum_error_birch / this._numberOfTasksReduce;
	}
	
	private static double getPSMap(WMap wmap, int phase, double sub_ps)
	{
		double ps = -1;
		if(phase == 0) { ps = wmap.getM1() * sub_ps; }
		else { ps = wmap.getM1() + wmap.getM2() * sub_ps; }
		return ps;
	}
	private static double getPSReduce(WReduce wreduce, int phase, double sub_ps)
	{
		double ps = -1;
		if(phase == 0) { ps = wreduce.getR1() * sub_ps; }
		else if(phase == 1) { ps = wreduce.getR1() + wreduce.getR2() * sub_ps; }
		else { ps = wreduce.getR1() + wreduce.getR2() + wreduce.getR3() * sub_ps; }
		return ps;
	}
	private static WMap getWeightsMap(Point reference, Point centroids[]) throws Exception
	{
		int size = centroids.length, min_index = -1;
		double min_distance = Double.MAX_VALUE;
		for(int i = 0; i < size; i++)
		{
			double distance = reference.getDistance(centroids[i]);
			if(distance < min_distance) { min_distance = distance; min_index = i; }
		}
		return new WMap(centroids[min_index].getValues()[0]);
	}
	private static WMap getWeightsMap() { return new WMap(1); }
	
	private static WReduce getWeightsReduce(Point p, Point centroids[]) throws Exception
	{
		int size = centroids.length, min_index = -1;
		double min_distance = Double.MAX_VALUE;
		for(int i = 0; i < size; i++)
		{
			double distance = p.getDistance(centroids[i]);
			if(distance < min_distance) { min_distance = distance; min_index = i; }
		}
		return new WReduce(centroids[min_index].getValues()[0], centroids[min_index].getValues()[1]);
	}
	private static WReduce getWeightsReduce() { return new WReduce(0.33, 0.33); }
	
	public double getAverageErrorLateMap() { return this._avg_error_late_map; }
	public double getAverageErrorLateReduce() { return this._avg_error_late_reduce; }
	public double getAverageErrorEsamrMap() { return this._avg_error_esamr_map; }
	public double getAverageErrorEsamrReduce() { return this._avg_error_esamr_reduce; }
	public double getAverageErrorBirchMap() { return this._avg_error_birch_map; }
	public double getAverageErrorBirchReduce() { return this._avg_error_birch_reduce; }
	
	public static void multiTest(String path, int repetitions, Comparative comparatives[]) throws Exception
	{
		String _path = Config.folder_results + "/" + path;
		int length = comparatives.length;
		double results_map[][] = new double[_schedulers_size][length];
		double results_reduce[][] = new double[_schedulers_size][length];
				
		for(int i = 0; i < length; i++)
		{
			System.out.println("i: " + i);
			double late_map = 0, late_reduce = 0, esamr_map = 0, esamr_reduce = 0, birch_map = 0, birch_reduce = 0; 
			for(int j = 0; j < repetitions; j++)
			{
				System.out.print(j + " ");
				Comparative comparative = comparatives[i];
				comparative.test();
				late_map +=  comparative.getAverageErrorLateMap();
				esamr_map +=  comparative.getAverageErrorEsamrMap();
				birch_map +=  comparative.getAverageErrorBirchMap();
				late_reduce +=  comparative.getAverageErrorLateReduce();
				esamr_reduce +=  comparative.getAverageErrorEsamrReduce();
				birch_reduce +=  comparative.getAverageErrorBirchReduce();
//				comparative.saveClusteringTechniques(path + "/" + comparative.getId() + "_" + j);
			}
			System.out.println();
			results_map[0][i] = late_map / repetitions;
			results_map[1][i] = esamr_map / repetitions;
			results_map[2][i] = birch_map / repetitions;
			results_reduce[0][i] = late_reduce / repetitions;
			results_reduce[1][i] = esamr_reduce / repetitions;
			results_reduce[2][i] = birch_reduce / repetitions;;
		}
		
		Helper.createFolder(_path);
		Helper.writeFile(_path + "/map", Helper.arrayToCSV(results_map));
		Helper.writeFile(_path + "/reduce", Helper.arrayToCSV(results_reduce));
	}
	// Map 8:5, Reduce 4:13
	public static void findParameters(String path, int repetitions, double Ts_map[], int Bs_map[], int Ls_map[], double Ts_reduce[], int Bs_reduce[], int Ls_reduce[], int historical_size, int tests_size, int min_id, int max_id, int number_of_tasks_map, int number_of_tasks_reduce, ArrayList<String> jobs) throws Exception
	{
		int length = Ts_map.length;
		if(Bs_map.length != length || Ls_map.length != length || Ts_reduce.length != length || Bs_reduce.length != length || Ls_reduce.length != length)
		{
			throw new Exception("The sizes of Ts, Bs or Ls are differents");
		}
		else
		{
			String _path = Config.folder_results + "/" + path;
			double results_map[] = new double[length], results_reduce[] = new double[length];
			String id = Helper.getUniqueId();
			for(int i = 0; i < length; i++)
			{
				System.out.println("B_map: " + Bs_map[i]);
				System.out.println("L_map: " + Ls_map[i]);
				System.out.println("B_reduce: " + Bs_reduce[i]);
				System.out.println("L_reduce: " + Ls_reduce[i]);
				
				double total_map = 0, total_reduce = 0;
				for(int j = 0; j < repetitions; j++)
				{
					System.out.print(j + " ");
					Comparative comparative = new Comparative(historical_size, tests_size, min_id, max_id, number_of_tasks_map, number_of_tasks_reduce, jobs);
					comparative.setTMap(Ts_map[i]);
					comparative.setBMap(Bs_map[i]);
					comparative.setLMap(Ls_map[i]);
					comparative.setTReduce(Ts_reduce[i]);
					comparative.setBReduce(Bs_reduce[i]);
					comparative.setLReduce(Ls_reduce[i]);
					comparative.setId(id + "_" + i + "_" + j);
					comparative.test();
					total_map += comparative.getAverageErrorBirchMap();
					total_reduce += comparative.getAverageErrorBirchReduce();
				}
				
				results_map[i] += total_map / repetitions;
				results_reduce[i] += total_reduce / repetitions;

				System.out.println();
			}
			double results[][] = new double[2][length];
			results[0] = results_map;
			results[1] = results_reduce;
			Helper.createFolder(_path);
			Helper.writeFile(_path + "/parameters.txt", Helper.arrayToCSV(results));
		}
	}
	
	/* GyS */
	public void setTMap(double T_map) { this._T_map = T_map; }
	public void setBMap(int B_map) { this._B_map = B_map; }
	public void setLMap(int L_map) { this._L_map = L_map; }
	
	public void setTReduce(double T_reduce) { this._T_reduce = T_reduce; }
	public void setBReduce(int B_reduce) { this._B_reduce = B_reduce; }
	public void setLReduce(int L_reduce) { this._L_reduce = L_reduce; }
	
	public void setId(String id) { this._id = id; }
	public String getId() { return this._id; }
	public BIRCH getBIRCHMap() { return this._birch_map; }
	public BIRCH getBIRCHReduce() { return this._birch_reduce; }
	public KMeans getKMeansMap() { return this._kmeans_map; }
	public KMeans getMeansReduce() { return this._kmeans_reduce; }
	/* End GyS */
}