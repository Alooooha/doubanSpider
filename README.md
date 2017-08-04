# DoubanSpider（豆瓣爬虫）
起因：
------
        学习《JAVA并发编程实践》时，发现里面的很多知识点而实践很少，对初学者来说不好深入理解JAVA多线程。因为爬虫技术爬取数据量大，使用单线程不现实，因此计划使用JAVA多线程+爬虫技术完成一个高性能的爬虫工具。


Version 1.1.0 (17-8-2)
------
①  目前可以爬取电影分类首页（电影-剧情-美国-经典）和电影详情页的描述信息
<br>
<br>使用主线程顺序执行代码
<br>获取从豆瓣服务器上获取5个页面的JSON数据-依次取出json数据并解析为bean-从bean中取出所有的详情地址链接-HttpClient访问url-提取每个html中的剧情描述-存入Bean
<br>② 运行时间 ：2min 15s
<br>
<br>更新(17-8-4) ：
<br>--创建URLspider线程类用于url请求将response解析成HTML，存入entitys中
<br>--创建URLAnalyzer线程类从entitys中取出html，解析出url（解析功能未实现）
<br>--创建HTMLAnalyzer线程类解析html代码中需要的数据(解析功能未实现)
<br>--添加工具类：RegexUtil，HttpClientUtil
<br>--采用ArrayBlockingQueue容器作为存放URL的urls和html代码的entitys
<br>--考虑到多线程安全，用CopyOnWriteArraySet作为存放使用过的url的容器
