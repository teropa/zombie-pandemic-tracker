package zpt.client;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandlerAdapter;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;

public class TimeControl extends Composite {

	private final FlowPanel container = new FlowPanel();
	private final AbsolutePanelWithClicks sliderTrack = new AbsolutePanelWithClicks();
	private final FocusPanel sliderKnob = new FocusPanel();
	
	private final HTML info = new HTML();
	
	private final EpidemicLayer layer;
	
	private final PickupDragController dragController = new TimeControlDragController(sliderTrack, this);
	
	
	public TimeControl(EpidemicLayer layer) {
		this.layer = layer;
		initWidget(container);
		setStyleName("TimeControl");
		initControl();
		info.setStyleName("TimeControlInfo");
		container.add(info);
	}

	private void initControl() {
		dragController.setConstrainWidgetToBoundaryPanel(true);
		dragController.setBehaviorDragProxy(false);
		
		sliderTrack.setStyleName("SliderTrack");
		container.add(sliderTrack);

		sliderKnob.setStyleName("SliderKnob");
		sliderKnob.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				event.stopPropagation();
			}
		});
		dragController.makeDraggable(sliderKnob);

		DeferredCommand.addCommand(new Command() {
			public void execute() {
				int stepWidth = sliderTrack.getOffsetWidth() / Client.NUM_STEPS;
				sliderTrack.setWidth((stepWidth * Client.NUM_STEPS)+"px");
				for (int i=0 ; i<Client.NUM_STEPS ; i++) {
					FlowPanel sliderTrackBg = new FlowPanel();
					sliderTrackBg.setWidth((stepWidth-2)+"px");
					sliderTrackBg.setStyleName("SliderTrackBg");
					sliderTrackBg.setTitle("Show Day Z"+(i > 0 ? "+"+i : ""));
					sliderTrack.add(sliderTrackBg, stepWidth * i + 1, 12);
				}				
				sliderTrack.add(sliderKnob, 0, 0);
				
				moveKnobToStep(layer.getTimeStep());
			}
		});
		
		sliderTrack.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int x = event.getNativeEvent().getClientX();
				moveKnob(x - sliderTrack.getAbsoluteLeft());
			}
		});
				
		dragController.addDragHandler(new DragHandlerAdapter() {
			public void onDragEnd(DragEndEvent event) {
				double left = sliderTrack.getWidgetLeft(sliderKnob);
				moveKnob(left);
			}
		});
	}
	
	private void moveKnob(double left) {
		double width = sliderTrack.getOffsetWidth();
		int step = (int)Math.floor((left / width) * Client.NUM_STEPS);
		sliderTrack.setWidgetPosition(sliderKnob, getSnapPositionForStep(step), sliderTrack.getWidgetTop(sliderKnob));
		layer.setTimestep(step);
		moveKnobToStep(step);		
	}

	void moveKnobToStep(int step) {
		sliderTrack.setWidgetPosition(sliderKnob, getSnapPositionForStep(step), sliderTrack.getWidgetTop(sliderKnob));
		updateInfo(step);
	}

	public int getSnapPositionForX(int at) {
		double width = sliderTrack.getOffsetWidth();
		int step = (int)Math.floor((at / width) * Client.NUM_STEPS);
		return getSnapPositionForStep(step);
	}
	
	public int getSnapPositionForStep(int step) {
		int stepWidth = sliderTrack.getOffsetWidth() / Client.NUM_STEPS;
		return (int)(step * stepWidth + 0.5 * stepWidth - 0.5 * sliderKnob.getOffsetWidth());
	}

	public void onKnobDragged(int at) {
		double width = sliderTrack.getOffsetWidth();
		int step = (int)Math.floor((at / width) * Client.NUM_STEPS);
		updateInfo(step);	
	}
	
	
	public void onKnobLingered(int at) {
		double width = sliderTrack.getOffsetWidth();
		int step = (int)Math.floor((at / width) * Client.NUM_STEPS);
		layer.setTimestep(step);		
	}

	private void updateInfo(int step) {
		info.setHTML("Day <span class=\"zed\">Z</span>"+(step > 0 ? "+"+step : ""));
	}

	private final class AbsolutePanelWithClicks extends AbsolutePanel implements HasClickHandlers {
		
		@Override
		public HandlerRegistration addClickHandler(ClickHandler handler) {
			return addDomHandler(handler, ClickEvent.getType());
		}
	}





}
