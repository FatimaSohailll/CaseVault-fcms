package com.fcms.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public class SceneManager {

    private final Stage stage;

    public SceneManager(Stage stage) {
        this.stage = stage;
    }

    /**
     * Simple center switch without context injection.
     */
    public void switchContent(String fxmlPath) {
        switchContent(fxmlPath, null);
    }

    /**
     * Replace the center content of the application's BorderPane root with the loaded FXML.
     * If controllerContext is provided, attempt to inject it into the controller via a single-arg setter.
     *
     * @param fxmlPath         path to FXML resource (e.g. "/fxml/policeOfficer/editCase.fxml")
     * @param controllerContext optional context object to inject into the controller (e.g. a Case)
     */
    public void switchContent(String fxmlPath, Object controllerContext) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();

            // Inject context into controller if possible
            Object controller = loader.getController();
            if (controller != null && controllerContext != null) {
                injectContext(controller, controllerContext);
            }

            // Replace only the center of the main BorderPane
            BorderPane root = (BorderPane) stage.getScene().getRoot();
            root.setCenter(content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open a modal window from any static context and attempt to inject the provided context into the modal's controller.
     *
     * @param fxmlPath path to FXML resource
     * @param title    window title
     * @param context  optional controller context (e.g. Case, Evidence, Participant)
     */
    public static void openModal(String fxmlPath, String title, Object context) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller != null && context != null) {
                injectContextStatic(controller, context);
            }

            Stage modal = new Stage();
            modal.setTitle(title);
            modal.setScene(new Scene(root));
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* -------------------------
       Context injection helpers
       ------------------------- */

    /**
     * Instance helper: try to find and invoke a single-argument setter on the controller
     * whose parameter type is assignable from the context's class.
     */
    private void injectContext(Object controller, Object context) {
        try {
            Optional<Method> setter = Arrays.stream(controller.getClass().getMethods())
                    .filter(m -> m.getParameterCount() == 1)
                    .filter(m -> {
                        Class<?> param = m.getParameterTypes()[0];
                        return param.isAssignableFrom(context.getClass());
                    })
                    .findFirst();

            setter.ifPresent(m -> {
                try {
                    m.invoke(controller, context);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Static variant used by openModal.
     */
    private static void injectContextStatic(Object controller, Object context) {
        try {
            Optional<Method> setter = Arrays.stream(controller.getClass().getMethods())
                    .filter(m -> m.getParameterCount() == 1)
                    .filter(m -> {
                        Class<?> param = m.getParameterTypes()[0];
                        return param.isAssignableFrom(context.getClass());
                    })
                    .findFirst();

            setter.ifPresent(m -> {
                try {
                    m.invoke(controller, context);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
