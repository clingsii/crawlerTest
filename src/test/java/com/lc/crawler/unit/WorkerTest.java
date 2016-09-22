package com.lc.crawler.unit;

import com.lc.crawler.BaseTest;
import com.lc.crawler.processor.CrawlerWorker;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by Ling Cao on 2016/8/16.
 */
public class WorkerTest extends BaseTest {

    public static final DateFormat df = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private CrawlerWorker worker;

    @Test
    public void testWorker() {
        FutureTask<Integer> future = worker.doFetch("http://www.sli-demo.com/home-decor/electronics/madison-rx3400.html");
        while (!future.isDone()) {
            System.out.println("not done yet : " + df.format(new Date()));
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            System.out.println("Finished. Total cnt: " + future.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
