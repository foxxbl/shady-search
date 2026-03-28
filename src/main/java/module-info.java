module shady.search {
  requires jakarta.xml.bind;
  requires java.xml;
  requires javafx.controls;
  requires javafx.fxml;
  requires MaterialFX;
  requires net.rgielen.fxweaver.core;
  requires org.slf4j;
  requires spring.boot;
  requires spring.boot.autoconfigure;
  requires spring.context;
  requires static lombok;
  requires spring.core;
  requires tools.jackson.databind;

  opens eu.foxxbl.x4.gameparser.shady.model.json to tools.jackson.databind;

}
