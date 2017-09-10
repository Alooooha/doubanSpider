package cc.heroy.douban.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.impl.client.CloseableHttpClient;

import com.alibaba.fastjson.JSONObject;

import cc.heroy.douban.bean.Movie;
import cc.heroy.douban.task.HTMLAnalyzer;
import cc.heroy.douban.task.URLAnalyzer;
import cc.heroy.douban.task.URLSpider;
import cc.heroy.douban.util.HttpClientUtil;

/**
 * 功能 ：爬取豆瓣-分类中的电影信息 内容：电影名称，评分，剧情简介 使用技术：HttpClient , 单线程
 */
public class CategoryMovie {

	//问题： 容器装满了
	
	// 电影分类首页（电影-剧情-美国-经典）
//	private final String category_index = "https://movie.douban.com/j/new_search_subjects?sort=T&range=0,10&tags=%E7%94%B5%E5%BD%B1,%E7%BE%8E%E5%9B%BD,%E5%89%A7%E6%83%85,%E7%BB%8F%E5%85%B8&start=";
	// 电影类型首页 （电影）
//	String category_index = "https://movie.douban.com/j/new_search_subjects?sort=T&range=0,10&tags=%E7%94%B5%E5%BD%B1&start=";
	
	//具体电影详情url
	String durl = "https://movie.douban.com/subject/1851857/";
	String durl1 = "https://movie.douban.com/subject/1862151/";
	String durl2 = "https://movie.douban.com/subject/1291561/";
	
	// 豆瓣详情页的url容器，作为 生产者消费者 的容器使用
	BlockingQueue<String> urls = new ArrayBlockingQueue<String>(400000);
	// 获取的页面实体(URLAnalyzer使用)
	BlockingQueue<String> entitys1 = new ArrayBlockingQueue<String>(200000);
	// 获取的页面实体(HTMLAnalyzer使用)
	BlockingQueue<String> entitys2 = new ArrayBlockingQueue<String>(200000);
	// 被使用过的url(去重)
	CopyOnWriteArraySet<String> usedURLS = new CopyOnWriteArraySet<>();
	// 储存获取的json对象
	List<JSONObject> jsons = new ArrayList<JSONObject>();
	// 储存获取的movie对象（理解Vector）
	Vector<Movie> movies = new Vector<Movie>(200);
	// 线程池(后期添加线程日志)
	ExecutorService pool = Executors.newFixedThreadPool(16);
	// URLSpider线程数
	private final int spiderCount = 3;
	// URLAnalyzer线程数
	private final int urlAnalyzerCount = 1;
	// HTMLAnalyzer线程数
	private final int HTMLAnalyzerCount = 1;
	

	// URLSpider 的 二元闭锁
	int spiderStartGateNum = 1;
	int spiderEndGateNum = spiderStartGateNum * spiderCount;

	// URLAnalyzer的二元闭锁
	int urlAnalyzerStartGateNum = 1;
	int urlAnalyzerEndGateNum = urlAnalyzerStartGateNum * urlAnalyzerCount;

	// HTMLAnalyzer的二元闭锁
	int HTMLAnalyzerStartGateNum = 1;
	int HTMLAnalyzerEndGateNum = HTMLAnalyzerStartGateNum * HTMLAnalyzerCount;
	
	CountDownLatch spiderStartGate = new CountDownLatch(spiderStartGateNum);
	CountDownLatch spiderEndGate = new CountDownLatch(spiderEndGateNum);

	CountDownLatch urlAnalyzerStartGate = new CountDownLatch(urlAnalyzerStartGateNum);
	CountDownLatch urlAnalyzerEndGate = new CountDownLatch(urlAnalyzerEndGateNum);

	CountDownLatch HTMLAnalyzerStartGate = new CountDownLatch(HTMLAnalyzerStartGateNum);
	CountDownLatch HTMLAnalyzerEndGate = new CountDownLatch(HTMLAnalyzerEndGateNum);
	
	CountDownLatch urlSpiderStartGate = new CountDownLatch(spiderStartGateNum);
	CountDownLatch urlSpiderEndGate = new CountDownLatch(spiderEndGateNum);
	
	private void spider() {

		// 开始时间
		long begin_time = System.currentTimeMillis();

		// 获取单个httpClient
		CloseableHttpClient httpClient = HttpClientUtil.getHttpClient();

		try {
			urls.put(durl);
			urls.put(durl1);
			urls.put(durl2);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// 同时启动URLAnalyzer和HTMLAnalyzer,URLSpider
		
		for (int i = 0; i < urlAnalyzerCount; i++) {
			pool.submit(new Thread(new URLAnalyzer(entitys1,entitys2, urls, usedURLS, urlAnalyzerStartGate, urlAnalyzerEndGate)));
		}
		
		for(int i = 0;i < HTMLAnalyzerCount;i++){
			pool.submit(new Thread(new HTMLAnalyzer(entitys1,entitys2, urls ,usedURLS,HTMLAnalyzerStartGate,HTMLAnalyzerEndGate,movies)));
		}
		
		for(int i = 0;i < spiderCount ;i++) {
			pool.submit(new Thread(new URLSpider(httpClient, urls, entitys1, entitys2, urlSpiderStartGate, urlSpiderEndGate)));
		}
		
		try {
			urlSpiderStartGate.countDown();
			Thread.sleep(5000);
			urlAnalyzerStartGate.countDown();
			Thread.sleep(5000);
			HTMLAnalyzerStartGate.countDown();
			urlSpiderEndGate.await();
			urlAnalyzerEndGate.await();
			HTMLAnalyzerEndGate.await();
			
			for(Movie m : movies){
//				System.out.println("爬取到的电影信息 ："+"《"+m.getTitle()+"》"+" 剧情 ："+m.getStory());
				System.out.println(m.getTitle()+"  类型:"+m.getType());
			}
			long end_time = System.currentTimeMillis();
System.out.println("待访问的url数量  ："+urls.size());
System.out.println("已访问的url数量  ："+usedURLS.size());
			System.out.println("结束时间"+(end_time - begin_time));
	
			httpClient.close();
			// 必须关闭线程池
			pool.shutdownNow();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		new CategoryMovie().spider();
	}
}
