package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PrimaryController {

    @FXML private TableView<Student> tableView;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> emailColumn;
    @FXML private TableColumn<Student, Integer> ageColumn;

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField ageField;

    @FXML private Button insertButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button viewButton;

    private ObservableList<Student> students = FXCollections.observableArrayList();

    private Student selectedStudent;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        emailColumn.setCellValueFactory(data -> data.getValue().emailProperty());
        ageColumn.setCellValueFactory(data -> data.getValue().ageProperty().asObject());

        tableView.setItems(students);

        tableView.setOnMouseClicked(this::handleRowClick);

        viewButton.setOnAction(e -> loadData());
        insertButton.setOnAction(e -> insertData());
        updateButton.setOnAction(e -> updateData());
        deleteButton.setOnAction(e -> deleteData());
    }

    private void handleRowClick(MouseEvent event) {
        selectedStudent = tableView.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            nameField.setText(selectedStudent.getName());
            emailField.setText(selectedStudent.getEmail());
            ageField.setText(String.valueOf(selectedStudent.getAge()));
        }
    }

    private void loadData() {
        students.clear();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM student");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                students.add(new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("age")
                ));
            }
        } catch (Exception e) {
            showError("Load Error", e.getMessage());
        }
    }

    private void insertData() {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO student (name, email, age) VALUES (?, ?, ?)")) {
            stmt.setString(1, nameField.getText());
            stmt.setString(2, emailField.getText());
            stmt.setInt(3, Integer.parseInt(ageField.getText()));
            stmt.executeUpdate();
            clearFields();
            loadData();
        } catch (Exception e) {
            showError("Insert Error", e.getMessage());
        }
    }

    private void updateData() {
        if (selectedStudent == null) return;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE student SET name = ?, email = ?, age = ? WHERE id = ?")) {
            stmt.setString(1, nameField.getText());
            stmt.setString(2, emailField.getText());
            stmt.setInt(3, Integer.parseInt(ageField.getText()));
            stmt.setInt(4, selectedStudent.getId());
            stmt.executeUpdate();
            clearFields();
            loadData();
        } catch (Exception e) {
            showError("Update Error", e.getMessage());
        }
    }

    private void deleteData() {
        if (selectedStudent == null) return;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM student WHERE id = ?")) {
            stmt.setInt(1, selectedStudent.getId());
            stmt.executeUpdate();
            clearFields();
            loadData();
        } catch (Exception e) {
            showError("Delete Error", e.getMessage());
        }
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        ageField.clear();
        selectedStudent = null;
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}