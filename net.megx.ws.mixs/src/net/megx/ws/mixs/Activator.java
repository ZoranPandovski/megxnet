package net.megx.ws.mixs;

import net.megx.megdb.mixs.MixsWsDao;
import net.megx.utils.OSGIUtils;
import net.megx.ws.mixs.rest.MixWsResource;
import net.megx.ws.mixs.service.MixsWsService;
import net.megx.ws.mixs.service.MixsWsServiceImpl;

import org.chon.cms.core.JCRApplication;
import org.chon.cms.core.ResTplConfiguredActivator;
import org.chon.web.RegUtils;

public class Activator extends ResTplConfiguredActivator {
	
	@Override
	protected void registerExtensions(JCRApplication app) {
		OSGIUtils.requestService(MixsWsDao.class.getName(), getBundleContext(), new OSGIUtils.OnServiceAvailable<MixsWsDao>() {

			@Override
			public void serviceAvailable(String name, MixsWsDao service) {
				MixsWsServiceImpl mixsService = new MixsWsServiceImpl();
				mixsService.setMixsWsDao(service);
				MixWsResource restResource = new MixWsResource();
				restResource.setMixsWsService(mixsService);
				
				// expose the Mixs Service
				RegUtils.reg(getBundleContext(), MixsWsService.class.getName(), mixsService, null);
				
				
				// register the REST Resource
				RegUtils.reg(getBundleContext(),MixWsResource.class.getName(), restResource, null);
			}
			
		});
	}

	@Override
	protected String getName() {
		return "net.megx.ws.mixs";
	}
	
}
