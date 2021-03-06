package net.megx.esa.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import net.megx.broadcast.proxy.BroadcasterProxy;
import net.megx.esa.rest.util.SampleDeserializer;
import net.megx.esa.util.ImageResizer;
import net.megx.form.widget.model.FormWidgetResult;
import net.megx.megdb.esa.EarthSamplingAppService;
import net.megx.model.esa.Sample;
import net.megx.model.esa.SampleLocationCount;
import net.megx.model.esa.SampleObservation;
import net.megx.model.esa.SamplePhoto;
import net.megx.model.esa.SampleRow;
import net.megx.twitter.BaseTwitterService;
import net.megx.ui.table.json.TableDataResponse;
import net.megx.ws.core.Result;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 *
 * @author borce.jadrovski
 *
 */
@Path("v1/esa/v1.0.0")
public class EarthSamplingAppAPI extends BaseRestService {

	private EarthSamplingAppService service;
	private BroadcasterProxy broadcasterProxy;
	private ImageResizer imageResizer;
	private BaseTwitterService twitterService;

	private static final int THUMBNAIL_WIDTH = 240;
	private static final int THUMBNAIL_HEIGHT = 240;

	private static final String CSV_HEADER = "ID,Taken,Modified,Collector_ID,Label,Barcode,Project_ID,Ship_name,Boat_manufacturer,Boat_model,Boat_length,Homeport,Nationality,Elevation,Biome,Feature,Collection,Permit, Material, Secchi_depth, Sampling_depth,Water_depth,Sample_size,Weather_condition,Air_temperature,Water_temperature,Conductivity,Wind_speed,Salinity,Comment,Lat,Lon,Accuracy,Phosphate,Nitrate,Nitrite,pH,MyOsd_number,Filter_one,Filter_two,Number_photos";
	private static final String CSV_ROW = "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s";

	public EarthSamplingAppAPI(EarthSamplingAppService service,
			BroadcasterProxy broadcasterProxy, BaseTwitterService twitterService) {
		this.service = service;
		this.broadcasterProxy = broadcasterProxy;
		this.imageResizer = new ImageResizer();
		this.twitterService = twitterService;
		gson = new GsonBuilder()
				.registerTypeAdapter(SamplePhoto.class,
						new JsonDeserializer<SamplePhoto>() {

							@Override
							public SamplePhoto deserialize(JsonElement el,
									Type type,
									JsonDeserializationContext context)
									throws JsonParseException {
								SamplePhoto sp = new SamplePhoto();
								JsonObject jo = el.getAsJsonObject();
								if (!jo.has("uuid")) {
									throw new JsonParseException(
											"Photo must contain UUID.");
								}
								sp.setUuid(jo.get("uuid").getAsString());

								if (jo.has("mimeType")) {
									sp.setMimeType(jo.get("mimeType")
											.getAsString());
								}
								if (jo.has("data")) {
									sp.setData(Base64.decodeBase64(jo.get(
											"data").getAsString()));
								}

								return sp;
							}

						})
				.registerTypeAdapter(Sample.class, new SampleDeserializer())
				.registerTypeAdapter(Double.class,
						new JsonSerializer<Double>() {
							@Override
							public JsonElement serialize(Double num, Type type,
									JsonSerializationContext context) {

								if (num != null && !num.isNaN()) {
									return new JsonPrimitive(num.toString());
								}
								return new JsonPrimitive("");

							}
						})
				.registerTypeAdapter(Double.class,
						new JsonSerializer<Double>() {
							@Override
							public JsonElement serialize(Double num, Type type,
									JsonSerializationContext context) {

								if (num != null && !num.isNaN()) {
									return new JsonPrimitive(num.toString());
								}
								return new JsonPrimitive("");

							}
						}).serializeNulls().setPrettyPrinting()
				.serializeSpecialFloatingPointValues().create();

	}

