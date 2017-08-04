package cc.heroy.douban.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cc.heroy.douban.bean.Movie;
import cc.heroy.douban.thread.URLAnalyzer;
import cc.heroy.douban.thread.URLSpider;
import cc.heroy.douban.util.HttpClientUtil;
import cc.heroy.douban.util.JSONUtil;

/**
 * 功能 ：爬取豆瓣-分类中的电影信息 
 * 内容：电影名称，评分，剧情简介
 * 使用技术：HttpClient , 单线程  
 */
public class CategoryMovie {
	
	//电影分类首页（电影-剧情-美国-经典）
	private final String category_index = "https://movie.douban.com/j/new_search_subjects?sort=T&range=0,10&tags=%E7%94%B5%E5%BD%B1,%E7%BE%8E%E5%9B%BD,%E5%89%A7%E6%83%85,%E7%BB%8F%E5%85%B8&start=";
	//最多查询页数（每页20个数据）
	private final int maxPage = 1 ;
	//记录每一条已经查询的电影详情的url（去重）
	List<String> used_url = new ArrayList<String>();
	//豆瓣详情页的url容器，作为 生产者消费者 的容器使用
	BlockingQueue<String> urls = new ArrayBlockingQueue<String>(200);
	//获取的页面实体
	BlockingQueue<String> entitys = new ArrayBlockingQueue<String>(200);
	//被使用过的url
	CopyOnWriteArraySet<String> usedURLS = new CopyOnWriteArraySet<>();
	
	//储存获取的json对象
	List<JSONObject> jsons = new ArrayList<JSONObject>();
	//储存获取的movie对象
	List<Movie> movies = new ArrayList<Movie>();
	//线程池(后期添加线程日志)
	ExecutorService pool = Executors.newFixedThreadPool(10);
	//URLSpider线程数
	private final int spiderCount = 3;
	//URLAnalyzer线程数
	private final int urlAnalyzerCount = 3 ;
	
	//URLSpider 的 二元闭锁
	int spiderStartGateNum = 1 ;
	int spiderEndGateNum = spiderStartGateNum*spiderCount;
	
	//URLAnalyzer的二元闭锁
	int urlAnalyzerStartGateNum = 1;
	int urlAnalyzerEndGateNum = urlAnalyzerStartGateNum*urlAnalyzerCount;
	
	CountDownLatch spiderStartGate = new CountDownLatch(spiderStartGateNum);
	CountDownLatch spiderEndGate = new CountDownLatch(spiderEndGateNum);
	
	CountDownLatch urlAnalyzerStartGate = new CountDownLatch(urlAnalyzerStartGateNum);
	CountDownLatch urlAnalyzerEndGate = new CountDownLatch(urlAnalyzerEndGateNum);

	private void spider(){
		
		//开始时间
long begin_time = System.currentTimeMillis();
		
		//获取单个httpClient
		CloseableHttpClient httpClient = HttpClientUtil.getHttpClient();
		
		//重复请求地址，获取json对象
		for(int i = 0 ;i<maxPage ;i++){
		//坑爹的豆瓣，每多一页就加一个新数据，有19个重复数据
		//设置Post
		HttpGet get = new HttpGet(category_index+i*20);
		try{
			CloseableHttpResponse response = httpClient.execute(get);
			//处理请求
			String content = EntityUtils.toString(response.getEntity());
			JSONObject j = JSONUtil.toJSONObject(content);
			jsons.add(j);
		}catch(Exception e){
			e.printStackTrace();
		}
		}
		
		JSONObject jt = null;
		//遍历jsons
		for(JSONObject obj : jsons){
			JSONArray json =(JSONArray)obj.get("data");
				Iterator<Object> it = json.iterator();
				while(it.hasNext()){
					Movie m = new Movie();
					jt = (JSONObject)it.next();
					m.setCover((String)jt.get("cover"));
					m.setTitle((String)jt.get("title"));
					m.setRate((String)jt.get("rate"));
					m.setUrl((String)jt.get("url"));
					try {
						urls.put((String)jt.get("url"));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					movies.add(m);
				}
		}
		//取得所有的movie
//		for(Movie m : movies){
//			System.out.println(m);
//		}
		
		//进入每个详情页面
		String durl = null ;
		//创建并执行消费者(使用线程池)
		for(int i = 0;i<spiderCount;i++){
			pool.submit(new Thread(new URLSpider(httpClient,urls,entitys,spiderStartGate,spiderEndGate)));
		}
		//开始URLSpider
		spiderStartGate.countDown();
		//查询描述
/*		for(Movie m : movies){
			durl = m.getUrl();
			HttpGet httpGet = new HttpGet(durl);
			try{
			HttpResponse response = httpClient.execute(httpGet);
			String content = EntityUtils.toString(response.getEntity());
			//解析页面
			Pattern pattern = Pattern.compile("class=\"all hidden\">([\\s\\S]*?)</span>");
			Matcher matcher = pattern.matcher(content);
			if(matcher.find()){
				String str = matcher.group();
				str = str.replaceAll("\n", "").substring(19,str.length()-9);
				m.setContent(str.trim());
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		}*/
		//执行结束时间

//结束
try {
	spiderEndGate.await();
} catch (InterruptedException e1) {
	e1.printStackTrace();
}
		/*//取得所有的movie
		for(Movie m : movies){
			System.out.println(m);
		}*/
		
//添加URLAnalyzer
long end_time = System.currentTimeMillis();
System.out.println(end_time-begin_time);
		
		for(int i = 0;i<urlAnalyzerCount;i++){
			pool.submit(new Thread(new URLAnalyzer(entitys, urls, usedURLS,urlAnalyzerStartGate,urlAnalyzerEndGate)));
		}
		try {
			urlAnalyzerStartGate.countDown();
			urlAnalyzerEndGate.await();
System.out.println("OK");
			httpClient.close();
//必须关闭线程池
			pool.shutdownNow();
		} catch (IOException e) {
			e.printStackTrace();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new CategoryMovie().spider();
	}
}


