package com.sergio.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;

public class HelloController {

    @FXML
    private Label priceField; // Label to display the current Bitcoin price

    private static final Logger logger = Logger.getLogger(HelloController.class.getName()); // Logger for debugging

    /**
     * Called automatically when the controller is initialized.
     * Fetches the Bitcoin price on initialization.
     */
    @FXML
    public void initialize() {
        fetchBitcoinPrice();
    }

    /**
     * Fetches the current Bitcoin price from the CoinDesk API and updates the label.
     * Uses the HttpClient library for making HTTP requests.
     */
    public void fetchBitcoinPrice() {
        try {
            // URL of the CoinDesk API to get the current Bitcoin price in CAD
            String apiUrl = "https://api.coindesk.com/v1/bpi/currentprice/CAD.json";

            // Create an HttpClient to send the request
            HttpClient client = HttpClient.newHttpClient();

            // Build the HTTP GET request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the JSON response to extract the price
            String jsonString = response.body();
            String price = jsonString.split("\"rate_float\":")[1].split("}")[0]; // Extracting price from JSON

            // Update the label with the fetched price in the JavaFX UI thread
            javafx.application.Platform.runLater(() ->
                    priceField.setText(String.format("%.2f", Double.parseDouble(price)))
            );
        } catch (Exception e) {
            // Display an error message if there is an issue fetching the price
            priceField.setText("Error! Refresh!");
        }
    }

    /**
     * Handles the "Refresh" button click.
     * Calls the fetchBitcoinPrice() method to update the Bitcoin price.
     */
    @FXML
    public void onRefreshClick() {
        fetchBitcoinPrice();
    }

    @FXML
    private Button fetch; // Button to fetch and display the Bitcoin price graph

    /**
     * Handles the "Fetch" button click.
     * Opens a new window to display the graph of historical Bitcoin prices.
     */
    @FXML
    public void onFetchClick() {
        try {
            // Load the FXML file for the graph view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fetchGraph.fxml"));
            Parent root = loader.load();

            // Create and display a new stage (window) for the graph
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            // Log the error if the FXML file cannot be loaded
            e.printStackTrace();
        }
    }

}
