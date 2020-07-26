package bam.billing.gst.alert;

import bam.billing.gst.Main;
import bam.billing.gst.controller.GSTBillController;
import bam.billing.gst.controller.GenericBillController;
import bam.billing.gst.controller.IGSTBill;
import bam.billing.gst.controller.Settings;
import bam.billing.gst.model.Bill;
import bam.billing.gst.utils.Preferences;
import bam.billing.gst.utils.StringUtil;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static bam.billing.gst.utils.ResourceConstants.Views;

@SuppressWarnings("Duplicates")
public class AlertMaker {

    private static final Logger l = Logger.getLogger(AlertMaker.class.getName());

    private AlertMaker() {

    }

    public static void showErrorMessage(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Occurred");
        alert.setHeaderText("Error Occurred");
        alert.setContentText(ex.getLocalizedMessage());

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);
        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

    //alert Dialog Box

    public static boolean showMCAlert(String header, String body, Main main) {

        JFXAlert<ButtonType> alert = new JFXAlert<>(main.getPrimaryStage());

        JFXDialogLayout dialogLayout = new JFXDialogLayout();


        JFXButton okayButton = new JFXButton("Okay");
        okayButton.setDefaultButton(true);
        okayButton.setOnAction(e -> {
            alert.setResult(ButtonType.OK);
            alert.hideWithAnimation();
        });

        JFXButton cancelButton = new JFXButton(StringUtil.CANCEL);
        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(e -> {
            alert.setResult(ButtonType.CANCEL);
            alert.hideWithAnimation();
        });

        dialogLayout.setHeading(new Label(header));
        dialogLayout.setBody(new Label(body));
        dialogLayout.setActions(okayButton, cancelButton);

        alert.setAnimation(JFXAlertAnimation.BOTTOM_ANIMATION);
        alert.initOwner(main.getPrimaryStage());
        alert.setContent(dialogLayout);

        Optional<ButtonType> optional = alert.showAndWait();

        return optional.isPresent() && optional.get().equals(ButtonType.OK);
    }

    public static boolean showBill(Bill bill, Main main, boolean isView, boolean isIBill) {
        try {
            JFXAlert<ButtonType> alert = new JFXAlert<>(main.getPrimaryStage());
            alert.setSize(1080, 720);
            alert.setResizable(true);

            JFXDialogLayout dialogLayout = new JFXDialogLayout();

            if (isView) {
                JFXButton okayButton = new JFXButton("Okay");
                okayButton.setOnAction(e -> {
                    alert.setResult(ButtonType.OK);
                    alert.hideWithAnimation();
                });
                okayButton.setDefaultButton(true);
                dialogLayout.setActions(okayButton);
            } else {
                JFXButton okayButton = new JFXButton("Submit");
                okayButton.setOnAction(e -> {
                    alert.setResult(ButtonType.OK);
                    alert.hideWithAnimation();
                });

                JFXButton cancelButton = new JFXButton("Cancel");
                cancelButton.setOnAction(e -> {
                    alert.setResult(ButtonType.CANCEL);
                    alert.hideWithAnimation();
                });

                okayButton.setDefaultButton(true);

                dialogLayout.setActions(okayButton, cancelButton);
            }

            dialogLayout.setHeading(new Label("Bills"));

            dialogLayout.setBody(getPane(bill, main, isIBill));

            alert.setAnimation(JFXAlertAnimation.BOTTOM_ANIMATION);
            alert.setContent(dialogLayout);
            alert.setResizable(true);

            Optional<ButtonType> optional = alert.showAndWait();

            return optional.isPresent() && optional.get().equals(ButtonType.OK);

        } catch (Exception e) {
            l.log(Level.SEVERE, e.getMessage());
        }
        return false;
    }

