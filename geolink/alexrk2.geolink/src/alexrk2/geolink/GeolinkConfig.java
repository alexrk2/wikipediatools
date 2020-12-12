package alexrk2.geolink;

import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.CoordinateTransformFactory;
import org.osgeo.proj4j.ProjCoordinate;

public class GeolinkConfig {

	private String id;
	private String proj;
	private String urlTemplate;

	public GeolinkConfig(String id) {
		super();
		this.id = id;
	}

	public GeolinkConfig(String id, String proj, String urlTemplate) {
		super();
		this.id = id;
		this.proj = proj;
		this.urlTemplate = urlTemplate;
	}	
	
	public String constructUrl(double lat, double lon, int scale, String style) {
		String url = urlTemplate;		
		int dim = (int)Math.round((double)scale/10d);
		double x = lat;
		double y = lon;
		if (proj!=null) {
	        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
	        CRSFactory csFactory = new CRSFactory();	        
	        CoordinateReferenceSystem crs = csFactory.createFromParameters("id", proj);	        
	        final String WGS84_PARAM = "+title=long/lat:WGS84 +proj=longlat +ellps=WGS84 +datum=WGS84 +units=degrees";
	        CoordinateReferenceSystem WGS84 = csFactory.createFromParameters("WGS84",WGS84_PARAM);	        
	        CoordinateTransform trans = ctFactory.createTransform(WGS84, crs);	        
	        ProjCoordinate p = new ProjCoordinate();
	        ProjCoordinate p2 = new ProjCoordinate();
	        p.x = lon;
	        p.y = lat;	        
	        trans.transform(p, p2);
	        x = p2.x;
	        y = p2.y;
		}
		url = url.replaceAll("\\{x\\}", Double.toString(x));
		url = url.replaceAll("\\{y\\}", Double.toString(y));
		url = url.replaceAll("\\{xint\\}", Integer.toString((int)Math.round(x)));
		url = url.replaceAll("\\{yint\\}", Integer.toString((int)Math.round(y)));
		url = url.replaceAll("\\{x1\\}", Double.toString(x-dim/2));
		url = url.replaceAll("\\{y1\\}", Double.toString(y-dim/2));
		url = url.replaceAll("\\{x2\\}", Double.toString(x+dim/2));
		url = url.replaceAll("\\{y2\\}", Double.toString(y+dim/2));
		url = url.replaceAll("\\{x1int\\}", Integer.toString((int)Math.round(((double)x-((double)dim)/2d))));
		url = url.replaceAll("\\{y1int\\}", Integer.toString((int)Math.round(((double)y-((double)dim)/2d))));
		url = url.replaceAll("\\{x2int\\}", Integer.toString((int)Math.round(((double)x+((double)dim)/2d))));
		url = url.replaceAll("\\{y2int\\}", Integer.toString((int)Math.round(((double)y+((double)dim)/2d))));
		url = url.replaceAll("\\{lat\\}", Double.toString(lat));
		url = url.replaceAll("\\{lon\\}", Double.toString(lon));
		url = url.replaceAll("\\{scale\\}", Double.toString(scale));
		
		if (style!=null)
			url = url.replaceAll("\\{style\\}", style);
		
		return url;
	}

	public String getProj() {
		return proj;
	}

	public void setProj(String proj) {
		this.proj = proj;
	}

	public String getUrlTemplate() {
		return urlTemplate;
	}

	public void setUrlTemplate(String urlTemplate) {
		this.urlTemplate = urlTemplate;
	}

	@Override
	public String toString() {
		return "GeolinkConfig [id=" + id + ", proj=" + proj + ", urlTemplate="
				+ urlTemplate + "]";
	}
		
}
