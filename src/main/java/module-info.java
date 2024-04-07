module com.fnvigg.csv2rdf {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.apache.commons.csv;
    requires javatuples;
    requires java.desktop;
    requires java.sql;


    opens com.fnvigg.csv2rdf to javafx.fxml;
    exports com.fnvigg.csv2rdf;
}