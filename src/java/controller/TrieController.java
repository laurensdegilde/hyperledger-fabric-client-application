package controller;

import generator.Generator;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.bouncycastle.util.encoders.Hex;
import trie.DatabaseExposure;
import trie.Trie;
import util.Util;

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
    public TextField tfKey;

    @FXML
    public TextField tfValue;

    private Generator generator;

    private Trie trie;

    public TrieController() throws IOException, InvalidFormatException, NoSuchAlgorithmException {
        this.generator = new Generator();
        this.trie = new Trie();
    }

    @FXML
    public void getValue() throws NoSuchAlgorithmException {
        tfValue.setText(new String(this.trie.get(tfKey.getText())));
    }
    @FXML
    public void insertValue() throws NoSuchAlgorithmException, IOException {
        this.trie.insert(tfKey.getText(), tfValue.getText());
        this.trie.dumpTrie();
    }
    @FXML
    public void dumpTrie() throws NoSuchAlgorithmException {
        System.out.println(trie.getTrieDump());
    }
    @FXML
    public void generateTrie() throws IOException, NoSuchAlgorithmException, InvalidFormatException {
        this.generator = new Generator();
        for (int i = 1; i <= Integer.valueOf(tfAmountOfUsers.getText()); i++){
            for (String [] kv: this.generator.generateRecordForUser(i, Integer.valueOf(tfAmountOfAttributes.getText()))){
                this.trie.insert(kv[0], kv[1]);
            }
        }
        this.trie.dumpTrie();
        this.printGeneratedRecordData();
    }

    public void printGeneratedRecordData(){
        for (Map.Entry<String, String> entry : this.generator.getGeneratedDataRepresentation().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
