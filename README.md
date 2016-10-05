This is a simple crawler which is designed to explore eCommerce websites and extract useful product information from them.

##System requirements

- Java 8 
- Maven 3


##Architecture 

- Crawler (CrawlerWorker.java)
![](https://github.com/clingsii/images/blob/master/cr1.png?raw=true)

- Simple extractor (SimpleExtractor.java) 

It's simple and based on rules, it needs human to look through item detail pages and provide specific rules for each website.
![](https://github.com/clingsii/images/blob/master/cr2.png?raw=true)

- Automatic extractor (AutoExtractor.java)

It's a simple machine learning based solution, which analyses the catalog of the website and the structure of item detail 
pages automatically, so it may work well on different websites well ( After proper training and optimization )  
![](https://github.com/clingsii/images/blob/master/cr3.png?raw=true)
