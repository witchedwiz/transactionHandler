package it.vdevred.n26.sillycache;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheManagement {

	private static final Logger log = LoggerFactory.getLogger(CacheManagement.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    
    @Autowired
    private BadCache badCache;

    //effectively purging 20 seconds after the completion of the last method invocation.. could go with higher number
    //@Scheduled(fixedDelay = 20000)
    public void deflate() {
        log.info("deflate time is now {}", dateFormat.format(new Date()));
        badCache.deflateQuants();
    }
    
    //forcing invocation every 60s
    // will effectively add 60 quants slot
    @Scheduled(fixedRate  = 60000)
    public void inflate() {
        log.info("inflate time is now {}", dateFormat.format(new Date()));
        badCache.inflateQuants();
    }
    
    @Scheduled(fixedDelay = 60000)
    public void removeOrphanLocks()
    {
    	 log.info("removeOrphanLocks time is now {}", dateFormat.format(new Date()));
    	 badCache.removeOrphans();
    }
	
}
