package cc.heroy.douban.task;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import org.apache.http.ParseException;
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
	private final BlockingQueue<String> entitys1 ;
	private final BlockingQueue<String> entitys2 ;
	private final CountDownLatch startGate ;
	private final CountDownLatch endGate ;
	//状态监听器
	private URLSpiderListener listener;
	String name ;
	
	//线程睡眠时间
	long space = 3000L;
	
	public URLSpider(CloseableHttpClient httpCLient ,BlockingQueue<String> urls,BlockingQueue<String> entitys1,BlockingQueue<String> entitys2,CountDownLatch startGate,CountDownLatch endGate){
		this.httpClient = httpCLient;
		this.urls = urls;
		this.entitys1 = entitys1;
		this.entitys2 = entitys2;
		this.startGate = startGate;
		this.endGate = endGate ;
		name = UUID.randomUUID().toString().substring(0, 4);
	}
	
	@Override
	public void run() {
		//等待startGate = 0
System.out.println("URLSpider启动");
		try{
		startGate.await();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		
		while(!urls.isEmpty()||!entitys1.isEmpty()||!entitys2.isEmpty()){
			//take()方法在urls没数据时阻塞
			try {
				String url = urls.take();
				spider(url);
				//保持活着
				listener.keepLive(this.name);
			} catch (InterruptedException e){
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				
			}
		}
		//endGate-1
		endGate.countDown();
System.out.println("URLSpider结束");
	}
	
	private void spider(String url) throws InterruptedException, ParseException, IOException{
		HttpGet get = new HttpGet(url);
		//设置请求超时时间 5s
		RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000).build();
		get.setConfig(config);
		CloseableHttpResponse response = null ;
			response = httpClient.execute(get);
			String content = EntityUtils.toString(response.getEntity());
			entitys1.put(url+"#"+content);
			entitys2.put(url+"#"+content);
System.out.println(name+"提取："+url);
			Thread.sleep(space);
				response.close();
		}
	
	//持有UrlSpiderListener引用
	public void registerListner(URLSpiderListener listener) {
		this.listener = listener;
		listener.keepLive(name);
	}
	
	
		
}

