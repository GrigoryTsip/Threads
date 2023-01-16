import java.util.*;
import java.util.concurrent.*;

public class Main {

    protected static List<Future<Integer>> poolTask = new ArrayList<>();

    protected static String[] texts = new String[25];
    protected static String threadName;
    protected static String textThread;


    public static void main(String[] args) throws InterruptedException, ExecutionException {

        Callable<Integer> value = () -> {
            String text = textThread;
            String name = threadName;

            int maxSize = 0;
            for (int i = 0; i < text.length(); i++) {
                for (int j = 0; j < text.length(); j++) {
                    if (i >= j) {
                        continue;
                    }
                    boolean bFound = false;
                    for (int k = i; k < j; k++) {
                        if (text.charAt(k) == 'b') {
                            bFound = true;
                            break;
                        }
                    }
                    if (!bFound && maxSize < j - i) {
                        maxSize = j - i;
                    }
                }
            }
            System.out.println(name + " " + text.substring(0, 100) + " -> " + maxSize);
            return maxSize;
        };

        final ExecutorService threadPool = Executors.newFixedThreadPool(6);
        List<Integer> resultList = new ArrayList<>();

        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        long startTs = System.currentTimeMillis(); // start time
        for (int i = 0; i < texts.length; i++) {
            threadName = "thread_" + i;
            textThread = texts[i];

            Future<Integer> task = threadPool.submit(value);
            Thread.sleep(800);
            poolTask.add(task);
        }

        for (Future<Integer> task : poolTask) {
            resultList.add(task.get());
        }
        threadPool.shutdown();

        int maxValue = 0;
        for (Integer val : resultList) {
            maxValue = Math.max(maxValue, val);
        }
        System.out.println("Максимальный интервал значений: " + maxValue);
        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");
    }


    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}