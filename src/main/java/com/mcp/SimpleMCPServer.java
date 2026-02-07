package com.mcp;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.*;

public class SimpleMCPServer {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String line = reader.readLine();
            if (line == null) break;

            handleRequest(line);
        }
    }

    private static void handleRequest(String jsonRequest) {
        try {
            Map<String, Object> request = mapper.readValue(jsonRequest, Map.class);
            String method = (String) request.get("method");

            Map<String, Object> response = new HashMap<>();
            response.put("jsonrpc", "2.0");
            response.put("id", request.get("id"));

            switch (method) {
                case "initialize":
                    response.put("result", handleInitialize());
                    break;
                case "tools/list":
                    response.put("result", handleToolsList());
                    break;
                case "tools/call":
                    response.put("result", handleToolCall(request));
                    break;
                case "resources/list":
                    response.put("result", handleResourcesList());
                    break;
                default:
                    response.put("error", Map.of("code", -32601, "message", "Method not found"));
            }

            System.out.println(mapper.writeValueAsString(response));
            System.out.flush();

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private static Map<String, Object> handleInitialize() {
        return Map.of(
                "protocolVersion", "2024-11-05",
                "serverInfo", Map.of(
                        "name", "java-mcp-server",
                        "version", "1.0.0"
                ),
                "capabilities", Map.of(
                        "tools", Map.of(),
                        "resources", Map.of()
                )
        );
    }

    private static Map<String, Object> handleToolsList() {
        List<Map<String, Object>> tools = new ArrayList<>();

        // Tool 1: Get current time
        tools.add(Map.of(
                "name", "get_current_time",
                "description", "Returns the current server time",
                "inputSchema", Map.of(
                        "type", "object",
                        "properties", Map.of()
                )
        ));

        // Tool 2: Calculate sum
        tools.add(Map.of(
                "name", "calculate_sum",
                "description", "Calculates the sum of two numbers",
                "inputSchema", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "a", Map.of("type", "number", "description", "First number"),
                                "b", Map.of("type", "number", "description", "Second number")
                        ),
                        "required", List.of("a", "b")
                )
        ));

        return Map.of("tools", tools);
    }

    private static Map<String, Object> handleToolCall(Map<String, Object> request) {
        Map<String, Object> params = (Map<String, Object>) request.get("params");
        String toolName = (String) params.get("name");
        Map<String, Object> arguments = (Map<String, Object>) params.get("arguments");

        String result;

        switch (toolName) {
            case "get_current_time":
                result = new Date().toString();
                break;
            case "calculate_sum":
                double a = ((Number) arguments.get("a")).doubleValue();
                double b = ((Number) arguments.get("b")).doubleValue();
                result = "The sum is: " + (a + b);
                break;
            default:
                result = "Unknown tool";
        }

        return Map.of(
                "content", List.of(
                        Map.of("type", "text", "text", result)
                )
        );
    }

    private static Map<String, Object> handleResourcesList() {
        List<Map<String, Object>> resources = new ArrayList<>();

        resources.add(Map.of(
                "uri", "file:///config.json",
                "name", "Server Configuration",
                "description", "Current server configuration",
                "mimeType", "application/json"
        ));

        return Map.of("resources", resources);
    }
}
