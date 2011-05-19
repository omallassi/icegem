package example.ds.plugins;

import com.gemstone.gemfire.cache.CacheWriterException;
import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.util.CacheWriterAdapter;
import example.model.TradeMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * gather expired msgs
 * User: akondratyev
 */
public class ExpiredMsgCacheWriter extends CacheWriterAdapter<Long, TradeMsg> implements Declarable{

    private static Logger logger = LoggerFactory.getLogger(ExpiredMsgCacheWriter.class);

    @Override
    public void beforeUpdate(EntryEvent<Long, TradeMsg> entryEvent) throws CacheWriterException {
        //logger.trace("update {}", entryEvent.getNewValue());
    }

    @Override
    public void beforeDestroy(EntryEvent<Long, TradeMsg> entryEvent) throws CacheWriterException {
  /*      Region<Long, TradeMsg> expiredMsg = entryEvent.getRegion().getRegionService().getRegion("expired-msgs");
        TradeMsg  msg = entryEvent.getOldValue();
        logger.trace("msg {} expired and stored in saving region", msg.getId());
        expiredMsg.put(msg.getId(), msg);*/
    }

    public void init(Properties properties) {}
}
