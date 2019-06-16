package bilibili;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 * 
 * @author didi
 *	输入bilibili av号,下载图片
 *	输入av16211930
 */

public class bilibili {
	public static void main(String[] args) {
		Scanner sc=new Scanner(System.in);
		System.out.println("------");
		String av = "";
		av+=sc.nextLine();
		System.out.println(av+"封面图片地址为:"+getimg_url(av));
		getpic(getimg_url(av), av, "D:/bilibili_img");
	}
	private static String getimg_url(String av) {
		String Jsondata="";
		String url = "https://search.bilibili.com/all?keyword=" + av + "&from_source=banner_search";
		String img_url="";
		try {
			Document doc = Jsoup.connect(url).get();
			Elements imgs = doc.getElementsByTag("script");
			for (Element img : imgs) {
				if (img.attr("src").toString().equals("")&&img.attr("type").toString().equals("")) {
					img_url=JsontoString(img.toString());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img_url;
	}
	private static String JsontoString(String Jsondata) {
		Jsondata = Jsondata.substring(33, Jsondata.length() - 131);//截取Json
		Jsondata = Jsondata.substring(Jsondata.indexOf("\"pic\""), Jsondata.indexOf(".jpg") + 4);//从数据中获取"pic:"的值
		Jsondata = Jsondata.substring(Jsondata.indexOf("\\u002F"));//从数据中获取"pic:"的值，从\u002F开始截取
		Jsondata=Jsondata.replace("\\u002F", "\\");//替换
		Jsondata="https:"+Jsondata;
		return Jsondata;
	}
	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		// 创建一个Buffer字符串
		byte[] buffer = new byte[1024];
		// 每次读取的字符串长度，如果为-1，代表全部读取完毕
		int len = 0;
		// 使用一个输入流从buffer里把数据读取出来
		while ((len = inStream.read(buffer)) != -1) {
			// 用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
			outStream.write(buffer, 0, len);
		}
		// 关闭输入流
		inStream.close();
		// 把outStream里的数据写入内存
		return outStream.toByteArray();
	}
	private static void getpic(String src, String name, String patch) {
		// TODO Auto-generated method stub
		try {
			src=src.replace("\\", "/");
			System.out.println(src);
			URL url = new URL(src);
			// URL url= new URL(null, url, new sun.net.www.protocol.https.Handler());
			// 打开连接
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			// 设置请求方式
			// connection.setRequestMethod("get");
			// 设置超时响应时间
			int timeout = 5000;
			connection.setConnectTimeout(timeout);
			// 通过输入流获取图片数据
			InputStream inStream = connection.getInputStream();
			// 得到图片的二进制数据，以二进制封装得到数据，具有通用性
			byte[] data;
			try {
				data = readInputStream(inStream);
				FileInit(patch);
				// new一个文件对象用来保存图片，保存在path
				File imageFile = new File(patch + "/" + name + ".jpg");
				// 如果文件存在就删除
				if (imageFile.exists())
					imageFile.delete();
				// 创建输出流
				FileOutputStream outStream = new FileOutputStream(imageFile);
				// 写入数据
				outStream.write(data);
				// 关闭输出流
				outStream.close();
				System.out.println("下载完成，文件位于"+imageFile.getPath());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	private static void FileInit(String patch) {
		File file = new File(patch);
		if (file.exists()) {
			if (file.isDirectory()) {
				System.out.println("文件夹" + file.getPath() + "存在，开始下载...");
			} else {
				System.out.println("存在同名文件，无法创建文件夹");
			}
		} else {
			System.out.println("文件夹" + file.getPath() + "不存在,开始创建文件夹，成功后开始下载 ...");
			file.mkdir();
		}
	}
}