	/**
	 *
	 * @return JSON formatted string of the all of the samples stored in DB.
	 */
	@GET
	@Path("allSamples")
	public String getAllSamples() {
		List<SampleRow> samples;
		try {
			samples = service.getAllSamples();
			TableDataResponse<SampleRow> resp = new TableDataResponse<SampleRow>();
			resp.setData(samples);
			return toJSON(resp);
		} catch (Exception e) {
			return toJSON(handleException(e));
		}

	}

	@GET
	@Path("sampledOceans")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSampledOceans() {
		try {
			List<SampleLocationCount> sampledOceans = service
					.getSamplesLocationAndCount();
			return toJSON(new Result<List<SampleLocationCount>>(sampledOceans));
		} catch (Exception e) {
			return toJSON(handleException(e));
		}
	}

	// TODO why a post? and not a get? and sampleIDs as form param?
	@POST
	@Path("downloadSamples.csv")
	public Response downloadSamples(
			@FormParam("sampleIds") final String sampleIds) {
		ResponseBuilder rb = Response.ok().entity(new StreamingOutput() {

			@Override
			public void write(OutputStream out) throws IOException,
					WebApplicationException {

				try {
					List<String> ids = new ArrayList<String>(Arrays
							.asList(sampleIds.split(",")));
					List<Sample> samples = service.downloadSamples(ids);
					PrintWriter writer = new PrintWriter(out);
					writer.println(CSV_HEADER);
					for (Sample sample : samples) {
						writer.println(String.format(CSV_ROW, sample.getId(),
								sample.getTaken(), sample.getModified(),
								sample.getCollectorId(), sample.getLabel(),
								sample.getBarcode(), sample.getProjectId(),
								sample.getShipName(),
								sample.getBoatManufacturer(),
								sample.getBoatModel(), sample.getBoatLength(),
								sample.getHomeport(), sample.getNationality(),
								sample.getElevation(), sample.getBiome(),
								sample.getFeature(), sample.getCollection(),
								sample.getPermit(), sample.getMaterial(),
								sample.getSecchiDepth(),
								sample.getSamplingDepth(),
								sample.getWaterDepth(), sample.getSampleSize(),
								sample.getWeatherCondition(),
								sample.getAirTemperature(),
								sample.getWaterTemperature(),
								sample.getConductivity(),
								sample.getWindSpeed(), sample.getSalinity(),
								sample.getComment(), sample.getLat(),
								sample.getLon(), sample.getAccuracy(),
								sample.getPhosphate(), sample.getNitrate(),
								sample.getNitrite(), sample.getPh(),
								sample.getMyosdNumber(), sample.getFilterOne(),
								sample.getFilterTwo(),
								sample.getPhotos().length));
					}
					writer.flush();
					out.flush();
				} catch (Exception e) {
					throw new WebApplicationException(500);
				}
			}
		});

		rb.header("Content-Disposition: attachment",
				"filename=\"samplesData.csv\"");
		rb.type(MediaType.APPLICATION_OCTET_STREAM);
		return rb.build();
	}

	/**
	 *
	 * @return JSON formatted string of a sample stored in DB.
	 */
	@GET
	@Path("sample")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSample(@QueryParam("sampleId") String sampleId) {
		Sample sample;
		try {
			sample = service.getSample(sampleId);
			return toJSON(new Result<Sample>(sample));
		} catch (Exception e) {
			return toJSON(handleException(e));
		}
	}

	@GET
	@Path("renzo")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRenzo() {

		int idx = 1;
		Random random = new Random();
		SampleRow row = null;
		List<SampleRow> samples;
		try {
			samples = service.getAllSamples();
			idx = random.nextInt(samples.size());
			row = samples.get(idx);
			tweet(row.getLon(), row.getLat(), row.getTaken());
			return gson.toJson(samples);
		} catch (Exception e) {
			return gson.toJson("error");
		}

	}

