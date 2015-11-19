package io.pivotal.pde.demo.tracker.gemfire;

import com.gemstone.gemfire.cache.CacheListener;
import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.InterestResultPolicy;
import com.gemstone.gemfire.cache.RegionEvent;

public class CheckInCacheListener implements CacheListener<String, CheckIn> {

	public static interface ChangeHandler {
		public void itemAdded(CheckIn newItem);
	}
	
	private ChangeHandler changeHandler;
	
	public void setHandler(ChangeHandler handler){
		this.changeHandler = handler;
	}
	
	@Override
	public void close() {
	}

	@Override
	public void afterCreate(EntryEvent<String, CheckIn> event) {
		if (changeHandler != null) changeHandler.itemAdded(event.getNewValue());
	}

	@Override
	public void afterDestroy(EntryEvent<String, CheckIn> event) {
		// no destroys in this app
	}

	@Override
	public void afterInvalidate(EntryEvent<String, CheckIn> event) {
	}

	@Override
	public void afterRegionClear(RegionEvent<String, CheckIn> regionEvent) {
	}

	@Override
	public void afterRegionCreate(RegionEvent<String, CheckIn> regionEvent) {
	}

	@Override
	public void afterRegionDestroy(RegionEvent<String, CheckIn> regionEvent) {
	}

	@Override
	public void afterRegionInvalidate(RegionEvent<String, CheckIn> regionEvent) {
	}

	@Override
	public void afterRegionLive(RegionEvent<String, CheckIn> regionEvent) {
		regionEvent.getRegion().registerInterest("ALL_KEYS",InterestResultPolicy.NONE,false, true);
	}

	@Override
	public void afterUpdate(EntryEvent<String, CheckIn> event) {
		// no updates in this app
	}

}
