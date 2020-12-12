package alexrk2.geolink;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GeolinkServlet
 */
@WebServlet("/geolink")
public class GeolinkServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(GeolinkServlet.class.getName());

	private static final long serialVersionUID = 1L;
       		
	Map<String, GeolinkConfig> configMap;
	
	GeolinkConfigFactory factory = new GeolinkConfigFactory();
	
	String cache;
	
	@Override
	public void init() throws ServletException {
		super.init();
		configMap = factory.createConfigFromString(getConfigFile());
	}
	
	public String getConfigFile() {
        if (cache==null)
        	cache = readConfig();
        return cache;
	}
	
	public void resetConfig() {
		log.info("Purge configfile from cache");
		cache = null;
		configMap = factory.createConfigFromString(getConfigFile());
	}
	
	public String readConfig() {
		log.info("Request config file read");
	    Wiki wiki = new Wiki("de.wikipedia.org");
	    
	    try {
			String content = wiki.getPageText("Vorlage:GeoTemplate/GeolinkConfig");
			String configText = content.substring(content.indexOf("START_CONFIG")+12);
			configText = configText.substring(0, configText.indexOf("END_CONFIG"));			
			return "#" + new Date() +"\n" + configText;
		} catch (IOException e) {
			e.printStackTrace();
			log.severe(e.getMessage());
			return "#" + new Date() +"\n";
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		String config = req.getParameter("config");
		String latS = req.getParameter("lat");
		String lonS = req.getParameter("lon");
		String scaleS = req.getParameter("scale");
		String style = req.getParameter("style");
		boolean showconfig = req.getParameter("showconfig")!=null;
		boolean preview = req.getParameter("preview")!=null;
		boolean purge = req.getParameter("purge")!=null;
		
		if (showconfig) {
			String cfg = getConfigFile();
			cfg = cfg.replace("\n", "<br>");
			resp.getWriter().write(cfg);
			resp.getWriter().close();			
            return;            						
		}

		if (purge) {
			resetConfig();
			resp.getWriter().write("Konfig-Cache wurde gelöscht.");
			resp.getWriter().close();			
            return;            						
		}

		if (config==null || latS==null || lonS==null) {
			resp.getWriter().write("Parameter config, lat, lon muss angegeben werden.");
			resp.getWriter().close();
            return;            			
		}
		
		double lat = 0, lon = 0;
		int scale = 10000;
		try {
			lat = Double.parseDouble(latS);
			lon = Double.parseDouble(lonS);
			if (scaleS != null) {
				scale = Integer.parseInt(scaleS);
			}
		} catch (Exception e) {
			resp.getWriter().write("Fehler beim Einlesen der Parameter:<br/>");
			resp.getWriter().write(e.getLocalizedMessage());
			resp.getWriter().close();
            return;            			
		}
		
		config = config.toLowerCase();
		if (!configMap.containsKey(config)) {
			resp.getWriter().write("Konfiguration "+config+" nicht definiert");
			resp.getWriter().close();
            return;            						
		}
		
		GeolinkConfig glc =  configMap.get(config);
		
		if (preview) {
			resp.getWriter().write(glc.constructUrl(lat, lon, scale, style));
			resp.getWriter().close();			
		} else {
			resp.sendRedirect(glc.constructUrl(lat, lon, scale, style) );			
		}		
		
	}
	
}
