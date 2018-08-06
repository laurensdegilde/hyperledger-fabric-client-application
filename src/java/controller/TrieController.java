package controller;

import generator.Generator;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import trie.TrieParser;

import java.io.IOException;
import java.util.Map;

public class TrieController {

    @FXML
    public TextField tfAmountOfUsers;

    @FXML
    public TextField tfAmountOfAttributes;

    @FXML
    public TextField tfGetValue;

    private Generator generator;

    private TrieParser trie;

    public TrieController() throws IOException, InvalidFormatException {
        this.generator = new Generator();

    }

    @FXML
    public void getValue(){
        System.out.println(new String(this.trie.get(tfGetValue.getText())));
    }

    @FXML
    public void generateTrie(){
        trie = new TrieParser(new byte[]{});
        for (int i = 0; i < Integer.valueOf(tfAmountOfUsers.getText()); i++){
            for (String [] kv: this.generator.generateRecordForUser(i, Integer.valueOf(tfAmountOfAttributes.getText()))){
                trie.insert(kv[0], kv[1]);
            }
        }
        this.printGeneratedRecordData();
    }

    public void printGeneratedRecordData(){
        for (Map.Entry<String, String> entry : this.generator.getGeneratedDataRepresentation().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
