package cc.heroy.douban.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cc.heroy.douban.bean.Movie;
import cc.heroy.douban.thread.HTMLAnalyzer;
import cc.heroy.douban.thread.URLAnalyzer;
import cc.heroy.douban.thread.URLSpider;
import cc.heroy.douban.util.HttpClientUtil;
import cc.heroy.douban.util.JSONUtil;

/**
 * 功能 ：爬取豆瓣-分类中的电影信息 内容：电影名称，评分，剧情简介 使用技术：HttpClient , 单线程
 */
public class CategoryMovie {

	// 电影分类首页（电影-剧情-美国-经典）
	private final String category_index = "https://movie.douban.com/j/new_search_subjects?sort=T&range=0,10&tags=%E7%94%B5%E5%BD%B1,%E7%BE%8E%E5%9B%BD,%E5%89%A7%E6%83%85,%E7%BB%8F%E5%85%B8&start=";
	// 最多查询页数（每页20个数据）
	private final int maxPage = 5;
	// 记录每一条已经查询的电影详情的url（去重）
//	List<String> used_url = new ArrayList<String>();
	// 豆瓣详情页的url容器，作为 生产者消费者 的容器使用
	BlockingQueue<String> urls = new ArrayBlockingQueue<String>(500);
	// 获取的页面实体(URLAnalyzer使用)
	BlockingQueue<String> entitys1 = new ArrayBlockingQueue<String>(500);
	// 获取的页面实体(HTMLAnalyzer使用)
	BlockingQueue<String> entitys2 = new ArrayBlockingQueue<String>(500);
	// 被使用过的url
	CopyOnWriteArraySet<String> usedURLS = new CopyOnWriteArraySet<>();
	// 储存获取的json对象
	List<JSONObject> jsons = new ArrayList<JSONObject>();
	// 储存获取的movie对象（理解Vector）
	Vector<Movie> movies = new Vector<Movie>(200);
	// 线程池(后期添加线程日志)
	ExecutorService pool = Executors.newFixedThreadPool(10);
	// URLSpider线程数
	private final int spiderCount = 3;
	// URLAnalyzer线程数
	private final int urlAnalyzerCount = 3;
	// HTMLAnalyzer线程数
	private final int HTMLAnalyzerCount = 3;
		
	

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
	
	
	private void spider() {

		// 开始时间
		long begin_time = System.currentTimeMillis();

		// 获取单个httpClient
		CloseableHttpClient httpClient = HttpClientUtil.getHttpClient();

		// 重复请求地址，获取json对象
		for (int i = 0; i < maxPage; i++) {
			// 坑爹的豆瓣，每多一页就加一个新数据，有19个重复数据
			// 设置Post
			HttpGet get = new HttpGet(category_index + i * 20);
			try {
				CloseableHttpResponse response = httpClient.execute(get);
System.out.println("请求豆瓣接口 : page = "+(i+1));
				// 处理请求
				String content = EntityUtils.toString(response.getEntity());
				JSONObject j = JSONUtil.toJSONObject(content);
				jsons.add(j);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		JSONObject jt = null;
		// 遍历jsons
		for (JSONObject obj : jsons) {
			JSONArray json = (JSONArray) obj.get("data");
			Iterator<Object> it = json.iterator();
			while (it.hasNext()) {
				jt = (JSONObject) it.next();
				try {
					urls.put((String) jt.get("url"));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		// 取得所有的movie
		// for(Movie m : movies){
		// System.out.println(m);
		// }

		// 创建并执行消费者(使用线程池)
		for (int i = 0; i < spiderCount; i++) {
			pool.submit(new Thread(new URLSpider(httpClient, urls, entitys1,entitys2, spiderStartGate, spiderEndGate)));
		}
		// 开始URLSpider
		spiderStartGate.countDown();

		// 结束
		try {
			spiderEndGate.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		/*
		 * //取得所有的movie for(Movie m : movies){ System.out.println(m); }
		 */

//System.out.println(entitys);

		// 同时启动URLAnalyzer和HTMLAnalyzer
		
		for (int i = 0; i < urlAnalyzerCount; i++) {
			pool.submit(new Thread(new URLAnalyzer(entitys1, urls, usedURLS, urlAnalyzerStartGate, urlAnalyzerEndGate)));
		}
		
		for(int i = 0;i < HTMLAnalyzerCount;i++){
			pool.submit(new Thread(new HTMLAnalyzer(entitys2,HTMLAnalyzerStartGate,HTMLAnalyzerEndGate,movies)));
		}
		
		try {
			urlAnalyzerStartGate.countDown();
			HTMLAnalyzerStartGate.countDown();
			urlAnalyzerEndGate.await();
			HTMLAnalyzerEndGate.await();
			
			for(Movie m : movies){
				System.out.println("爬取到的电影信息 ："+"《"+m.getTitle()+"》"+" 剧情 ："+m.getStory());
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
