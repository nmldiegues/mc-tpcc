package pt.inesc.gsd.evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseResults {

    public static final int[] POSSIBLE_THREADS = { 1, 2, 4, 8, 16, 24, 32, 48};
    public static final String[] MODES = { "" };
    public static final String[] BENCHMARKS = { 
	"n0-p0-o100", "n5-p5-o90", "n15-p15-o70", "n0-p10-o90", "n10-p0-o90", "n0-p30-o70", "n30-p0-o70"
    };
    public static final int ATTEMPTS = 3;

    public static class Result {
	public double commitsPerSec = 0.0;
	public double abortsPerSec = 0.0;
	public int attempts = ATTEMPTS;
	public Result() { }
    }

    public static void main(String[] args) {
	// mode -> benchmark -> threads
	Map<String, Map<String, Map<Integer, Result>>> perMode = new HashMap<String, Map<String, Map<Integer, Result>>>();
	for (String mode : MODES) {
	    Map<String, Map<Integer, Result>> perBenchmark = new HashMap<String, Map<Integer, Result>>();
	    for (String benchmark : BENCHMARKS) {
		Map<Integer, Result> perThreads = new HashMap<Integer, Result>();
		for (int threads : POSSIBLE_THREADS) {
		    Result result = new Result();
		    for (int a = 0; a < ATTEMPTS; a++) {
			List<String> content = getFileContent(args[0] + "/1-" + benchmark + "-" + threads + "-" + (a+1) + ".data");
			result.commitsPerSec += getCommitsPerSec(benchmark, content);
			result.abortsPerSec += getAbortsPerSec(benchmark, content);
		    }
		    result.commitsPerSec = result.commitsPerSec / result.attempts;
		    result.abortsPerSec = result.abortsPerSec / result.attempts;
		    perThreads.put(threads, result);
		}
		perBenchmark.put(benchmark, perThreads);
	    }
	    perMode.put(mode, perBenchmark);
	}

	for (String benchmark : BENCHMARKS) {
	    String throughput = "-";
	    for (String mode : MODES) {
		throughput += " JVSTM";
	    }
	    for (int threads : POSSIBLE_THREADS) {
		throughput += "\n" + threads;
		for (String mode : MODES) {
		    double commitsPerSec = perMode.get(mode).get(benchmark).get(threads).commitsPerSec;
		    double abortsPerSec = perMode.get(mode).get(benchmark).get(threads).abortsPerSec;
		    throughput += " " + roundTwoDecimals(commitsPerSec) + " " + roundTwoDecimals(abortsPerSec);
		}
	    }
	    writeToFile(args[0] + "/results/warehouses-1-" + benchmark + "-throughput+aborts.output", throughput);
	}

    }

    private static void writeToFile(String filename, String content) {
	try {
	    FileWriter fstream = new FileWriter(filename);
	    BufferedWriter out = new BufferedWriter(fstream);
	    out.write(content);
	    out.close();
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    private static List<String> getFileContent(String filename) {
	List<String> testLines1 = new ArrayList<String>();
	try {
	    FileInputStream is = new FileInputStream(filename);
	    DataInputStream in = new DataInputStream(is);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
	    String strLine;
	    while ((strLine = br.readLine()) != null) {
		if (strLine.equals("")) {
		    continue;
		}
		testLines1.add(strLine);
	    }
	    br.close();
	} catch (Exception e) {
	    //	    return testLines1;
	    e.printStackTrace();
	    System.exit(1);
	}
	return testLines1;
    }

    private static double roundTwoDecimals(double d) {
	if (d < 0.1) {
	    DecimalFormat twoDForm = new DecimalFormat("#.####");
	    return Double.valueOf(twoDForm.format(d));
	} else {
	    DecimalFormat twoDForm = new DecimalFormat("#.##");
	    return Double.valueOf(twoDForm.format(d));
	}
    }

    private static double getCommitsPerSec(String benchmark, List<String> content) {
	double total = 0.0;
	for (String l : content) {
	    if (l.contains("REQ_PER_SEC")) {
		String[] parts = l.split(" ");
		total += Double.parseDouble(parts[1]);
		return total;
	    } 
	}
	return total;
    }
    
    private static double getAbortsPerSec(String benchmark, List<String> content) {
	double total = 0.0;
	for (String l : content) {
	    if (l.contains("FAILURES")) {
		String[] parts = l.split(" ");
		total += Double.parseDouble(parts[1]);
	    } 
	}
	long timeMsec = 0l;
	for (String l : content) {
	    if (l.contains("DURATION")) {
		String[] parts = l.split(" ");
		timeMsec += Long.parseLong(parts[2]);
	    } 
	}
	return total == 0 ? 0.0 : total / (timeMsec / 1000.0);
    }

}
