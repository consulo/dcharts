/**
 * Copyright (C) 2012-2013  Dušan Vejnovič  <vaadin@dussan.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dussan.vaadin.dcharts.events.click;

import org.dussan.vaadin.dcharts.events.ChartData;

import com.google.gwt.event.shared.GwtEvent;

public class ChartDataClickEvent extends GwtEvent<ChartDataClickHandler> {

	private ChartData chartData = null;
	public static final Type<ChartDataClickHandler> TYPE = new Type<ChartDataClickHandler>();

	public ChartDataClickEvent(ChartData chartData) {
		this.chartData = chartData;
	}

	public ChartData getChartData() {
		return chartData;
	}

	@Override
	public Type<ChartDataClickHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ChartDataClickHandler handler) {
		handler.onChartDataClick(this);
	}

	public static Type<ChartDataClickHandler> getType() {
		return TYPE;
	}

}
