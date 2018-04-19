package it.polito.tdp.meteo;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class MeteoController {
	
	Model model;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ChoiceBox<Integer> boxMese;

	@FXML
	private Button btnCalcola;

	@FXML
	private Button btnUmidita;

	@FXML
	private TextArea txtResult;

	@FXML
	void doCalcolaSequenza(ActionEvent event) {
		if(boxMese.getValue()!=null) {
			txtResult.setText(model.trovaSequenza(boxMese.getValue()));
		}
		else {
			this.txtResult.setText("Attenzione: mese non inserito!\n");
		}
	}

	@FXML
	void doCalcolaUmidita(ActionEvent event) {
		if(boxMese.getValue()!=null) {
			txtResult.setText(model.getUmiditaMedia(boxMese.getValue()));
		}
		else {
			this.txtResult.setText("Attenzione: mese non inserito!\n");
		}
	}

	@FXML
	void initialize() {
		assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert btnCalcola != null : "fx:id=\"btnCalcola\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert btnUmidita != null : "fx:id=\"btnUmidita\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Meteo.fxml'.";
	}

	public void setModel(Model model) {
		this.model = model;
		
		this.boxMese.getItems().add(null);
		for(int i=1; i<=12; i++) {
			boxMese.getItems().add(i);
		}
		
	}

}