	/**
	 * @param nbObservations
	 * @return JSON formatted string of latest nbObservations (or less if query
	 *         returns less) sample retrievals
	 */
	@GET
	@Path("observations/{nbObservations}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getObservations(
			@PathParam("nbObservations") int nbObservations) {
		List<SampleObservation> observations = new ArrayList<SampleObservation>();
		try {
			observations = service.getLatestObservations(nbObservations);
			return toJSON(new Result<List<SampleObservation>>(observations));
		} catch (Exception e) {
			return toJSON(handleException(e));
		}
	}

	/**
	 *
	 * @param ID
	 *            of the collector of the samples
	 * @return JSON formatted string of the samples created by the collector if
	 *         any.
	 */
	@GET
	@Path("samples/{creator}")
	public String getSamplesForCollector(@PathParam("creator") String creator) {
		List<Sample> samples;
		try {
			samples = service.getSamples(creator);
			return toJSON(new Result<List<Sample>>(samples));
		} catch (Exception e) {
			return toJSON(handleException(e));
		}

	}

	/**
	 * Stores sample in the database from our web online form
	 *
	 * @param air_temperature
	 *            the air temperature value
	 * @param biome
	 *            the biome value
	 * @param comment
	 *            the comment
	 * @param date_taken
	 *            the date when the sample is taken
	 * @param fun
	 *            the real sample or just for fun
	 * @param air_temperature
	 *            the air_temperature value
	 * @param gps_accuracy
	 *            the gps accuracy value
	 * @param json
	 *            the whole form json
	 * @param latitude
	 *            the latitude value
	 * @param longitude
	 *            the longitude value
	 * @param nitrate
	 *            the nitrate value
	 * @param nitrite
	 *            the nitrite value
	 * @param origin
	 *            the origin value
	 * @param phosphate
	 *            the phosphate value
	 * @param ph
	 *            the ph value
	 * @param salinity
	 *            the salinity value
	 * @param sample_name
	 *            the name of the taken sample
	 * @param secchi_depth
	 *            the secchi depth value
	 * @param submit
	 *            the submit
	 * @param time_taken
	 *            the time when the sample was taken
	 * @param version
	 *            the version of the form
	 * @param water_temperature
	 *            the water temperature value
	 * @param weather_condition
	 *            the weather condition
	 * @param wind_speed
	 *            the wind speed value
	 * @param sampling_kit
	 *            the sampling kit value
	 * @param myosd_number
	 *            the my osd number value
	 * @param filter_one
	 *            the filter one value
	 * @param filter_two
	 *            the filter two value
	 */
	@Path("observation")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response storeSingleSample(
			@FormParam("air_temperature") String airTemperature,
			@FormParam("biome") String biome,
			@FormParam("comment") String comment,
			@FormParam("date_taken") String dateTaken,
			@FormParam("depth") Double depth, @FormParam("fun") Boolean fun,
			@FormParam("gps_accuracy") String gpsAccuracy,
			@FormParam("json") String json,
			@FormParam("latitude") String latitude,
			@FormParam("longitude") String longitude,
			@FormParam("nitrate") String nitrate,
			@FormParam("nitrite") String nitrite,
			@FormParam("origin") String origin, @FormParam("ph") String ph,
			@FormParam("phosphate") String phosphate,
			@FormParam("salinity") String salinity,
			@FormParam("sample_name") String sampleName,
			@FormParam("secchi_depth") String secchiDepth,
			@FormParam("submit") String submit,
			@FormParam("time_taken") String timeTaken,
			@FormParam("version") String version,
			@FormParam("water_temperature") String waterTemperature,
			@FormParam("weather_condition") String weatherCondition,
			@FormParam("wind_speed") String windSpeed,
			@FormParam("sampling_kit") Boolean samplingKit,
			@FormParam("myosd_number") String myosdNumber,
			@FormParam("filter_one") String filterOne,
			@FormParam("filter_two") String filterTwo,
			@Context HttpServletRequest request) {

		if (version == null || version.isEmpty()) {
			return Response
					.status(Status.BAD_REQUEST)
					.entity(toJSON(new FormWidgetResult(true,
							"Version not provided.", null))).build();
		}
		if (latitude == null || latitude.isEmpty()) {
			log.error("Latitude parameter not provided.");
			return Response
					.status(Status.BAD_REQUEST)
					.entity(toJSON(new FormWidgetResult(true,
							"Latitude parameter not provided.", null))).build();

		} else if (!latitude.matches("-?\\d{1,2}(\\.\\d+)?")) {
			log.error("Latitude parameter is not valid number format.");
			return Response
					.status(Status.BAD_REQUEST)
					.entity(toJSON(new FormWidgetResult(true,
							"Latitude parameter is not valid number format.",
							null))).build();

		} else if (Double.valueOf(latitude) < -90
				|| Double.valueOf(latitude) > 90) {
			log.error("Latitude value is out of range.");
			return Response
					.status(Status.BAD_REQUEST)
					.entity(toJSON(new FormWidgetResult(true,
							"Latitude value is out of range.", null))).build();

		} else if (longitude == null || longitude.isEmpty()) {
			log.error("Longitude parameter not provided.");
			return Response
					.status(Status.BAD_REQUEST)
					.entity(toJSON(new FormWidgetResult(true,
							"Longitude parameter not provided.", null)))
					.build();

		} else if (!longitude.matches("-?\\d{1,3}(\\.\\d+)?")) {
			log.error("Longitude parameter is not valid number format.");
			return Response
					.status(Status.BAD_REQUEST)
					.entity(toJSON(new FormWidgetResult(true,
							"Longitude parameter is not valid number format.",
							null))).build();

		} else if (Double.valueOf(longitude) < -180
				|| Double.valueOf(longitude) > 180) {
			log.error("Longitude value is out of range.");
			return Response
					.status(Status.BAD_REQUEST)
					.entity(toJSON(new FormWidgetResult(true,
							"Longitude value is out of range.", null))).build();

		}

		if (sampleName == null || sampleName.isEmpty()) {
			return Response
					.status(Status.BAD_REQUEST)
					.entity(toJSON(new FormWidgetResult(true,
							"Sample Name not provided.", null))).build();
		}

		if (dateTaken == null || dateTaken.isEmpty()) {
			return Response
					.status(Status.BAD_REQUEST)
					.entity(toJSON(new FormWidgetResult(true,
							"Date not provided.", null))).build();
		}

		if (timeTaken == null || timeTaken.isEmpty()) {
			return Response
					.status(Status.BAD_REQUEST)
					.entity(toJSON(new FormWidgetResult(true,
							"Time not provided.", null))).build();
		}

		Sample sample = new Sample();
		String savedSample = "";
		List<Sample> samplesToBroadcast = new ArrayList<Sample>();
		String sampleCreator = "";
		Date taken = null;
		Date modified = Calendar.getInstance().getTime();

		if (request.getUserPrincipal() != null) {
			sampleCreator = request.getUserPrincipal().getName();
		} else {
			sampleCreator = "anonymous";
		}

		try {
			taken = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(dateTaken
					+ " " + timeTaken);
		} catch (ParseException e) {
			log.error("Could not parse date.", e);
		}

		String id = UUID.randomUUID().toString();

		sample.setId(id);
		sample.setAirTemperature(parseDouble(airTemperature));
		sample.setBiome(biome);
		sample.setComment(comment);
		sample.setSamplingDepth(depth);
		sample.setFun(fun);
		sample.setAccuracy(parseDouble(gpsAccuracy));
		sample.setRawData(json);
		sample.setLat(Double.parseDouble(latitude));
		sample.setLon(Double.parseDouble(longitude));
		sample.setNitrate(parseDouble(nitrate));
		sample.setNitrite(parseDouble(nitrite));
		sample.setPh(parseDouble(ph));
		sample.setPhosphate(parseDouble(phosphate));
		sample.setSalinity(parseDouble(salinity));
		sample.setLabel(sampleName);
		sample.setSecchiDepth(parseDouble(secchiDepth));
		sample.setAppVersion(version);
		sample.setWaterTemperature(parseDouble(waterTemperature));
		sample.setWeatherCondition(weatherCondition);
		sample.setWindSpeed(parseDouble(windSpeed));
		sample.setTaken(taken);
		sample.setModified(modified);
		sample.setCollectorId("anonymous");
		sample.setUserName(sampleCreator);
		sample.setSamplingKit(samplingKit);
		sample.setMyosdNumber(parseInteger(myosdNumber));
		sample.setFilterOne(parseDouble(filterOne));
		sample.setFilterTwo(parseDouble(filterTwo));

		try {
			savedSample = service.storeSingleSample(sample);
		} catch (Exception e) {
			log.error("Could not save sample", e);
			return Response
					.serverError()
					.entity(toJSON(new FormWidgetResult(true,
							"Database error, could not save sample.", null)))
					.build();
		}

		// Broadcast JSON string with saved samples to subscribed clients
		// and tweet about it
		if (savedSample == sample.getId()) {
			samplesToBroadcast.add(sample);
			this.tweet(sample.getLon(), sample.getLat(), sample.getTaken());
		}
		this.broadcasterProxy.broadcastMessage("/topic/notifications/esa",
				toJSON(samplesToBroadcast));

		String url = "";
		URI uri = null;
		try {
			url = "http://" + request.getServerName() + ":"
					+ request.getServerPort() + request.getContextPath()
					+ "/osd-app/samples";
			uri = new URI(url);
		} catch (URISyntaxException e) {
			log.error("Wrong URI" + url, e);
		}
		return Response
				.ok()
				.entity(toJSON(new FormWidgetResult(false,
						"Sample saved successfully.", uri.toString()))).build();
	}