    private static VBox getPane(Bill bill, Main mainApp, boolean isIBill) {

        VBox main = new VBox();
        main.setSpacing(20);
        main.setAlignment(Pos.CENTER);

        HBox box = new HBox();
        box.setSpacing(20);
        box.setAlignment(Pos.CENTER);

        JFXButton print1 = new JFXButton("Print 1");
        JFXButton print2 = new JFXButton("Print 2");

        box.getChildren().addAll(print1, print2);


        try {
            VBox vBox;
            if (isIBill) {
                FXMLLoader loader = Views.getFXMLLoaderWithUrl(Views.I_GST_BILL);
                vBox = loader.load();
                IGSTBill billingController = loader.getController();
                billingController.setBill(bill);

            } else {
                FXMLLoader loader = Views.getFXMLLoaderWithUrl(Views.BILL);
                vBox = loader.load();
                GSTBillController billingController = loader.getController();
                billingController.setBill(bill);
            }
            vBox.getTransforms().add(new Scale(1.7, 1.11));

            StackPane pane = new StackPane(vBox);
            GridPane.setVgrow(pane, Priority.ALWAYS);
            GridPane.setHgrow(pane, Priority.ALWAYS);
            pane.setAlignment(Pos.TOP_LEFT);

            ScrollPane billPane = new ScrollPane(pane);
            billPane.setPannable(true);

            print1.setOnAction(e -> print(bill, mainApp.getPrimaryStage(), 1, isIBill));
            print2.setOnAction(e -> {
                print(bill, mainApp.getPrimaryStage(), 1, isIBill);
                print(bill, mainApp.getPrimaryStage(), 2, isIBill);
            });

            main.getChildren().addAll(billPane, box);

        } catch (IOException e) {
            l.log(Level.SEVERE, e.getMessage());
        }

        return main;
    }

    private static void print(Bill bill, Stage owner, int i, boolean isIBill) {
        Printer printer = Printer.getDefaultPrinter();
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        PageLayout pageLayout = printer.createPageLayout(Paper.A4
                , PageOrientation.REVERSE_PORTRAIT
                , 1, 1, 1, 1);

        try {
            Node node;
            GenericBillController billingController;
            FXMLLoader loader;
            if (isIBill) {
                loader = Views.getFXMLLoaderWithUrl(Views.I_GST_BILL);
            } else {
                loader = Views.getFXMLLoaderWithUrl(Views.BILL);
            }

            loader.load();
            billingController = loader.getController();
            billingController.setBill(bill);
            if (i == 1) billingController.setCopyText("( Original Copy )");
            else billingController.setCopyText("( Duplicate Copy )");
            node = billingController.getRoot();
            if (printerJob != null) {
                printerJob.getJobSettings().setPageLayout(pageLayout);
                if (printerJob.showPrintDialog(owner))
                    if (printerJob.printPage(node))
                        printerJob.endJob();
                    else
                        printerJob.endJob();
                else
                    printerJob.cancelJob();
            }

        } catch (Exception e) {
            l.log(Level.SEVERE, e.getMessage());
        }
    }

    public static boolean showSettings(Main main) {
        try {
            JFXAlert<ButtonType> alert = new JFXAlert<>(main.getPrimaryStage());
            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            dialogLayout.setStyle(String.format("-fx-background-color:%s;", Preferences.getPreferences().getCssThemeName().equals("black")
                    ? "rgb(50,50,50)" : "white"));
            FXMLLoader loader = Views.getFXMLLoaderWithUrl(Views.SETTINGS);
            VBox vBox = loader.load();
            Settings settings = loader.getController();

            JFXButton okayButton = new JFXButton("Okay");
            okayButton.setDefaultButton(true);
            okayButton.setOnAction(e -> {
                alert.setResult(ButtonType.OK);
                alert.hideWithAnimation();
                settings.saveAll();
            });

            JFXButton cancelButton = new JFXButton("Cancel");
            cancelButton.setCancelButton(true);
            cancelButton.setOnAction(e -> {
                alert.setResult(ButtonType.CANCEL);
                alert.hideWithAnimation();
            });

            dialogLayout.setHeading(new Label("Settings"));
            dialogLayout.setBody(vBox);
            dialogLayout.setActions(okayButton, cancelButton);

            alert.setSize(400, 600);
            alert.setAnimation(JFXAlertAnimation.BOTTOM_ANIMATION);
            alert.initOwner(main.getPrimaryStage());
            alert.setContent(dialogLayout);

            Optional<ButtonType> optional = alert.showAndWait();
            main.setNewStyle();
            return optional.isPresent() && optional.get().equals(ButtonType.OK);

        } catch (Exception e) {
            l.log(Level.SEVERE, e.getMessage());

        }

        return false;
    }


}
