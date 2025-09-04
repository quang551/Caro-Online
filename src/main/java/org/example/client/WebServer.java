package org.example.client;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer;
import org.example.server.CaroEndpoint;

public class WebServer {
    public static void startServer() throws Exception {
        Server server = new Server(8080);

        // Tạo handler cho file tĩnh
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setResourceBase("src/main/java/web");  // chỗ này chứa index.html
        context.addServlet(new ServletHolder(new DefaultServlet()), "/");

        // Cấu hình WebSocket
        JakartaWebSocketServletContainerInitializer.configure(context, (servletContext, wsContainer) -> {
            wsContainer.addEndpoint(CaroEndpoint.class);
        });

        server.setHandler(context);
        server.start();
        System.out.println("Web UI chạy tại http://localhost:8080");
        server.join();
    }

    // ✅ Hàm main để chạy trực tiếp
    public static void main(String[] args) throws Exception {
        startServer();
    }
}