	/**
	 * Stores samples in the database. These samples are being transferred from
	 * the user's device.
	 *
	 * @param samplesJson
	 *            JSON formatted string representing the samples to be stored in
	 *            the database.
	 * @return Comma delimited string containing the ID's of the successfully
	 *         saved samples.
	 */
	@Path("samples")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String storeSamples(@FormParam("samples") String samplesJson,
			@Context HttpServletRequest request) {
		try {
			if (samplesJson == null) {
				return toJSON(new Result<String>(true, "Samples not provided",
						"bad-request"));
			}
			Sample[] samples = gson.fromJson(samplesJson, Sample[].class);
			List<Sample> samplesToSave = new ArrayList<Sample>();
			Map<String, String> errorMap = new HashMap<String, String>();
			List<String> savedSamples = new ArrayList<String>();
			List<Sample> samplesToBroadcast = new ArrayList<Sample>();
			Map<String, Object> result = new HashMap<String, Object>();
			String sampleCreator = request.getUserPrincipal().getName();
			for (Sample sample : samples) {
				if (validateSample(sample)) {

					sample.setUserName(sampleCreator);
					samplesToSave.add(sample);
				} else {
					if (sample.getLabel() != null) {
						errorMap.put(sample.getId(),
								"Sample " + sample.getLabel()
										+ " is missing latitude or longitude");
					} else {
						errorMap.put(sample.getId(), "Sample " + sample.getId()
								+ " is missing latitude, longitude or label.");
					}
				}
			}
			savedSamples = service.storeSamples(samplesToSave);
			result.put("saved", savedSamples);
			result.put("errors", errorMap);
			Result<Map<String, Object>> resultToReturn = new Result<Map<String, Object>>(
					result);

			// Broadcast JSON string with saved samples to subscribed clients
			// and tweet about it
			for (Sample sample : samplesToSave) {
				if (savedSamples.contains(sample.getId())) {
					samplesToBroadcast.add(sample);
					this.tweet(sample.getLon(), sample.getLat(),
							sample.getTaken());
				}
			}
			this.broadcasterProxy.broadcastMessage("/topic/notifications/esa",
					toJSON(samplesToBroadcast));

			return toJSON(resultToReturn);
		} catch (Exception e) {
			return toJSON(handleException(e));
		}
	}

