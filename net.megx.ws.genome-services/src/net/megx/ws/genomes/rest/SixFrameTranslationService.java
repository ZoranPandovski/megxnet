package net.megx.ws.genomes.rest;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

import net.megx.ws.core.CustomMediaType;
import net.megx.ws.core.Result;
import net.megx.ws.genomes.SixFrameTranslation;

@Path("v1/six-frame-translation/v1.0.0")
public class SixFrameTranslationService extends GenomesRestService{

	private SixFrameTranslation sixFrameTranslation;
	private static final int [] ALL_FRAMES = {1,2,3,-1,-2,-3};
	private static final int LINE_SIZE = 60;
	
	public void setSixFrameTranslation(SixFrameTranslation sixFrameTranslation) {
		this.sixFrameTranslation = sixFrameTranslation;
	}



	@GET
	@Produces({CustomMediaType.APPLICATION_CSV, 
		MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	public Response doSixFrameTranslation(
			@QueryParam("infile") final String infile,
			@QueryParam("outfile") final String outFile,
			@Context HttpServletRequest request){
		
		final String username = request.getUserPrincipal() != null ? 
					request.getUserPrincipal().getName() : null;
		if(username == null){
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		
		
		
		StreamingOutput so = new StreamingOutput() {
			
			@Override
			public void write(OutputStream output) throws IOException,
					WebApplicationException {
				
				try {
					if(outFile != null){
						sixFrameTranslation.sixFrameTranslate(username, ALL_FRAMES, 
								infile, outFile, false, LINE_SIZE);
						Result<String> result = new Result<String>(outFile);
						output.write(toJSON(result).getBytes());
					}else{
						sixFrameTranslation.sixFrameTranslate(username, ALL_FRAMES, 
								infile, output, false, LINE_SIZE);
					}
				} catch (Exception e) {
					handleWorkspaceAccessException(e);
				}
				
			}
		};
		ResponseBuilder rb = Response.ok(so);
		if(outFile != null && !"".equals(outFile.trim())){
			rb.type(MediaType.APPLICATION_JSON);
		}else{
			rb.type(CustomMediaType.APPLICATION_CSV);
		}
		return rb.build();
	}
}
