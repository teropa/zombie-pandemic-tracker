package zpt.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class Info extends Composite {

	private final HTML content;
	
	public Info(final Client client) {
		Anchor patientZero = Anchor.wrap(Document.get().getElementById("patient_zero_link"));
		patientZero.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent evt) {
				client.goToPatientZero();
			}
		});

		content = HTML.wrap(Document.get().getElementById("info"));
		initWidget(content);
	}

}
