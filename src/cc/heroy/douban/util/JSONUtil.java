package cc.heroy.douban.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * JSON 工具类 ：
 *		1 toJSONObject(String str) 将String类型转换成JSON对象  				 		
 *
 */
public class JSONUtil {

	/**
	 * 	将String 转换成JSON对象
	 */
	public static JSONObject toJSONObject(String str){
		JSONObject obj = JSON.parseObject(str);
		return obj ;
	}
	
	
}
