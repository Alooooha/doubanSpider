package cc.heroy.douban.test;

import java.io.IOException;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import cc.heroy.douban.util.HttpClientUtil;

public class HttpClientTest {
	
	public static void main(String[] args) {
		
		
//		HttpClientUtil.getHttpClient();
//		HttpGet get = new HttpGet("http://www.baidusadadw.com");
		
		//连接池
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(100);
		
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build()	;
		System.out.println("sss");
	/*	String[] urls = {
			"http://www.baidu.com",
			"http://www.heroy.cc",
			"http://xiaoa7.iteye.com/blog/1262034"
		};
		
		Thread[] threads = new Thread[urls.length];
		for(int i =0;i<urls.length;i++){
			threads[i] = new Thread(new GetThread(i+"",urls[i],httpClient));
		}
		
		for(Thread t : threads){
			t.start();
		}
		
	*/	
		
		
/*		//创建自定义响应程序
		try{
			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
				
				@Override
				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					int code = response.getStatusLine().getStatusCode();
					if(code>=200&&code<300){
						System.out.println(code);
						String str = EntityUtils.toString(response.getEntity());
						return str ;
					}else{
						throw new ClientProtocolException();
					}
				}
			};
			String content = httpClient.execute(get, responseHandler);
			System.out.println(content);
			
		}
		catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
	}
}
	
class GetThread implements Runnable{

		private String name ;
		private String url ;
		private	CloseableHttpClient httpClient ;
		
		GetThread(String name ,String url , CloseableHttpClient httpClient){
			this.name = name ;
			this.url = url ;
			this.httpClient = httpClient;
		}
		
		@Override
		public void run() {
			System.out.println("线程 "+name+ "启动");
			HttpGet get = new HttpGet(url);
			CloseableHttpResponse httpResponse = null;
			
			try{
				//配置连接超时时间，若超过5s就断开次连接
				RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).build();
				get.setConfig(config);
			httpResponse = httpClient.execute(get);
			int code = httpResponse.getStatusLine().getStatusCode();
System.out.println("线程 "+name+" 请求码 ：" + code);
			}catch(Exception e){
				e.printStackTrace();
			}
			finally{
				try {
					httpResponse.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
}
