package cc.heroy.douban.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则解析器
 *
 */
public class RegexUtil {
	private static final String urlRegex = "^https://movie.douban.com/subject/[0-9]+/";
	
	private static Pattern urlPattern ;
	
	static{
		urlPattern = Pattern.compile(urlRegex);
	}
	/**
	 * 地址解析
	 * 使用synchronized : 方法会被多个线程调用，因为urlPattern为单例，为了防止出现打断异常，必须要同步方法
	 * 
	 */
	public static synchronized Set<String> URLRegex(Set<String> set){
		Set<String> result =new HashSet<String>();
		Iterator<String> it = set.iterator();
		while(it.hasNext()){
			String str = it.next();
			Matcher m = urlPattern.matcher(str);
			if(m.find()){
				result.add(m.group());
			}
		}
		return result;
	}
	
}
