package gen;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.trie4j.util.StreamUtil;

public class GenHtml {
	public static void main(String[] args) throws Exception{
		String templ = null;
		InputStream temps = GenHtml.class.getResourceAsStream("template.txt");
		try{
			templ = StreamUtil.readAsString(temps, "UTF-8");
		} finally{
			temps.close();
		}
		InputStream res = GenHtml.class.getResourceAsStream("result.txt");
		try{
			BufferedReader r = new BufferedReader(new InputStreamReader(res, "UTF-8"));
			String line = null;
			while((line = r.readLine()) != null){
				String[] vals = line.split("[ ]+");
				String name = vals[0].substring(0, vals[0].length() - 1).replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)").trim();
				templ = templ.replaceAll("\\$\\{" + name + "\\.build\\}", String.format("%,d", Integer.valueOf(vals[1].replace(',', ' ').trim())))
						.replaceAll("\\$\\{" + name + "\\.contains\\}", String.format("%,d", Integer.valueOf(vals[2].replace(',', ' ').trim())))
						.replaceAll("\\$\\{" + name + "\\.mem\\}", String.format("%.1f", Integer.valueOf(vals[3].replace(',', ' ').trim()) / 1000000.0));
			}
			System.out.println(templ);
		} finally{
			res.close();
		}
	}
}