	/**
	 * Tweet the message to megx twitter account with the longitude, latitude
	 * values and link to /osd-app/samples page
	 *
	 */
	private void tweet(Double longitude, Double latitude, Date date) {
		String link = "https://mb3is.megx.net/osd-app/samples";

		DecimalFormat fmt = new DecimalFormat("0.00");

		String lat = fmt.format(latitude);
		String lon = fmt.format(longitude);

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateTaken = dateFormat.format(date);

		String[] greetings = { "Hurray", "Awesome", "Fantastic", "Super", ":)",
				"Incredible" };

		Random random = new Random();
		int idx = random.nextInt(greetings.length);

		String tweet = greetings[idx] + "! New observation reached us from "
				+ lat + ", " + lon + " at " + dateTaken + ". See " + link
				+ " @Micro_B3 #osd #myosd";
		this.twitterService.geoTweet(tweet, latitude, longitude);
	}

	private boolean validateSample(Sample sample) {
		if (sample.getLat() == null || sample.getLon() == null) {
			return false;
		}
		return true;
	}

	private Double parseDouble(String dbl) {
		Double d = null;
		if (dbl != null && !dbl.equals("")) {
			d = Double.parseDouble(dbl);
		}
		return d;
	}

	private Integer parseInteger(String num) {
		Integer i = null;
		if (num != null && !num.equals("")) {
			i = Integer.parseInt(num);
		}
		return i;
	}

