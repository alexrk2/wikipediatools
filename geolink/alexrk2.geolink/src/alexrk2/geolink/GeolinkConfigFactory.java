package alexrk2.geolink;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

public class GeolinkConfigFactory {
	
	private static final Logger log = Logger.getLogger(GeolinkServlet.class.getName());
		
	Map<String, GeolinkConfig> createConfigFromString(String configString) {
		Map<String, GeolinkConfig> configMap = new HashMap<String, GeolinkConfig>();
		
		Scanner scanner = new Scanner(configString);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.startsWith("#")) {
				log.info("[config header] "+line);
				continue;
			}
			if (line.trim().isEmpty() || !line.contains("="))
				continue;			
			String s[] = line.split("=", 2);
			String key = s[0].trim();
			String value = s[1].trim();
			String configid = key.split("\\.")[0].toLowerCase();
			String configParam = key.split("\\.")[1];

			if (!configMap.containsKey(configid))
				configMap.put(configid, new GeolinkConfig(configid));
			GeolinkConfig glc = configMap.get(configid);
			if (configParam.equalsIgnoreCase("proj"))
				glc.setProj(value);
			if (configParam.equalsIgnoreCase("url"))
				glc.setUrlTemplate(value);
			
		}
		log.info("Read configs for: "+configMap.keySet());
		
		return configMap;
	}
	
}
