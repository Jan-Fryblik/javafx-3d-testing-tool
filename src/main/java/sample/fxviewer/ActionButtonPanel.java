package sample.fxviewer;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ToggleButton;

import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;

/**
 * @author Marian
 * @version 1.0.0 20.8.2018
 */
public class ActionButtonPanel extends ButtonBar {

	public ActionButtonPanel() {
		setButtonMinWidth(25);
	}

	public void addActionButton(Action action) {
		Button button = ActionUtils.createButton(action);
		getButtons().add(button);
	}

	public void addActionToggleButton(Action action) {
		ToggleButton button = ActionUtils.createToggleButton(action);
		getButtons().add(button);
	}

}
