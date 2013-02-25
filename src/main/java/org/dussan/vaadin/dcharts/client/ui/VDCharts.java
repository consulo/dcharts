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
package org.dussan.vaadin.dcharts.client.ui;

import java.util.HashMap;
import java.util.Map;

import org.dussan.vaadin.dcharts.client.events.EventObject;
import org.dussan.vaadin.dcharts.client.js.JqPlot;
import org.dussan.vaadin.dcharts.client.js.injector.JavaScriptInjector;
import org.dussan.vaadin.dcharts.client.handlers.BarDataHandler;
import org.dussan.vaadin.dcharts.client.handlers.BubbleDataHandler;
import org.dussan.vaadin.dcharts.client.handlers.DonutDataHandler;
import org.dussan.vaadin.dcharts.client.handlers.LineDataHandler;
import org.dussan.vaadin.dcharts.client.handlers.OhlcDataHandler;
import org.dussan.vaadin.dcharts.client.handlers.PieDataHandler;
import org.dussan.vaadin.dcharts.client.handlers.PyramidDataHandler;
import org.dussan.vaadin.dcharts.client.handlers.ResizeHandler;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class VDCharts extends SimplePanel implements Paintable {

	private static final String DCHARTS = "v-dcharts";
	private Boolean isAttached = null;
	private Boolean showChart = null;
	private String decimalSeparator = null;
	private String thousandsSeparator = null;
	private String dataSeries = null;
	private String options = null;

	private Boolean enableChartDataMouseEnterEvent = null;
	private Boolean enableChartDataMouseLeaveEvent = null;
	private Boolean enableChartDataClickEvent = null;
	private Boolean enableChartDataRightClickEvent = null;

	protected String uidl = null;
	protected ApplicationConnection client = null;
	private Element chart = null;
	private EventObject eventObject = null;

	public VDCharts() {
		// global id
		decimalSeparator = ".";
		thousandsSeparator = ",";

		getElement().setId(
				DCHARTS + "-" + ((long) (Math.random() * 10000000000000000L)));
		setStyleName(DCHARTS);
		isAttached = false;

		// enable/disable mouse events
		enableChartDataMouseEnterEvent = false;
		enableChartDataMouseLeaveEvent = false;
		enableChartDataClickEvent = false;
		enableChartDataRightClickEvent = false;
	}

	private void loadJQueryLibrary() {
		JavaScriptInjector.inject(JqPlot.CODE.jQuery().getText());
	}

	private void loadJqPlotLibrary() {
		// inject JqPlot css
		JqPlot.CODE.css().ensureInjected();

		// inject canvas emulator for Microsoft Internet Explorer
		if (BrowserInfo.get().isIE6() || BrowserInfo.get().isIE7()
				|| BrowserInfo.get().isIE8()) {
			JavaScriptInjector.inject(JqPlot.CODE.exCanvas().getText());
		}

		// initialize main JqPlot libraries
		JavaScriptInjector.inject(JqPlot.CODE.jqPlot().getText());
		JavaScriptInjector.inject(JqPlot.CODE.canvasTextRenderer().getText());
	}

	private static native void showChart(String id, String dataSeries,
			String options, String decimalSeparator, String thousandsSeparator)
	/*-{
	 	eval("var _options="+options+";");
	 	eval("var _dataSeries="+dataSeries+";");
		$wnd.jQuery(document).ready(function($){
			$wnd.jQuery('#'.concat(id)).empty();
			$.jqplot.sprintf.decimalMark=decimalSeparator;
			$.jqplot.sprintf.thousandsSeparator=thousandsSeparator;
			$.jqplot(id, _dataSeries, _options);
		});
	}-*/;

	private void refreshChart() {
		if (isAttached && showChart) {
			showChart(chart.getId(), dataSeries, options, decimalSeparator,
					thousandsSeparator);
		}
	}

	@Override
	protected void onLoad() {
		chart = DOM.createDiv();
		chart.setId("chart-" + ((int) (Math.random() * 1000000)));
		getElement().appendChild(chart);

		// load JQuery library
		loadJQueryLibrary();

		// load JqPlot libraries
		loadJqPlotLibrary();

		// load resize handler
		ResizeHandler.activate(this, false);
	}

	@Override
	protected void onUnload() {
		// unload resize handler
		ResizeHandler.activate(this, false);
	}

	private void activateJqPlotPlugins(String options) {
		if (enableChartDataClickEvent) {
			LineDataHandler.activateClick(this, chart.getId());
		}
		if (enableChartDataRightClickEvent) {
			LineDataHandler.activateRightClick(this, chart.getId());
		}

		if (options.contains("$wnd.jQuery.jqplot.BarRenderer")) {
			JavaScriptInjector.inject(JqPlot.CODE.barRenderer().getText());
			if (enableChartDataMouseEnterEvent) {
				BarDataHandler.activateMouseEnter(this, chart.getId());
			}
			if (enableChartDataMouseLeaveEvent) {
				BarDataHandler.activateMouseLeave(this, chart.getId());
			}
			if (enableChartDataClickEvent) {
				BarDataHandler.activateClick(this, chart.getId());
			}
			if (enableChartDataRightClickEvent) {
				BarDataHandler.activateRightClick(this, chart.getId());
			}
		}

		if (options.contains("$wnd.jQuery.jqplot.BubbleRenderer")) {
			JavaScriptInjector.inject(JqPlot.CODE.bubbleRenderer().getText());
			if (enableChartDataMouseEnterEvent) {
				BubbleDataHandler.activateMouseEnter(this, chart.getId());
			}
			if (enableChartDataMouseLeaveEvent) {
				BubbleDataHandler.activateMouseLeave(this, chart.getId());
			}
			if (enableChartDataClickEvent) {
				BubbleDataHandler.activateClick(this, chart.getId());
			}
			if (enableChartDataRightClickEvent) {
				BubbleDataHandler.activateRightClick(this, chart.getId());
			}
		}

		if (options.contains("$wnd.jQuery.jqplot.CategoryAxisRenderer")) {
			JavaScriptInjector.inject(JqPlot.CODE.categoryAxisRenderer()
					.getText());
		}
		if (options.contains("$wnd.jQuery.jqplot.CanvasAxisLabelRenderer")) {
			JavaScriptInjector.inject(JqPlot.CODE.canvasAxisLabelRenderer()
					.getText());
		}

		if (options.contains("$wnd.jQuery.jqplot.CanvasAxisTickRenderer")) {
			JavaScriptInjector.inject(JqPlot.CODE.canvasAxisTickRenderer()
					.getText());
		}

		if (options.contains("$wnd.jQuery.jqplot.DateAxisRenderer")) {
			JavaScriptInjector.inject(JqPlot.CODE.dateAxisRenderer().getText());
		}

		if (options.contains("$wnd.jQuery.jqplot.DonutRenderer")) {
			JavaScriptInjector.inject(JqPlot.CODE.donutRenderer().getText());
			if (enableChartDataMouseEnterEvent) {
				DonutDataHandler.activateMouseEnter(this, chart.getId());
			}
			if (enableChartDataMouseLeaveEvent) {
				DonutDataHandler.activateMouseLeave(this, chart.getId());
			}
			if (enableChartDataClickEvent) {
				DonutDataHandler.activateClick(this, chart.getId());
			}
			if (enableChartDataRightClickEvent) {
				DonutDataHandler.activateRightClick(this, chart.getId());
			}
		}

		if (options.contains("$wnd.jQuery.jqplot.EnhancedLegendRenderer")) {
			JavaScriptInjector.inject(JqPlot.CODE.enhancedLegendRenderer()
					.getText());
		}

		if (options.contains("$wnd.jQuery.jqplot.LogAxisRenderer")) {
			JavaScriptInjector.inject(JqPlot.CODE.logAxisRenderer().getText());
		}

		if (options.contains("$wnd.jQuery.jqplot.PieRenderer")) {
			JavaScriptInjector.inject(JqPlot.CODE.pieRenderer().getText());
			if (enableChartDataMouseEnterEvent) {
				PieDataHandler.activateMouseEnter(this, chart.getId());
			}
			if (enableChartDataMouseLeaveEvent) {
				PieDataHandler.activateMouseLeave(this, chart.getId());
			}
			if (enableChartDataClickEvent) {
				PieDataHandler.activateClick(this, chart.getId());
			}
			if (enableChartDataRightClickEvent) {
				PieDataHandler.activateRightClick(this, chart.getId());
			}
		}

		if (options.contains("$wnd.jQuery.jqplot.PyramidAxisRenderer")) {
			JavaScriptInjector.inject(JqPlot.CODE.pyramidAxisRenderer()
					.getText());
		}

		if (options.contains("$wnd.jQuery.jqplot.PyramidGridRenderer")) {
			JavaScriptInjector.inject(JqPlot.CODE.pyramidGridRenderer()
					.getText());
		}

		if (options.contains("$wnd.jQuery.jqplot.PyramidRenderer")) {
			JavaScriptInjector.inject(JqPlot.CODE.pyramidRenderer().getText());
			if (enableChartDataMouseEnterEvent) {
				PyramidDataHandler.activateMouseEnter(this, chart.getId());
			}
			if (enableChartDataMouseLeaveEvent) {
				PyramidDataHandler.activateMouseLeave(this, chart.getId());
			}
		}

		if (options.contains("$wnd.jQuery.jqplot.OHLCRenderer")) {
			JavaScriptInjector.inject(JqPlot.CODE.ohlcRenderer().getText());
			if (enableChartDataMouseEnterEvent) {
				OhlcDataHandler.activateMouseEnter(this, chart.getId());
			}
			if (enableChartDataMouseLeaveEvent) {
				OhlcDataHandler.activateMouseLeave(this, chart.getId());
			}
			if (enableChartDataClickEvent) {
				OhlcDataHandler.activateClick(this, chart.getId());
			}
		}

		if (options.contains("cursor:")) {
			JavaScriptInjector.inject(JqPlot.CODE.cursor().getText());
		}

		if (options.contains("highlighter:")) {
			JavaScriptInjector.inject(JqPlot.CODE.highlighter().getText());
		}

		if (options.contains("pointLabels:")) {
			JavaScriptInjector.inject(JqPlot.CODE.pointLabels().getText());
		}

		if (options.contains("trendline:")) {
			JavaScriptInjector.inject(JqPlot.CODE.trendline().getText());
		}
	}

	private String checkEnabledAnimationEffects(String options) {
		if (BrowserInfo.get().isIE6() || BrowserInfo.get().isIE7()
				|| BrowserInfo.get().isIE8()) {
			options = options.replaceAll("animate: true", "animate: false");
		}
		return options;
	}

	private String setChartDimensions(String options) {
		StringBuilder value = new StringBuilder();
		if (options != null && options.length() > 0) {
			value.append("{width: '");
			value.append(getOffsetWidth());
			value.append("px', height: '");
			value.append(getOffsetHeight());
			value.append("px', " + options.substring(1));
		}
		return value.toString();
	}

	private void sendMessage(String id, String event, String data) {
		if (eventObject == null
				|| !eventObject.equals(new EventObject(id, event, data))) {
			Map<String, Object> request = new HashMap<String, Object>();
			request.put("id", id == null ? new String() : id);
			request.put("event", event == null ? new String() : event);
			request.put("data", data == null ? "" : data);
			eventObject = new EventObject(id, event, data);

			// send request to server
			client.updateVariable(uidl, "request", request, true);
		}
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		if (client.updateComponent(this, uidl, true)) {
			return;
		}

		this.client = client;
		this.uidl = uidl.getId();

		int top = uidl.getIntAttribute("marginTop");
		int right = uidl.getIntAttribute("marginRight");
		int bottom = uidl.getIntAttribute("marginBottom");
		int left = uidl.getIntAttribute("marginLeft");
		getElement().getStyle().setHeight(
				getElement().getOffsetHeight() - top - bottom, Unit.PX);
		getElement().getStyle().setWidth(getElement().getOffsetWidth() - left,
				Unit.PX);
		getElement().getStyle().setMarginTop(top, Unit.PX);
		chart.getStyle().setMarginRight(right, Unit.PX);
		getElement().getStyle().setMarginBottom(bottom, Unit.PX);
		getElement().getStyle().setMarginLeft(left, Unit.PX);

		if (uidl.hasAttribute("enableChartDataMouseEnterEvent")) {
			enableChartDataMouseEnterEvent = uidl
					.getBooleanAttribute("enableChartDataMouseEnterEvent");
		}

		if (uidl.hasAttribute("enableChartDataMouseLeaveEvent")) {
			enableChartDataMouseLeaveEvent = uidl
					.getBooleanAttribute("enableChartDataMouseLeaveEvent");
		}

		if (uidl.hasAttribute("enableChartDataClickEvent")) {
			enableChartDataClickEvent = uidl
					.getBooleanAttribute("enableChartDataClickEvent");
		}

		if (uidl.hasAttribute("enableChartDataRightClickEvent")) {
			enableChartDataRightClickEvent = uidl
					.getBooleanAttribute("enableChartDataRightClickEvent");
		}

		if (uidl.hasAttribute("idChart")) {
			String idChart = uidl.getStringAttribute("idChart");
			if (idChart != null && idChart.length() > 0) {
				chart.setId(idChart);
			}
		}

		if (uidl.hasAttribute("decimalSeparator")) {
			String decimalSeparator = uidl
					.getStringAttribute("decimalSeparator");
			if (decimalSeparator != null && decimalSeparator.length() > 0) {
				this.decimalSeparator = decimalSeparator;
			}
		}

		if (uidl.hasAttribute("thousandsSeparator")) {
			String thousandsSeparator = uidl
					.getStringAttribute("thousandsSeparator");
			if (thousandsSeparator != null && thousandsSeparator.length() > 0) {
				this.thousandsSeparator = thousandsSeparator;
			}
		}

		dataSeries = new String("[]");
		if (uidl.hasAttribute("dataSeries")) {
			dataSeries = uidl.getStringAttribute("dataSeries");
		}

		options = new String("{}");
		if (uidl.hasAttribute("options")) {
			options = uidl.getStringAttribute("options");
			options = checkEnabledAnimationEffects(options);
			activateJqPlotPlugins(options);
		}
		options = setChartDimensions(options);

		showChart = uidl.getBooleanAttribute("showChart");
		if (showChart) {
			getElement().getStyle().clearDisplay();
			if (!isAttached) {
				isAttached = true;
				showChart(chart.getId(), dataSeries, options, decimalSeparator,
						thousandsSeparator);
			}
		} else {
			getElement().getStyle().setDisplay(Display.NONE);
		}
	}

}
