package cc.heroy.douban.bean;

/**
 * 电影的封装类
 *	包含 ：title , cover(电影图片地址) , rate(评分) , url(详情页链接) , type(类型) , country(国家)
 */
public class Movie {
	private String title ;
	private String cover ;
	private String rate ;
	private String url ;
	private String type ;
	private String contry ;
	private String content ;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContry() {
		return contry;
	}
	public void setContry(String contry) {
		this.contry = contry;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return "Movie [title=" + title + ", cover=" + cover + ", rate=" + rate + ", url=" + url + ", type=" + type
				+ ", contry=" + contry + ", content=" + content + "]";
	}
	
}
