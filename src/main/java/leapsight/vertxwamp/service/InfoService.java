package saleCH.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InfoService {

//	@Value("${Main-Class}")
//	protected String MAIN_CLASS;
    @Value("${Implementation-Title}")
    protected String TITLE;
    @Value("${Implementation-Version}")
    protected String VERSION;
    @Value("${Implementation-Vendor}")
    protected String VENDOR;
    @Value("${Implementation-Build-Date}")
    protected String DATE;
    @Value("${Implementation-Description}")
    protected String DESCRIPTION;

    public Map<String, Object> info() {
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("Title", TITLE);
        info.put("Version", VERSION);
        info.put("Vendor", VENDOR);
        info.put("Date", DATE);
        info.put("Descriptions", DESCRIPTION);
        return info;
	}

    public String getServiceVersion() {
    	return VERSION;
    }

	public String getServiceName() {
    	return TITLE;
    }

}