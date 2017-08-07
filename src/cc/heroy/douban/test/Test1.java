package cc.heroy.douban.test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import cc.heroy.douban.util.RegexUtil;

public class Test1 {
public static void main(String[] args) throws IOException {
		String str = "F:123456.txt";
		File html = new File(str);
System.out.println(html.exists());
		Document doc = Jsoup.parse(html,"UTF-8");  
//		Elements re = doc.getElementsByClass("celebrity");
		
		
//		Elements e = doc.getElementsByClass("");
//		Elements es = e.first().children();
		
		Elements links = doc.select("a[href]");
		Set<String> set = new HashSet<String>();
		for(Element i : links){
			set.add(i.attr("href"));
		}
		set = RegexUtil.URLRegex(set);
		for(String s : set){
			System.out.println(s);
		}
		
		Elements titles = doc.getElementsByTag("title");
		
		Elements e = doc.getElementsByClass("info");

		
		System.out.println(titles.get(0).text());
		
//		Elements type = doc.getElementsby
		
		System.out.println("SS");
		
	String url = "http://asdadsa/asdas#asdadsa";
		
		System.out.println(url.substring(0,url.indexOf("#")));
		
		
//		Element e = doc.getElementsByClass("recommendations").first();
//		Elements es = doc.getAllElements();
		
//		System.out.println(doc.getElementById("db-nav-movie").className());
		
//		System.out.println(e+"sss");
//		Elements es = e.getElementsByTag("a");
//		for(Element element :es){
//			System.out.println(es);
//			System.out.println(element.attr("href"));
//		}
	}


	@Test
	public void HtmlAnalyzer(){
		String str = "F:159226.html";
		File html = new File(str);
		try{
		Document doc = Jsoup.parse(html,"UTF-8");  
		//获取电影名字
		Element titles = doc.getElementById("wrapper");
		System.out.println("ss");
		//
//		Elements titles = doc.getElementsByTag("title");
		Elements esss = doc.getElementsByClass("tags-body");
		Elements essss = doc.getElementsByClass("indent");
		
		
//		System.out.println(titles.get(0).text());;
		
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void URLTest(){
		String url = "http://asdadsa/asdas#asdadsa";
		
		System.out.println(url.endsWith("#"));
		
	}
}
