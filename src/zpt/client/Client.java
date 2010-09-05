package zpt.client;

import teropa.globetrotter.client.Map;
import teropa.globetrotter.client.common.LonLat;
import teropa.globetrotter.client.common.Position;
import teropa.globetrotter.client.controls.CopyrightText;
import teropa.globetrotter.client.controls.Panner;
import teropa.globetrotter.client.controls.Zoomer;
import teropa.globetrotter.client.osm.OpenStreetMapLayer;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.widgetideas.graphics.client.Color;

public class Client implements EntryPoint {

	public final Resources resources = GWT.create(Resources.class);
	
	private Map map;
	private EpidemicLayer solanum;
	private TimeControl tc;
	
	public static final int NUM_STEPS = 26;
	
	public void onModuleLoad() {
		resources.style().ensureInjected();
		
		DockLayoutPanel dock = new DockLayoutPanel(Unit.PX);
		initLeft(dock);
		initMain(dock);
		RootLayoutPanel.get().add(dock);
		
		// TODO: Find out why we need to do this
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				RootLayoutPanel.get().onResize();
			}
		});
	}

	private void initMain(DockLayoutPanel dock) {
		LayoutPanel mainPanel = new LayoutPanel();
		
		map = new Map("100%", "100%");
		map.setResolutions(getResolutions(), 2);
		map.setCenter(new LonLat(391357.58482, 5476196.443835));
		map.getView().setBackgroundColor(new Color("#c8c8c8"));
		OpenStreetMapLayer base = new OpenStreetMapLayer(GWT.getHostPageBaseURL() + "tiles/osm", "Mapnik", true);
		map.addLayer(base);
		
		solanum = new EpidemicLayer(GWT.getHostPageBaseURL() + "tiles/solanum", "Solanum", false);
		map.addLayer(solanum);
		
		map.addControl(new Panner(), Position.TOP_LEFT);
		map.addControl(new Zoomer(), Position.MIDDLE_LEFT);
		map.addControl(new CopyrightText(initCopyrightText()), Position.BOTTOM_LEFT);
		
		mainPanel.add(map);
		
		tc = new TimeControl(solanum);
		mainPanel.add(tc);
		mainPanel.setWidgetBottomHeight(tc, 30, Unit.PX, 50, Unit.PX);
		mainPanel.setWidgetLeftRight(tc, 50, Unit.PX, 50, Unit.PX);
		
		dock.add(mainPanel);
	}

	private void initLeft(DockLayoutPanel dock) {
		DockLayoutPanel left = new DockLayoutPanel(Unit.PX);
		left.setStyleName("LeftPanel");

		HTML src = HTML.wrap(Document.get().getElementById("src"));
		src.setStyleName("src");
		left.addSouth(src, 30);

		HTML like = HTML.wrap(Document.get().getElementById("fb"));
		like.setStyleName("like");
		left.addSouth(like, 30);
		
		HTML tweet = HTML.wrap(Document.get().getElementById("twitter"));
		tweet.setStyleName("tweetButton");
		left.addSouth(tweet, 30);
		
		Info info = new Info(this);
		left.add(info);
		
		dock.addWest(left, 200);
	}

	private double[] getResolutions() {
		double[] resolutions = new double[7];
		for (int i=0, r=1 ; i < 7 ; i++, r++) {
			resolutions[i] = OpenStreetMapLayer.SUPPORTED_RESOLUTIONS[r];
		}
		return resolutions;
	}

	private HTML initCopyrightText() {
		HTML res = new HTML("(c) <a href=\"http://www.openstreetmap.org/\">OpenStreetMap</a> (and) contributors, <a href=\"http://creativecommons.org/licenses/by-sa/2.0/\">CC-BY-SA</a>");
		res.setStyleName("copy");
		return res;
	}

	public void goToPatientZero() {
		map.zoomTo(map.getResolutions().length - 1, new LonLat(11865472.774764482, 3458622.6558476537));
		solanum.setTimestep(0);
		tc.moveKnobToStep(0);
	}
	
}
