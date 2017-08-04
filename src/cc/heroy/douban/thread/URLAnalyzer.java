package cc.heroy.douban.thread;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import cc.heroy.douban.util.RegexUtil;

/**
 * URL解析器线程
 * 从entitys中获取entity,解析出url地址
 * 将url进行去重判断，若在usedURLS中存在，则抛弃，否则添加到usedURLS和urls中
 * 
 */
public class URLAnalyzer implements Runnable{

	private BlockingQueue<String> entitys ;
	
	private BlockingQueue<String> urls ;
	
	private CopyOnWriteArraySet<String> usedURLS;
	
	private CountDownLatch startGate ;
	
	CountDownLatch endGate ;
	public URLAnalyzer(BlockingQueue<String> entitys,BlockingQueue<String> urls, CopyOnWriteArraySet<String> usedURLS,CountDownLatch startGate,CountDownLatch endGate){
		this.entitys = entitys;
		this.urls = urls;
		this.usedURLS = usedURLS;
		this.startGate = startGate;
		this.endGate = endGate;
	}
	
	@Override
	public void run() {
		try {
			startGate.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		
		while(!entitys.isEmpty()){
			try {
				String content = entitys.take();
				analyzer(content);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		endGate.countDown();
	}
	
	
	//解析entity的url
	private void analyzer(String content){
		try {
//System.out.println(content);
			Set<String> result = RegexUtil.URLRegex(content);
//test
			System.out.println(result.size());
			Iterator<String> it = result.iterator();
//			System.out.println("1111");
			while(it.hasNext()){
				System.out.println(it.next());
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
}
