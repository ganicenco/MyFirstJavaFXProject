module com.example.myfirstjavafxproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.myfirstjavafxproject to javafx.fxml;
    exports com.example.myfirstjavafxproject;
}