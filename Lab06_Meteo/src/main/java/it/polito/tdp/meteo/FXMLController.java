/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.meteo;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.meteo.model.Citta;
import it.polito.tdp.meteo.model.Model;
import it.polito.tdp.meteo.model.Rilevamento;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class FXMLController {
	private Model model;
	ObservableList<Integer> listaNum= FXCollections.observableArrayList();

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxMese"
    private ChoiceBox<Integer> boxMese; // Value injected by FXMLLoader

    @FXML // fx:id="btnUmidita"
    private Button btnUmidita; // Value injected by FXMLLoader

    @FXML // fx:id="btnCalcola"
    private Button btnCalcola; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCalcolaSequenza(ActionEvent event) {
    	txtResult.clear();
    	
    	Integer mese= boxMese.getValue();
    	if(mese!=null) {
    		List<Citta> best= model.trovaSequenza(mese);
    		
    		txtResult.appendText(String.format("Sequenza ottima per il mese %s\n", Integer.toString(mese)));
    		txtResult.appendText(best + "\n");
    	}

    }

    @FXML
    void doCalcolaUmidita(ActionEvent event) {
    	txtResult.clear();
    	
    	Integer mese= this.boxMese.getValue();
    	if(mese!=null) {
    		txtResult.appendText(String.format("Dati del mese %s\n", Integer.toString(mese)));
    	}
    	for(Citta c: model.getLeCitta()) {
    		Double u= model.getUmiditaMedia(mese, c);
    		txtResult.appendText(String.format("Citta %s: umidita %f\n", c.getNome(), u));
    	}
    //	String medie= this.model.getUmiditaMedia(mese);
    //	txtResult.appendText(medie);
    	
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnUmidita != null : "fx:id=\"btnUmidita\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCalcola != null : "fx:id=\"btnCalcola\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model=model;
    	for(int i=1; i<13; i++) {
    		listaNum.add(i);
    	}
    	this.boxMese.setItems(listaNum);
    	
    }
}

