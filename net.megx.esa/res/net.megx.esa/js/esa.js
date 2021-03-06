jQuery(document).ready(function () {
    //Atmosphere framework specific variables
    var socket = atmosphere;
    var request = {
        url: ctx.siteUrl + '/topic/notifications/esa',
        contentType: "application/json",
        logLevel: 'debug',
        transport: 'websocket',
        fallbackTransport: 'long-polling',
        reconnectInterval: 5000,
        enableXDR: true,
        timeout: 60000
    };
    
    //Define event handlers
    request.onOpen = function (response) {
        console.log('Atmosphere connected using ' + response.transport);
    };
    
    request.onClose = function (response) {
    	console.log('Atmosphere connection gets closed ');
    };
    
    request.onMessage = function (response) {
        var message = response.responseBody.split('|')[1];
        var rowsToAdd = [];
        try {
            var json = atmosphere.util.parseJSON(message);
            var table = $(".megx_dataTable");
            for (i = 0; i < json.length; i++) {
                rowsToAdd.push({
                    'id': json[i].id,
                    'label': json[i].label,
                    'collectorId': json[i].collectorId,
                    'taken': json[i].taken,
                    'biome': json[i].biome,
                    'weatherCondition': json[i].weatherCondition,
                    'feature': json[i].feature,
                    'airTemperature': json[i].airTemperature,
                    'lat': json[i].lat,
                    'lon': json[i].lon,
                    'barcode': json[i].barcode,
                    'elevation': json[i].elevation,
                    'collection': json[i].collection,
                    'permit': json[i].permit,
                    'material': json[i].material,
                    'secchiDepth': json[i].secchiDepth,
                    'samplingDepth': json[i].samplingDepth,
                    'waterDepth': json[i].waterDepth,
                    'sampleSize': json[i].sampleSize,
                    'waterTemperature': json[i].waterTemperature,
                    'conductivity': json[i].conductivity,
                    'windSpeed': json[i].windSpeed,
                    'salinity': json[i].salinity,
                    'comment': json[i].comment,
                    'accuracy': json[i].accuracy,
                    'phosphate': json[i].phosphate,
                    'nitrate': json[i].nitrate,
                    'nitrite': json[i].nitrite,
                    'ph': json[i].ph,
                    'id': json[i].id
                });
            }

            //Add the newly created rows to the table
            table.dataTable().fnAddData(rowsToAdd);

         
        } catch (e) {
            console.log('This does not look like a valid JSON: ', message.data);
            return;
        }

        console.log('Server pushed down this data:', json);

    };

    request.onError = function (response) {
        console.log('Sorry, but there is some problem with your socket or the server is down' + response.error);
    };
    
    request.onTransportFailure = function(errorMsg, request){
    	console.log('Fallback is ' + request.fallbackTransport);
    };
    
    request.onClientTimeout = function(request){
    	console.log('Client closed the connection after a timeout. Reconnecting in ' + request.reconnectInterval);
    };
    
    request.onReconnect = function (response){
    	console.log('Connection lost. Trying to reconnect ' + request.reconnectInterval);
    };
    
    request.onReopen = function(response){
    	console.log('Atmosphere re-connected using ' + response.transport + ' trying to reconnect ' + request.reconnectInterval);
    };

    //Subscribe to AtmosphereServlet
    var subSocket = socket.subscribe(request);
});