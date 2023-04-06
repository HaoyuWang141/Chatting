module learn.javafx {
    requires javafx.controls;
    requires javafx.fxml;

    opens learn.javafx to javafx.fxml;
    exports learn.javafx;
}