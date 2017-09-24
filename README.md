# doubanspider（豆瓣爬虫）
起因：
------
        学习《JAVA并发编程实践》时，发现里面的很多知识点多而实践很少，对于初学者不容易深入理解JAVA多线程。因为爬虫技术爬取数据量大，使用单线程不现实，因此计划使用JAVA多线程+爬虫技术完成一个高性能的爬虫工具。


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


Version 1.1.1 (17-8-7)
------
版本改动：
<br>①项目使用多线程
<br>②大量使用juc包容器
<br>③添加页面url解析器，html解析器，url爬虫
<br>项目逻辑 ：
![image](https://github.com/Alooooha/DoubanSpider/blob/master/img/Version1.1.1.png)
<br>运行时间 ：1min 17s
<br>
<br>项目不足:
<br>①频繁访问豆瓣url，导致IP地址被封
<br>②爬虫数据量不高
<br>③多线程顺序执行，考虑使用生产者消费者模型
<br>④项目功能过于局限
<br>
<br>更新(17-8-9)
<br>为了解决频繁访问导致IP被封的问题，决定写一个WEB项目从网上爬取可用的高匿IP
项目：[proxy_ip](https://github.com/Alooooha/proxy_ip "IP扫描系统")


Version 2.0.0 (17-9-6)
------
①爬取豆瓣所有电影详情信息
<br>
<br>描述：
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;该版本解决了爬虫数据量不高，项目功能局限，未采用生产者消费者模型的问题。由于
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;另一项目“IP扫描系统”遇到些麻烦，没法得到其他代理IP，所以把爬虫设置了访问时间
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;间隔3s。同时采用了广度优先遍历算法。
<br>
<br>项目逻辑 ：
![image](https://github.com/Alooooha/DoubanSpider/blob/master/img/Version2.0.0.PNG)
<br>运行时间 : 预计45小时（假设电影数15w([豆瓣一共收录了多少部电影](https://www.zhihu.com/question/20072525))，项目每秒钟请求1次）
<br>
<br>使用 :
<br>运行cc.heroy.douban.core.ategoryMovie.java类即可。
<br>
<br>遇到的问题：
<br>①在测试阶段，我将urls，entitys1，entitys2，分别设置大小为200，200，200。运行过程中出现URLAnalyzer线程出现阻塞的现象，通过在HTMLAnamyzer线程中添加打印容器状态代码后发现，urls达到200后，URLAnalyzer出现阻塞现象，伴随entitys1大小增加到200，URLSpider线程也出现阻塞，因此在网上查询了豆瓣收录电影大致数目后，将容器大小分别设置为40w，20w，20w。
<br>②出现与开发[proxy_ip](https://github.com/Alooooha/proxy_ip "IP扫描系统")项目一样的难题！HttpClient请求中socket阻塞，引发URLSpider线程假死，导致项目无法发出请求。晚上11点半左右，我测试项目，第二天早上起床的时候，项目已经阻塞，entitys1和entitys2已经空掉，当时已存储有14k多条数据，文件大小15m。
<br>
<br>更新前提 :
<br>Ⅰ.将项目改为web项目
<br>Ⅱ.添加日志功能
<br>Ⅲ.使用服务器执行爬虫
<br>Ⅳ.解决socket阻塞问题(困难)
<br>Ⅴ.规范代码