	/**
	 * Store a single photo that belongs to already saved sample in the
	 * database.
	 *
	 * @param request
	 *            Contains the binary data for the photo to be saved along with
	 *            the photos' UUID, MIME type and path properties.
	 * @throws IOException
	 * @throws Exception
	 *             If the photo to be saved doesn't belong to a sample that is
	 *             already saved in the database, exception is thrown.
	 */
	@Path("photos")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@POST
	public void storePhotos(@Context HttpServletRequest request)
			throws WebApplicationException, IOException {

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			FileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Parse the request
			List items;
			try {
				items = upload.parseRequest(request);
			} catch (FileUploadException e) {
				throw new WebApplicationException(e);
			}
			SamplePhoto photoToSave = new SamplePhoto();

			Iterator iter = items.iterator();

			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();

				if (!item.isFormField()) {
					byte[] imageData = item.get();
					photoToSave.setData(imageData);
					photoToSave.setThumbnail(this.imageResizer.resizeImage(
							imageData, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT));
					photoToSave.setMimeType(item.getContentType());
				} else {
					if (item.getFieldName().equalsIgnoreCase("uuid")) {
						photoToSave.setUuid(new String(item.get()));
					} else if (item.getFieldName().equalsIgnoreCase("path")) {
						photoToSave.setPath(new String(item.get()));
					}
				}
			}

			List<String> uuids;
			try {
				uuids = service.storePhotos(Arrays.asList(photoToSave));
			} catch (Exception e) {
				throw new WebApplicationException(e);
			}

