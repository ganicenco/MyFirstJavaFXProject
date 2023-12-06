package com.example.myfirstjavafxproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TaskManagerApp extends Application {
    private TaskManager taskManager;
    private TableView<Task> taskTable;
    private Label messageLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(TaskManagerApp.class.getResource("hello-view.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Your JavaFX App");
        stage.show();

        taskManager = new TaskManager();

        taskTable = new TableView<>();
        taskTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        TableColumn<Task, String> titleColum = new TableColumn<>("Title");
        titleColum.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Task, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Task, String> priorityColumn = new TableColumn<>("Priority");
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));

        TableColumn<Task, LocalDate> dueDateColum = new TableColumn<>("Due Date");
        dueDateColum.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        taskTable.getColumns().addAll(titleColum, descriptionColumn, priorityColumn, dueDateColum);
        messageLabel = new Label("No task available.");

        VBox layout = new VBox(10);
        layout.getChildren().addAll(taskTable, messageLabel);

        Button addButton = new Button("Add a New Task");
        addButton.setOnAction(e -> addTask(taskManager));

        Button editButton = new Button("Edit Task");
        editButton.setOnAction(e -> editTask());

        Button deleteButton = new Button("Delete Task");
        deleteButton.setOnAction(e -> deleteTask());

        layout.getChildren().addAll(addButton, editButton, deleteButton);

        Scene scene1 = new Scene(layout, 600, 400);
        scene.getStylesheets().add(TaskManagerApp.class.getResource("styles.css").toExternalForm());
        stage.setScene(scene1);
        stage.setTitle("Task Manager");
        stage.show();


        taskManager.addTask(new Task("coffee", "drink coffee first in the morning", "high", LocalDate.now()));
        taskManager.addTask(new Task("kinder", "take the kid to school", "high", LocalDate.now()));
        loadTaskIntoTable();
    }

    private void loadTaskIntoTable() {
        List<Task> tasks = taskManager.getTasks();
        taskTable.getItems().setAll(tasks);

        if (tasks.isEmpty()) {
            messageLabel.setText("No tasks available.");
        } else {
            messageLabel.setText("");
        }
    }

    public void addTask(TaskManager taskManager) {

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        TextField priorityField = new TextField();
        priorityField.setPromptText("Priority");

        TextField dueDateField = new TextField();
        dueDateField.setPromptText("Due Date(YYYY-MM-DD)");

        Button addButton = new Button("Add Task");
        addButton.setOnAction((e -> {
            String title = titleField.getText();
            String description = descriptionField.getText();
            String priority = priorityField.getText();
            String dueDateString = dueDateField.getText();

            if (title.isEmpty() || dueDateString.isEmpty()) {
                showAlert("Title and Due Date are required");
            }

            try {
                LocalDate dueDate = LocalDate.parse(dueDateString);

                Task newTask = new Task(title, description, priority, dueDate);
                taskManager.addTask(newTask);
            } catch (DateTimeException ex) {
                showAlert("Invalid date format. Please use YYYY-MM-DD.");
            }
            loadTaskIntoTable();
        }));


        VBox layout = new VBox(10);
        layout.getChildren().addAll(titleField, descriptionField, priorityField, dueDateField, addButton);
        showInputDialog("Add Task", layout);
    }

    private void editTask() {
       try {
           Task selectedTask = taskTable.getSelectionModel().getSelectedItem();

           if (selectedTask == null) {
               showAlert("Please select a task to edit!");
           }
           Dialog<Task> editDialog = new Dialog<>();
           editDialog.setTitle("Edit Task");

           TextField titleField = new TextField(selectedTask.getTitle());
           TextField descriptionField = new TextField(selectedTask.getDescription());
           TextField priorityField = new TextField(selectedTask.getPriority());
           DatePicker dueDatePiker = new DatePicker(selectedTask.getDueDate());

           VBox editLayout = new VBox(10);
           editLayout.getChildren().addAll(
                   new Label("Title: "), titleField,
                   new Label("Description: "), descriptionField,
                   new Label("Priority: "), priorityField,
                   new Label("Due Date: "), dueDatePiker
           );

           editDialog.getDialogPane().setContent(editLayout);

           ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
           editDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

           editDialog.setResultConverter(buttonType -> {
               if (buttonType == saveButtonType) {
                   try {
                       selectedTask.setTitle(titleField.getText());
                       selectedTask.setDescription(descriptionField.getText());
                       selectedTask.setPriority(priorityField.getText());
                       selectedTask.setDueDate(dueDatePiker.getValue());

                       loadTaskIntoTable();
                   } catch (DateTimeException e) {
                       showAlert("Invalid data format. Please use YYYY-MM-DD");
                   }
               }
               return null;
           });
           editDialog.showAndWait();

       } catch (Exception e){
           System.out.println("All good!");
       }
    }



    public void deleteTask() {
            Task selectedTask = taskTable.getSelectionModel().getSelectedItem();

            if (selectedTask == null) {
                showAlert("Please select a task to delete");
            }

            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Deletion");
            confirmationAlert.setHeaderText(null);
            confirmationAlert.setContentText("Are you sure you want to delete the selected task?");

            Optional<ButtonType> result = confirmationAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                taskManager.deleteTask(selectedTask);

                loadTaskIntoTable();
            }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

    }

    private void showInputDialog(String title, Node content) {
        Stage inputDialog = new Stage();
        inputDialog.setTitle(title);

        Scene scene = new Scene(new VBox(10, content), 300, 200);
        inputDialog.setScene(scene);

        inputDialog.show();
    }

}

