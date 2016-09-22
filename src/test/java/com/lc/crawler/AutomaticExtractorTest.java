package com.lc.crawler;

import com.lc.crawler.processor.CrawlerWorker;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by Ling Cao on 2016/8/20.
 */
public class AutomaticExtractorTest extends BaseTest {

    public static final DateFormat df = new SimpleDateFormat("HH:mm:ss");


    @Autowired
    private CrawlerWorker worker;

    @Autowired
    private AutoExtractor autoExtractor;

    public static final String SEED = "http://www.sli-demo.com/home-decor/electronics/madison-rx3400.html";

    @Test
    public void run() throws InterruptedException, ExecutionException {
        FutureTask<Integer> future = worker.doFetch(SEED);
        while (!future.isDone()) {
            System.out.println("page downloading : " + df.format(new Date()));
            Thread.sleep(5000);
        }
        System.out.println("page downloading finish. total page number is : " + future.get());
        System.out.println();
        System.out.println();

        autoExtractor.execute("./sli-test-result.txt");
        System.out.println("product info extraction finished. result file is : ./sli-test-result.txt");

    }
}