			if (uuids.size() == 0) {
				throw new WebApplicationException();
			}
		}
	}

	/**
	 * Retrieve the configuration that will be used by the Citizen version of
	 * the client application.
	 *
	 * @return JSON formatted string of the configuration to be used by the
	 *         client application.
	 */
	@Path("citizenConfig")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getConfigurationForCitizen() {
		try {
			Map<String, Object> configuration = new HashMap<String, Object>();
			Result<Map<String, Object>> r = new Result<Map<String, Object>>(
					configuration);
			List<String> exported = new LinkedList<String>();
			Map<String, String> exportedCfg = service
					.getConfigurationForCitizen("categories");
			for (Map.Entry<String, String> e : exportedCfg.entrySet()) {
				if (e.getValue().contains("exported")) {
					exported.add(e.getKey());
				}
			}
			for (String exportedCategory : exported) {
				Map<String, String> cat = service
						.getConfigurationForCitizen(exportedCategory);
				configuration.put(exportedCategory, cat);
			}
			return toJSON(r);
		} catch (Exception e) {
		  log.error("Could not load ciziten config", e);
			return toJSON(handleException(e));
		}
	}

	/**
	 * Retrieve the configuration that will be used by the Scientist version of
	 * the client application.
	 *
	 * @return JSON formatted string of the configuration to be used by the
	 *         client application.
	 */
	@Path("scientistConfig")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getConfigurationForScientist() {
		try {
			Map<String, Object> configuration = new HashMap<String, Object>();

			Result<Map<String, Object>> r = new Result<Map<String, Object>>(
					configuration);

			List<String> exported = new LinkedList<String>();
			Map<String, String> exportedCfg = service
					.getConfigurationForScientist("categories");
			for (Map.Entry<String, String> e : exportedCfg.entrySet()) {
				if (e.getValue().contains("exported")) {
					exported.add(e.getKey());
				}
			}
			for (String exportedCategory : exported) {
				Map<String, String> cat = service
						.getConfigurationForScientist(exportedCategory);
				configuration.put(exportedCategory, cat);
			}
			return toJSON(r);
		} catch (Exception e) {
			return toJSON(handleException(e));
		}
	}

	public static void main(String[] args) {
		String sampleJSON = "[{\"id\":1,\"collectorId\":\"username\",\"projectId\":\"Micro B3\",\"userName\":\" \","
				+ "\"shipName\":\"\",\"nationality\":\"\","
				+ "\"photos\":[{\"uuid\":\"random-uuid-photo-identificator\",\"bytes\":\"base64-encoded-string-of-the-image-data-optional\"}]"
				+ ",\"label\":\"label\",\"barcode\":\"23897238947923\",\"material\":\"material\",\"biome\":\"biome\",\"feature\":\"feat\",\"collectionMethod\":\"coll\",\"permit\":\"yes\",\"sampleSize\":\"89\",\"conductivity\":\"conduc\",\"samplingDepths\":\"34\",\"comment\":\"Commentos\",\"time\":\"Tue Nov 20 2012 13:05:59 GMT+0100 (CET)\",\"weatherCondition\":\"Clear night\",\"airTemperature\":\"3\",\"waterTemperature\":\"4\",\"windSpeed\":\"56\",\"salinity\":\"6\",\"lat\":\"21.21\",\"lon\":\"41.41\",\"accuracy\":\"30\",\"elevation\":\"3\",\"secchiDepth\":\"3\",\"waterDepth\":\"6\"}]";
		Gson gson = new GsonBuilder().registerTypeAdapter(SamplePhoto.class,
				new JsonDeserializer<SamplePhoto>() {

					@Override
					public SamplePhoto deserialize(JsonElement el, Type type,
							JsonDeserializationContext context)
							throws JsonParseException {
						SamplePhoto sp = new SamplePhoto();
						JsonObject jo = el.getAsJsonObject();
						if (!jo.has("uuid")) {
							throw new JsonParseException(
									"Photo must contain UUID.");
						}
						sp.setUuid(jo.get("uuid").getAsString());

						if (jo.has("mimeType")) {
							sp.setMimeType(jo.get("mimeType").getAsString());
						}
						if (jo.has("data")) {
							sp.setData(Base64.decodeBase64(jo.get("data")
									.getAsString()));
						}
						return sp;
					}

				}).create();

		Sample[] samples = gson.fromJson(sampleJSON, Sample[].class);
		System.out.println(Arrays.toString(samples));
	}

}
