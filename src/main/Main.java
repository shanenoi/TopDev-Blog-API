package main;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	
	private static String Convert(String str) {
		
		//It still failed when i try to convert unicode string to utf8 string
		
		Pattern pattern = Pattern.compile("(.)\\\\u(....)(.)");
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			char result = (char)Integer.parseInt(matcher.group(2), 16);
			str = str.replace(matcher.group(), matcher.group(1)+result+matcher.group(3));
		}
		return str;
	}

    private static String TopdevAPI(int currentPage, int max_num_pages, int category_id) throws IOException {
    	
    	//   Topdev API
    	//
		//    	Hot Jobs
		//
		//    	action: td_ajax_loop
		//    	loopState[sidebarPosition]: 
		//    	loopState[moduleId]: 10
		//    	loopState[currentPage]: 2
		//    	loopState[max_num_pages]: 2
		//    	loopState[atts][category_id]: 143
		//    	loopState[atts][offset]: 4
		//    	loopState[ajax_pagination_infinite_stop]: 0
		//    	loopState[server_reply_html_data]: 
		//
		//
		//    Lập Trình
		//
		//    	action: td_ajax_loop
		//    	loopState[sidebarPosition]: 
		//    	loopState[moduleId]: 10
		//    	loopState[currentPage]: 2
		//    	loopState[max_num_pages]: 86
		//    	loopState[atts][category_id]: 1
		//    	loopState[atts][offset]: 4
		//    	loopState[ajax_pagination_infinite_stop]: 0
		//    	loopState[server_reply_html_data]: 
		//
		//
		//    Career
		//
		//    	action: td_ajax_loop
		//    	loopState[sidebarPosition]: 
		//    	loopState[moduleId]: 10
		//    	loopState[currentPage]: 2
		//    	loopState[max_num_pages]: 10
		//    	loopState[atts][category_id]: 5
		//    	loopState[atts][offset]: 4
		//    	loopState[ajax_pagination_infinite_stop]: 0
		//    	loopState[server_reply_html_data]: 
		//
		//
		//    HR
		//
		//    	action: td_ajax_loop
		//    	loopState[sidebarPosition]: 
		//    	loopState[moduleId]: 10
		//    	loopState[currentPage]: 2
		//    	loopState[max_num_pages]: 19
		//    	loopState[atts][category_id]: 7
		//    	loopState[atts][offset]: 4
		//    	loopState[ajax_pagination_infinite_stop]: 0
		//    	loopState[server_reply_html_data]: 
		//
		//
		//    Công Nghệ
		//
		//    	action: td_ajax_loop
		//    	loopState[sidebarPosition]: 
		//    	loopState[moduleId]: 10
		//    	loopState[currentPage]: 2
		//    	loopState[max_num_pages]: 38
		//    	loopState[atts][category_id]: 145
		//    	loopState[atts][offset]: 4
		//    	loopState[ajax_pagination_infinite_stop]: 0
		//    	loopState[server_reply_html_data]: 

        String url = "https://topdev.vn/blog/wp-admin/admin-ajax.php?td_theme_name=Newspaper&v=7.3";
        HttpPost post = new HttpPost(url);
        String result = "";

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("action", "td_ajax_loop"));
        urlParameters.add(new BasicNameValuePair("loopState[sidebarPosition]", null));
        urlParameters.add(new BasicNameValuePair("loopState[moduleId]", "10"));
        urlParameters.add(new BasicNameValuePair("loopState[currentPage]", "2"));
        urlParameters.add(new BasicNameValuePair("loopState[max_num_pages]", "86"));
        urlParameters.add(new BasicNameValuePair("loopState[atts][category_id]", "1"));
        urlParameters.add(new BasicNameValuePair("loopState[atts][offset]", "4"));
        urlParameters.add(new BasicNameValuePair("loopState[ajax_pagination_infinite_stop]", "0"));
        urlParameters.add(new BasicNameValuePair("loopState[server_reply_html_data]", null));
        

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)){

            result = EntityUtils.toString(response.getEntity());
        }

        return result;
    }
    
    public static void main(String[] args) {

        try {
            String result = TopdevAPI(2, 38, 145);
            result = result.replace("\\r", "")
            				.replace("\\n", "")
            				.replace("\\\"", "\"")
            				.replace("\\/", "/");
            
            Pattern pattern = Pattern.compile("server_reply_html_data\":\"(.+)\"}");
            Matcher matcher = pattern.matcher(result);
            
            matcher.find();
            String html_content = matcher.group(1);
            Document doc = Jsoup.parse(html_content);
            
            for (Element ele: doc.getElementsByAttributeValue("class", "td_module_10 td_module_wrap td-animation-stack")) {
            	System.out.print(
            			String.format(
            					(
            							"Title:      %s\n" +
            							"Link:       %s\n" +
            							"Image:      %s\n" +
            							"Time:       %s\n" +
            							"SubContent: %s\n\n\n\n"
            					),
            					Convert(ele.select("a").first().attr("title")),
            					Convert(ele.select("a").first().attr("href")),
            					Convert(ele.select("img").first().attr("src")),
            					Convert(ele.select("time").first().text()),
            					Convert(ele.getElementsByAttributeValue("class", "td-excerpt").first().text())
            			)
            	);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}