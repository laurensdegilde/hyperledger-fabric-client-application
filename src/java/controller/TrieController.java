package controller;

import generator.Generator;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import trie.DatabaseExposure;
import trie.Trie;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class TrieController {

    @FXML
    public TextField tfAmountOfUsers;

    @FXML
    public TextField tfAmountOfAttributes;

    @FXML
    public TextField tfGetValue;

    private Generator generator;
    private DatabaseExposure databaseExposure;

    private Trie trie;

    public TrieController() throws IOException, InvalidFormatException {
        this.generator = new Generator();
        trie = new Trie(new byte[]{});
    }

    @FXML
    public void getValue() throws NoSuchAlgorithmException {
        System.out.println(new String(this.trie.get(tfGetValue.getText())));
    }

    @FXML
    public void generateTrie() throws IOException, NoSuchAlgorithmException {
        this.trie.setRoot(new byte[]{});
        for (int i = 1; i <= Integer.valueOf(tfAmountOfUsers.getText()); i++){
            for (String [] kv: this.generator.generateRecordForUser(i, Integer.valueOf(tfAmountOfAttributes.getText()))){
                trie.insert(kv[0], kv[1]);
            }
        }
        System.out.println(trie.getTrieDump());
        BufferedWriter writer = new BufferedWriter(new FileWriter("trie-dump.json"));
        writer.write(trie.getTrieDump());
        writer.close();
        this.printGeneratedRecordData();
    }

    public void printGeneratedRecordData(){
        for (Map.Entry<String, String> entry : this.generator.getGeneratedDataRepresentation().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
