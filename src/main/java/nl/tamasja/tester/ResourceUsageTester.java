package nl.tamasja.tester;

import nl.tamasja.searchprovider.ISearchProvider;
import nl.tamasja.tools.Profiler;
import nl.tamasja.tools.log.ILog;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TIS 31-8-2014.1:55
 */
public class ResourceUsageTester implements ITester {

    protected ILog log;
    protected ILog resultLog;
    protected ISearchProvider searchProvider;

    public ResourceUsageTester(ILog log, ILog resultLog, ISearchProvider searchProvider) {
        this.log = log;
        this.resultLog = resultLog;
        this.searchProvider = searchProvider;
    }

    @Override
    public void writeHeader() {
        this.resultLog.write("date,label,count,totalMemoryUsage,totalSwapUsage,totalDiskUsage,nodes,aliveNodes,avgMemoryUsage,avgSwapUsage,avgDiskUsage");
    }

    @Override
    public void runTest(String label) {

        try {

            Profiler profiler = new Profiler();

            this.log.write("Starting ResourceUsageTest..");

            this.searchProvider.commit();
            Thread.sleep(1000);
            long count = this.searchProvider.countTotal();
            Thread.sleep(1000);

            profiler.start();


            long nodeNumber = this.searchProvider.getNodeNumber();
            long liveNodeNumber = this.searchProvider.getLiveNodeNumber();

            long totalMemoryUsage = this.searchProvider.getTotalMemoryUsage();
            long totalSwapUsage = this.searchProvider.getTotalSwapUsage();
            long totalDiskUsage = this.searchProvider.getTotalDiskUsage();

            long avgMemoryUsage = totalMemoryUsage / nodeNumber;
            long avgSwapUsage = totalSwapUsage / nodeNumber;
            long avgDiskUsage = totalDiskUsage / nodeNumber;


            this.resultLog.write(
                    ""
                            + new SimpleDateFormat("dd/MM/yyyy H:mm:ss").format(new Date())
                            + "," + label
                            + "," + String.format("%d", (long) count)
                            + "," + String.format("%d", totalMemoryUsage)
                            + "," + String.format("%d", totalSwapUsage)
                            + "," + String.format("%d", totalDiskUsage)
                            + "," + String.format("%d", nodeNumber)
                            + "," + String.format("%d", liveNodeNumber)
                            + "," + String.format("%d", avgMemoryUsage)
                            + "," + String.format("%d", avgSwapUsage)
                            + "," + String.format("%d", avgDiskUsage)

            );

            profiler.stop();

            this.log.write("ResourceUsageTest Complete in " + profiler.getRuntime() + " (" + profiler.getRunTimeSeconds() + "s)");

        } catch (Exception e) {
            this.log.write("Exception in ResourceUsageTest: " + e.getMessage() + " - " + e.toString());
            e.printStackTrace();
        }
    }
}
