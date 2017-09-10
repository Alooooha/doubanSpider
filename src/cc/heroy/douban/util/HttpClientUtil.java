package cc.heroy.douban.util;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * 单例模式，生成单个 HttpClient
 *
 */
public class HttpClientUtil {
	
	private static BasicCookieStore cookieStore ;
	private static CloseableHttpClient httpClient ;
	//HttpClient连接池
	private static PoolingHttpClientConnectionManager cm ;
	
	
	static{
//		cookieStore = new BasicCookieStore();
//		clientPool.setMaxTotal(20);
//		httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).setConnectionManager(clientPool).build();
		cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(20);
		httpClient = HttpClients.custom().setConnectionManager(cm).build()	;		
	}

	//禁止构造器生成对象
	private HttpClientUtil(){
	}
	
	/**
	 * 获取HttpClient（单例）
	 */
	public static CloseableHttpClient getHttpClient(){
		return httpClient ;
	}
	
	/**
	 * 获取cookieStore（单例）
	 */
	public static CookieStore getcookieStore(){
		return cookieStore;
	}
	
}
