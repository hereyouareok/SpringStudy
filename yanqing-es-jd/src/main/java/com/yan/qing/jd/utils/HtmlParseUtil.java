package com.yan.qing.jd.utils;

import com.yan.qing.jd.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author YanQin
 * @version v1.0.0
 * @Description :
 * @Create on : 2020/11/22 20:39
 **/
@Component
public class HtmlParseUtil {

  /*  public static void main(String[] args) throws IOException {
        new HtmlParseUtil().parse("java").forEach(System.out::println);
    }*/

    public List<Content> parse(String keywords) throws IOException {
        String url = "https://search.jd.com/Search?keyword=" + keywords;
        //解析网页
        Document document = Jsoup.parse(new URL(url), 30000);
        Element element = document.getElementById("J_goodsList");

        Elements elements = element.getElementsByTag("li");
        ArrayList<Content> goodsList = new ArrayList<>();
        for (Element el : elements) {
            System.out.println(el);
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            //String price = el.getElementsByClass("p-price").eq(0).text();

            String price =el.getElementsByTag("strong").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();

            Content content = new Content();
            content.setImg(img);
            content.setPrice(price);
            content.setTitle(title);
            goodsList.add(content);

        }
        return goodsList;
    }
}
