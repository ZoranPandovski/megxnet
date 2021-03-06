MegxMapWidgetLayers = function( cfg ) {

  this.init(cfg);
}
MegxMapWidgetLayers.prototype = {
  getLayersNames : function() {
    var rv = [];
    for ( var k in this.layers ) {
      rv.push(k);
    }
    return rv;
  },

  getLayersArray : function() {
    var rv = [];
    for ( var k in this.layers ) {
      rv.push(this.layers[k]);
    }
    return rv;
  },

  getLayers : function() {
    return this.layers;
  },

  get : function( layerName ) {
    return this.layers[layerName];
  },

  getLayersNameDesc : function() {
    return this.layersNames;
  },

  getLayerNameDesc : function( layer ) {
    return this.layersNames[layer];
  },

  init : function( cfg ) {
    var gms_wms_url = cfg.gms_wms_url;
    var extent = cfg.extent;
    var sam_genomes = cfg.sam_genomes;
    var sam_rrna = cfg.sam_rrna;
    var sam_phages = cfg.sam_phages;
    var sam_metagenomes = cfg.sam_metagenomes;

    var layers = {};

    this.layersNames = {
      'bathymetry' : {
        'genericName' : 'bathymetry',
        'niceName' : 'Bathymetry',
        'description' : 'Bathymetry Map'
      },
      'ena_samples' : {
        'genericName' : 'ena_samples',
        'niceName' : 'ENA Samples',
        'description' : 'Samples of the European Nucleotide Archive'
      },
      'osd_app' : {
        'genericName' : 'osd_app',
        'niceName' : 'OSD Sampling App Data',
        'description' : 'Shows all samples and observations from citizens and scientists from all over the world'
      },
      'osd_registry' : {
        'genericName' : 'osd_registry',
        'niceName' : 'OSD Registry Layer',
        'description' : 'Map of OSD participating Sites and Institutes'
      },
      'samplingsites' : {
        'genericName' : 'samplingsites',
        'niceName' : 'Samplingsites',
        'description' : 'All samplingsites'
      },
      'metagenomes' : {
        'genericName' : 'metagenomes',
        'niceName' : 'Metagenomes',
        'description' : 'Metagenome samples'
      },
      'genomes' : {
        'genericName' : 'genomes',
        'niceName' : 'Marine Microbial Genomes',
        'description' : 'Marine microbial genomes'
      },
      'rdnas' : {
        'genericName' : 'rdnas',
        'niceName' : 'Silva ssu/lsu',
        'description' : 'Silva ssu/lsu rDNA Samples'
      },
      'phages' : {
        'genericName' : 'phages',
        'niceName' : 'Marine Phages',
        'description' : 'Marine phage samples'
      },
      'woa05_temperature' : {
        'genericName' : 'woa05_temperature',
        'niceName' : 'WOA Temperature',
        'description' : 'World Ocean Atlas Temperature (2005)'
      },
      'satellite' : {
        'genericName' : 'satellite',
        'niceName' : 'satellite layer',
        'description' : 'World satellite image'
      },
      'satellite_mod' : {
        'genericName' : 'satellite_mod',
        'niceName' : 'satellite_mod layer',
        'description' : 'This is a layer for satellite_mod'
      },
      'undersea_arc' : {
        'genericName' : 'undersea_arc',
        'niceName' : 'undersea_arc layer',
        'description' : 'This is a layer for undersea_arc'
      },
      'undersea_point' : {
        'genericName' : 'undersea_point',
        'niceName' : 'undersea_point layer',
        'description' : 'Undersea Features'
      },
      'lakes' : {
        'genericName' : 'lakes',
        'niceName' : 'lakes layer',
        'description' : 'World lakes'
      },
      'boundaries' : {
        'genericName' : 'boundaries',
        'niceName' : 'Boundaries',
        'description' : 'Non authorative world boundaries'
      },
      'limitsoceans' : {
        'genericName' : 'limitsoceans',
        'niceName' : 'limitsoceans layer',
        'description' : 'This is a layer for limitsoceans'
      },
      'coordinates' : {
        'genericName' : 'coordinates',
        'niceName' : 'coordinates layer',
        'description' : 'This is a layer for coordinates'
      }
    };

    layers.bathymetry = new OpenLayers.Layer.WMS(
      this.layersNames["bathymetry"].genericName, gms_wms_url, {
        layers : 'bathymetry',
        format : 'image/png',
        transparent : "true"
      }, {
        isBaseLayer : false,
        singleTile : true,
        maxExtent : extent,
        maxResolution : "auto"
      });

    layers.ena_samples = new OpenLayers.Layer.WMS(
      this.layersNames["ena_samples"].genericName, gms_wms_url, {
        layers : 'ena_samples',
        format : 'image/png',
        transparent : "true"
      }, {

        isBaseLayer : false,
        singleTile : true,
        maxExtent : extent,
        maxResolution : "auto"
      });
    layers.ena_samples.desc = "European Nucleotide Archive Samples";
    layers.ena_samples.niceName = "ENA Samples";
    layers.ena_samples.controlHtml = "";

    layers.osd_app = new OpenLayers.Layer.WMS(this.layersNames["osd_app"].genericName,
      gms_wms_url, {
        layers : 'osd_app',
        format : 'image/png',
        transparent : "true"
      }, {
        isBaseLayer : false,
        singleTile : true,
        maxExtent : extent,
        maxResolution : "auto"
      });

    layers.osd_registry = new OpenLayers.Layer.WMS(
      this.layersNames["osd_registry"].genericName, gms_wms_url, {
        layers : 'osd_registry',
        format : 'image/png',
        transparent : "true"
      }, {
        isBaseLayer : false,
        singleTile : true,
        maxExtent : extent,
        maxResolution : "auto"
      });

    layers.samplingsites = new OpenLayers.Layer.WMS(
      this.layersNames["samplingsites"].genericName,
      gms_wms_url,
      {
        layers : 'samplingsites',
        format : 'image/png',
        transparent : "true",
      },
      {
        isBaseLayer : false,
        attribution : 'Provided by <a style="color:black" href="http://www.megx.net/">megx.net</a>',
        singleTile : true,
        maxExtent : extent,
        maxResolution : "auto"
      });

    layers.metagenomes= new OpenLayers.Layer.WMS(
      this.layersNames["metagenomes"].genericName,
      gms_wms_url,
      {
        layers : 'metagenomes',
        format : 'image/png',
        transparent : "true",
      },
      {
        isBaseLayer : false,
        attribution : 'Provided by <a style="color:black" href="http://www.megx.net/">megx.net</a>',
        singleTile : true,
        maxExtent : extent,
        maxResolution : "auto"
      });
    
    layers.genomes = new OpenLayers.Layer.WMS(
      this.layersNames["genomes"].genericName,
      gms_wms_url,
      {
        layers : 'genomes',
        format : 'image/png',
        transparent : "true",
      },
      {
        isBaseLayer : false,
        attribution : 'Provided by <a style="color:black" href="http://www.megx.net/">megx.net</a>',
        singleTile : true,
        maxExtent : extent,
        maxResolution : "auto"
      });
    
    layers.rdnas= new OpenLayers.Layer.WMS(
      this.layersNames["rdnas"].genericName,
      gms_wms_url,
      {
        layers : 'rdnas',
        format : 'image/png',
        transparent : "true",
      },
      {
        isBaseLayer : false,
        attribution : 'Provided by <a style="color:black" href="http://www.megx.net/">megx.net</a>',
        singleTile : true,
        maxExtent : extent,
        maxResolution : "auto"
      });
    
    layers.phages= new OpenLayers.Layer.WMS(
      this.layersNames["phages"].genericName,
      gms_wms_url,
      {
        layers : 'phages',
        format : 'image/png',
        transparent : "true",
      },
      {
        isBaseLayer : false,
        attribution : 'Provided by <a style="color:black" href="http://www.megx.net/">megx.net</a>',
        singleTile : true,
        maxExtent : extent,
        maxResolution : "auto"
      });
    
    layers.woa05_temperature = new OpenLayers.Layer.WMS(
      this.layersNames["woa05_temperature"].genericName, gms_wms_url, {
        layers : 'woa05_temperature',
        format : 'image/png',
        transparent : "false",
        DEPTH : "0",
        SEASON : "0",
        isBaseLayer : false
      }, {
        singleTile : true,
        maxExtent : extent,
        maxResolution : "auto"
      });
    layers.woa05_temperature.desc = "World Ocean Atlas Temperature";
    layers.woa05_temperature.niceName = "WOA Temperature";
    layers.woa05_temperature.controlHtml = "";

    layers.satellite = new OpenLayers.Layer.WMS(
      this.layersNames["satellite"].genericName, gms_wms_url, {
        layers : 'satellite',
        format : 'image/png',
        transparent : "true"
      }, {
        isBaseLayer : false,
        singleTile : true,
        maxExtent : extent,
        maxResolution : "auto"
      });

    layers.satellite_mod = new OpenLayers.Layer.WMS(
      this.layersNames["satellite_mod"].genericName, gms_wms_url, {
        layers : 'satellite_mod',
        format : 'image/png',
        transparent : "false"
      }, {
        isBaseLayer : true,
        singleTile : true,
        maxExtent : extent,
        maxResolution : "auto"
      });

    layers.undersea_arc = new OpenLayers.Layer.WMS(
      this.layersNames["undersea_arc"].genericName, gms_wms_url, {
        layers : 'undersea_arc',
        format : 'image/png',
        transparent : "true"
      }, {
        isBaseLayer : false,
        singleTile : true,
        maxExtent : extent,
        maxResolution : "auto"
      });

    layers.undersea_point = new OpenLayers.Layer.WMS(
      this.layersNames["undersea_point"].genericName, gms_wms_url, {
        layers : 'undersea_point',
        format : 'image/png',
        transparent : "true"
      }, {
        isBaseLayer : false,
        singleTile : true,
        maxExtent : extent,
        maxResolution : "auto"
      });

    layers.lakes = new OpenLayers.Layer.WMS(
      this.layersNames["lakes"].genericName, gms_wms_url, {
        layers : 'lakes',
        format : 'image/png',
        transparent : "true"
      }, {
        isBaseLayer : false,
        singleTile : true,
        maxExtent : extent,
        maxResolution : "auto"
      });

    layers.boundaries = new OpenLayers.Layer.WMS(
      this.layersNames["boundaries"].genericName, gms_wms_url, {
        layers : 'boundaries',
        format : 'image/png',
        transparent : "true"
      }, {
        isBaseLayer : false,
        singleTile : true,
        maxExtent : extent,
        maxResolution : "auto",
        wrapDateLine: true
      });
    layers.boundaries.niceName = "Boundaries";
    layers.boundaries.description = "Non-authoritative boundaries of the World Oceans";

    layers.limitsoceans = new OpenLayers.Layer.WMS(
      this.layersNames["limitsoceans"].genericName, gms_wms_url, {
        layers : 'limitsoceans',
        format : 'image/png',
        transparent : "true"
      }, {
        isBaseLayer : false,
        singleTile : true,
        maxExtent : extent,
        maxResolution : "auto"
      });

    layers.coordinates = new OpenLayers.Layer.WMS(
      this.layersNames["coordinates"].genericName, gms_wms_url, {
        layers : 'coordinates',
        format : 'image/png',
        transparent : "true"
      }, {
        isBaseLayer : false,
        singleTile : true,
        maxExtent : extent,
        maxResolution : "auto"
      });

    this.layers = layers;
  }
}