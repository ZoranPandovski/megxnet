/*
 * Copyright 2011 Max Planck Institute for Marine Microbiology 
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.iw.megx.ws.dto.mpiws;

public class Sample {

	/*
		SAMPLE    (gms_samples)
		------------
		-Sample          ---- site_name
		-Type            ---- study_type
		-Depth           ---- depth
		-DateSampled     ---- date_taken
		-EnvO-Lite		 ---- hab_lite
		-In Situ measurement   -- ?
		-Temperature     ---- temperatyre
		-Salinity        ---- salinity
		-Chlorophyll     ---- chlorophyll

	 */
	
	public String sample;
	public String type;
	public String depth;
	public String dateSampled;
	public String envolite;
	public String insitu;
	public String temperature;
	public String salinity;
	public String chlorophyll;
	
	public String getSample() {
		return sample;
	}
	public void setSample(String sample) {
		this.sample = sample;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDepth() {
		return depth;
	}
	public void setDepth(String depth) {
		this.depth = depth;
	}
	public String getDateSampled() {
		return dateSampled;
	}
	public void setDateSampled(String dateSampled) {
		this.dateSampled = dateSampled;
	}
	public String getEnvolite() {
		return envolite;
	}
	public void setEnvolite(String envolite) {
		this.envolite = envolite;
	}
	public String getInsitu() {
		return insitu;
	}
	public void setInsitu(String insitu) {
		this.insitu = insitu;
	}
	public String getTemperature() {
		return temperature;
	}
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	public String getSalinity() {
		return salinity;
	}
	public void setSalinity(String salinity) {
		this.salinity = salinity;
	}
	public String getChlorophyll() {
		return chlorophyll;
	}
	public void setChlorophyll(String chlorophyll) {
		this.chlorophyll = chlorophyll;
	}

	
}
