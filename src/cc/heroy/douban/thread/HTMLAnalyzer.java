package cc.heroy.douban.thread;

import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cc.heroy.douban.bean.Movie;

/**
 * HTML页面解析线程
 * 从entitys中取出html源代码进行数据提取(电影名，评分，类型，剧情，链接)
 * 将movie存到vector容器
 */
public class HTMLAnalyzer implements Runnable{
	
	private final BlockingQueue<String> entitys ;
	
	private final CountDownLatch startGate ;
	private final CountDownLatch endGate ;
	private final Vector<Movie> movies ;
	
	public HTMLAnalyzer(BlockingQueue<String> entitys,CountDownLatch startGate , CountDownLatch endGate,Vector<Movie> movies){
		this.entitys = entitys;
		this.startGate = startGate;
		this.endGate = endGate;
		this.movies = movies;
	}
	
	@Override
	public void run() {
		//等待startGate
System.out.println(entitys.size());
		try {
			startGate.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		//启动
System.out.println("HTMLAnalyzer启动");
		
		while(!entitys.isEmpty()){
			try {
				String content = entitys.take();
				Movie movie = new Movie();
				Document doc = Jsoup.parse(content);
				//电影url
				String url = content.substring(0,content.indexOf("#"));
				movie.setUrl(url);
				//电影名
				Elements titles = doc.getElementsByTag("title");
				String title = titles.get(0).text();
				movie.setTitle(title);
				//电影评分
				Elements races = doc.getElementsByClass("ll rating_num");
				String race = races.get(0).text();
				movie.setRace(race);
				//电影标签
				Elements types = doc.getElementsByClass("tags-body");
				String type ="";
				for(Element e : types){
					type += e.text()+" ";
				}
				movie.setType(type);

				//剧情(有些网页剧情显示不完全，需要判断完整剧情位置)
				String story = "";
				Elements indents = doc.getElementsByClass("indent");
				Elements allhidden = doc.getElementsByClass("all hidden");
				if(indents!=null&&indents.get(1)!=null){
					story = indents.get(1).text();
				}else{
					if(allhidden!=null&&allhidden.get(0)!=null){
						story =allhidden.get(0).text();
					}
				}
//System.out.println(story);
				
				/*if(hidden!=null&&hidden.get(0)!=null){
					System.out.println("111111111111111111");
					story = hidden.get(0).text();
				}else{*/
//				}			
					
					
				movie.setStory(story);
//System.out.println(story);
				movies.addElement(movie);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		endGate.countDown();
System.out.println("HTMLAnalyzer结束");
		
	}
	
	

}
