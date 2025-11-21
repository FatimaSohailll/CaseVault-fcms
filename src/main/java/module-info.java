module com.fcms {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires org.xerial.sqlitejdbc;
    requires java.sql;
    requires java.naming;

    // Allow FXML loader to access controllers reflectively
    // opens com.fcms.controllers to javafx.fxml;
    opens com.fcms.models to javafx.fxml;

    // Export packages that other modules may use
    exports com.fcms.app;
    exports com.fcms.models;
    exports com.fcms.services;
    exports com.fcms.repositories;
    //exports com.fcms.controllers;
    opens com.fcms.app to javafx.fxml;
    exports com.fcms.controllers.policeOfficer;
    opens com.fcms.controllers.policeOfficer to javafx.fxml;
    exports com.fcms.controllers.forensicExpert;
    opens com.fcms.controllers.forensicExpert to javafx.fxml;
    exports com.fcms.controllers.courtOfficial;
    opens com.fcms.controllers.courtOfficial to javafx.fxml;
    exports com.fcms.controllers.systemAdmin;
    opens com.fcms.controllers.systemAdmin to javafx.fxml;
    exports com.fcms.controllers.components;
    opens com.fcms.controllers.components to javafx.fxml;
    opens com.fcms.services to javafx.fxml;
    opens com.fcms.repositories to javafx.fxml;
    exports com.fcms.database;
    opens com.fcms.database to javafx.fxml;
    exports com.fcms.models.users;
    opens com.fcms.models.users to javafx.fxml;
}