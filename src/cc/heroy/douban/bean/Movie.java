package cc.heroy.douban.bean;

/**
 * title :电影标题
 * race :评分
 * type :类型
 * story :剧情
 * url :详情页链接
 * 
 */
public class Movie {
	private String title ;
	private String race ;
	private String type ;
	private String story ;
	private String url ;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRace() {
		return race;
	}
	public void setRace(String race) {
		this.race = race;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStory() {
		return story;
	}
	public void setStory(String story) {
		this.story = story;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString() {
		return "Movie [title=" + title + ", race=" + race + ", type=" + type + ", story=" + story + ", url=" + url
				+ "]";
	
	}
	
}