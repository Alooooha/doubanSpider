package cc.heroy.douban.thread;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * 网页爬虫线程：
 * 	使用JUC中的BlockingQuery 实现生产者消费者模式
 *	线程从urls中取出url，然后进行页面访问，获取页面信息存入到entitys
 */
public class URLSpider implements Runnable{

	private final CloseableHttpClient httpClient ;
	private final BlockingQueue<String> urls;
	private final BlockingQueue<String> entitys ;
	private final CountDownLatch startGate ;
	private final CountDownLatch endGate ;
	
	public URLSpider(CloseableHttpClient httpCLient ,BlockingQueue<String> urls,BlockingQueue<String> entitys,CountDownLatch startGate,CountDownLatch endGate){
		this.httpClient = httpCLient;
		this.urls = urls;
		this.entitys = entitys;
		this.startGate = startGate;
		this.endGate = endGate ;
	}
	
	@Override
	public void run() {
		System.out.println("******");
		//等待startGate = 0
System.out.println(startGate.getCount());
		try{
		startGate.await();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		
		while(!urls.isEmpty()){
			//take()方法在urls没数据时阻塞
			try {
				String url = urls.take();
				spider(url);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}finally{
				
			}
		}
		//endGate-1
		endGate.countDown();
		System.out.println("结束");
	}
	
	private void spider(String url){
System.out.println(url);
		HttpGet get = new HttpGet(url);
		//设置请求超时时间 5s
		RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).build();
		get.setConfig(config);
		CloseableHttpResponse response = null ;
		try{
			response = httpClient.execute(get);
			String content = EntityUtils.toString(response.getEntity());
			entitys.put(content);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}

