package com.sergio.demo;

import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class FetchGraph {

    @FXML
    private LineChart<String, Number> btcChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private DatePicker fromDate;
    @FXML
    private DatePicker toDate;
    @FXML
    private Button returnButton;

    // Initialization
    @FXML
    public void initialize() {
        // Clear the chart data to ensure a fresh start each time the program is opened
        btcChart.getData().clear();
    }

    // Method to fetch the API response
    private String fetchApiResponse(String urlString) throws Exception {
        // Create the URL object
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Initialize the InputStreamReader to read the response
        InputStreamReader reader = new InputStreamReader(connection.getInputStream());

        // Create a new instance of StringBuilder to hold the response for each new request
        StringBuilder response = new StringBuilder();

        int character;
        // Read the response from the API while the end of the stream is not reached (-1)
        while ((character = reader.read()) != -1) {
            response.append((char) character);  // Append each character to the response
        }

        // Close the InputStreamReader after usage
        reader.close();

        // Return the API response as a String
        return response.toString();
    }

    // Method to generate the chart when the button is clicked
    @FXML
    private void graphOnClick() {
        // Reset the response and JSON object variables before each new request
        String response = null;
        JSONObject jsonResponse = null;

        // Get the selected dates from the DatePickers
        String from = fromDate.getValue().toString();
        String to = toDate.getValue().toString();

        // Print for debugging
        System.out.println("From: " + from + ", To: " + to);

        try {
            // Build the API URL with the selected date range
            String urlString = "https://api.coindesk.com/v1/bpi/historical/close.json?start=" + from + "&end=" + to;
            System.out.println("Generated URL: " + urlString);

            // Clear the previous chart data before making a new request
            btcChart.getData().clear();
            System.out.println("Chart data cleared.");

            // Call the method to fetch the API response
            response = fetchApiResponse(urlString);
            System.out.println("Raw API Response: " + response);

            // Process the JSON response
            jsonResponse = new JSONObject(response);
            JSONObject bpiData = jsonResponse.getJSONObject("bpi");

            // Print for debugging
            System.out.println("Data received from API (bpi): " + bpiData);

            // List to store date and price entries
            List<Map.Entry<String, Double>> entries = new ArrayList<>();
            Iterator<String> iterator = bpiData.keys();

            // Iterate over the data received
            while (iterator.hasNext()) {
                String date = iterator.next();
                Double price = bpiData.getDouble(date);
                System.out.println("Adding entry - Date: " + date + ", Price: " + price);
                entries.add(new AbstractMap.SimpleEntry<>(date, price));
            }

            // Clear the variable that stored the API response to free memory
            response = null;
            jsonResponse = null;  // Clean up the JSON response object

            // Sort the data by date
            System.out.println("Sorting data by date...");
            entries.sort(Map.Entry.comparingByKey());

            // Print sorted entries for debugging
            System.out.println("Sorted Entries:");
            for (Map.Entry<String, Double> entry : entries) {
                System.out.println("Sorted Entry - Date: " + entry.getKey() + ", Price: " + entry.getValue());
            }

            // Create a new data series for the chart
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Bitcoin Price");

            // Add the sorted data to the chart
            System.out.println("Adding data to chart...");
            for (Map.Entry<String, Double> entry : entries) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            // Add the series to the chart data (important step)
            btcChart.getData().add(series);
            System.out.println("Chart updated with new data.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to return to the previous screen
    @FXML
    private void returnOnClick() {
        Stage stage = (Stage) returnButton.getScene().getWindow();
        stage.close();
    }
}
