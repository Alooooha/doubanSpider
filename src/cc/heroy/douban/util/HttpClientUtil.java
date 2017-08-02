package cc.heroy.douban.util;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * 单例模式，生成单个 HttpClient
 *
 */
public class HttpClientUtil {
	
	private static BasicCookieStore cookieStore ;
	private static CloseableHttpClient httpClient ;
	
	static{
		cookieStore = new BasicCookieStore();
		httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
	}
	
	/**
	 * 获取HttpClient（单例）
	 */
	public static HttpClient getHttpClient(){
		return httpClient ;
	}
	
	/**
	 * 获取cookieStore（单例）
	 */
	public static CookieStore getcookieStore(){
		return cookieStore;
	}
	
}
