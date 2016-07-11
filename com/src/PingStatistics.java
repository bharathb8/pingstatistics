import java.lang.System.*;
import java.lang.Math.*;
import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PingStatistics {

	private static int NUM_THREADS = 3;
	private static int TOTAL_REQS = 12;

	public static class FetchURL implements Runnable {

		private String URL;

		public FetchURL(String url) {
			this.URL = url;

		}

		public void run() {
			String result = "";
			int code;
			try {
				URL siteURL = new URL(this.URL);
				HttpURLConnection urlConnection = (HttpURLConnection) siteURL.openConnection();
				urlConnection.setRequestMethod("GET");
				//Timer start
				long startTime = System.currentTimeMillis();
				urlConnection.connect();
				BufferedReader contentBufferedReader = new BufferedReader(
													new InputStreamReader(urlConnection.getInputStream()));
				
				long totalElapsedTime = System.currentTimeMillis() - startTime;
				StringBuilder sb = new StringBuilder();
				if (contentBufferedReader != null) {
					int cp;
					while ((cp = contentBufferedReader.read()) != -1) {
						sb.append((char) cp);
					}
					contentBufferedReader.close();
				}

				System.out.println("Thread: " + Thread.currentThread().getId() +
					 " Total Elapsed Time : " + totalElapsedTime + " Size: " + sb.length());

				urlConnection.disconnect();
				
			} catch (Exception e) {
				System.out.println("Caught Exception : " + e);
			}
			
		}

	}

	public static void main(String[] args) {

		ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

		int total_iterations = (int) Math.ceil((double)TOTAL_REQS / NUM_THREADS);
		for (int iteration = 0; iteration < total_iterations; iteration++) {
			System.out.println("Iteration Num " + iteration);
			for (int i = 0; i < NUM_THREADS; i++) {
				executor.execute(new FetchURL("https://en.wikipedia.org/wiki/Main_Page"));
			}
		}
		executor.shutdown();
		while(!executor.isTerminated()) {
			//System.out.println("Waiting for threads to finish..");
		}
		System.out.println("Finished fetching URLs.");

	}
}