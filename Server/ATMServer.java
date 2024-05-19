import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ATMServer {

    private static final String LOG_FILE = "server.log";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library?useSSL=false&serverTimezone=UTC";
    static final String USER = "root";
    static final String PASS = "Ljj200402260522";
    static String userId;
    static int flag = 0;

    public static void main(String[] args) {
        // 初始化用户账户

        Connection conn = null;
        Statement stmt = null;
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            // loadUserAccountsFromDatabase();
            // 执行查询
            System.out.println("实例化Statement对象...");
            log("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT userId FROM users";
            ResultSet rs = stmt.executeQuery(sql);

            // 展开结果集数据库
            while (rs.next()) {
                // 通过字段检索
                String userId = rs.getString("userId");
                // 输出数据
                System.out.println("userId " + userId);
            }
            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // 处理 JDBC 错误
            se.printStackTrace();
            log("SQLException occurred: " + se.getMessage());
        } catch (Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
            log("Exception occurred: " + e.getMessage());
        } finally {
            // 关闭资源
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
                se2.printStackTrace();
                log("SQLException occurred while closing statement: " + se2.getMessage());
            } // 什么都不做
            try {

                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
                log("SQLException occurred while closing connection: " + se.getMessage());
            }
        }
        // 启动服务器，接受客户端请求
        try (ServerSocket serverSocket = new ServerSocket(2525)) {
            log("Server started. Listening on port 2525...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                log("Client connected: " + clientSocket.getInetAddress());
                new Thread(() -> processClientRequests(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            log("IOException occurred: " + e.getMessage());
        }
    }

    /*
     * private static void loadUserAccountsFromDatabase() {
     * try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
     * Statement statement = connection.createStatement();
     * ResultSet rs = statement.executeQuery("SELECT * FROM users")) {
     * while (rs.next()) {
     * String userId = resultSet.getString("userId");
     * String password = resultSet.getString("password");
     * int balance = resultSet.getInt("balance");
     * userAccounts.put(userId, new Account(password, balance));
     * Set<Entry<String, Account>> en = userAccounts.entrySet();
     *
     * for (Entry<String, Account> entry : en) {
     * System.out.println(entry.getKey());
     * System.out.println(entry.getValue());
     * }
     * }
     * } catch (SQLException e) {
     * e.printStackTrace();
     * }
     * }
     */

    // 处理客户端请求
    // handleHELORequest, handlePASSRequest, handleWithdrawRequest,
    // handleBalanceRequest, handleDebugRequest 方法
    private static void processClientRequests(Socket clientSocket) {
        try (
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true)) {
            String request;
            while ((request = input.readLine()) != null) {
                log("Request received from client: " + request);
                if (request.startsWith("HELO")) {
                    handleHELORequest(request, output);
                } else if (request.startsWith("PASS")) {
                    handlePASSRequest(request, output);
                } else if (request.startsWith("WDRA") && flag == 1) {
                    handleWithdrawRequest(request, output);
                } else if (request.equals("BALA") && flag == 1) {
                    getBalance(userId, output);
                } else if (request.equals("BYE")) {
                    output.println("BYE");
                    break;
                } else {
                    output.println("401 ERROR!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            log("IOException occurred: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                log("IOException occurred while closing client socket: " + e.getMessage());
            }
        }
    }

    private static void handleHELORequest(String request, PrintWriter output) {
        userId = request.substring(5);
        if (verifyLogin(userId)) {
            output.println("500 AUTH REQUIRE");
            log("HELO request processed successfully.");
        } else {
            output.println("401 ERROR!");
            log("Error processing HELO request.");
        }
    }

    private static void handlePASSRequest(String request, PrintWriter output) {
        String password = request.substring(5);
        if (verifyPassword(userId, password)) {
            output.println("525 OK!");
            flag = 1;
            log("PASS request processed successfully.");
        } else {
            output.println("401 ERROR!");
            log("Error processing PASS request.");
        }
    }

    private static void handleWithdrawRequest(String request, PrintWriter output) {
        String parts = request.substring(5);
        double amount = Double.parseDouble(parts);
        if (withdraw(userId, amount)) {
            try (Connection con = getConnection();
                    PreparedStatement statement = con
                            .prepareStatement("INSERT INTO record(userid,amountChange,time) VALUES(?,?,?)")) {
                statement.setString(1, userId);
                statement.setString(2, "-" + parts);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.now();
                String formattedDateTime = dateTime.format(formatter);
                statement.setString(3, formattedDateTime);
                statement.executeUpdate();
                output.println("525 OK!");
                log("WDRA request processed successfully.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                log("SQLException occurred while processing WDRA request: " + ex.getMessage());
            }
        } else {
            output.println("401 ERROR!");
            log("Error processing WDRA request. Insufficient funds.");
        }
    }

    private static void getBalance(String userId, PrintWriter output) {
        double balance = 0.0; // 初始化余额为0.0
        try (Connection con = getConnection();
                PreparedStatement statement = con.prepareStatement("SELECT balance FROM users WHERE userId = ?")) {
            statement.setString(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    balance = rs.getDouble("balance"); // 将查询到的余额赋值给balance变量
                    output.println("AMNT:" + balance);
                    log("BALA request processed successfully.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("SQLException occurred while processing BALA request: " + ex.getMessage());
        }
    }

    private static boolean verifyLogin(String userId) {
        try (Connection con = getConnection();
                PreparedStatement statement = con.prepareStatement("SELECT * FROM users WHERE userId = ? ")) {
            statement.setString(1, userId);
            System.out.println(userId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next(); // 返回true表示用户名和密码匹配
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("SQLException occurred while processing HELO request: " + ex.getMessage());
            return false;
        }
    }

    private static boolean verifyPassword(String userId, String password) {
        try (Connection connection = getConnection();
                PreparedStatement statement = connection
                        .prepareStatement("SELECT * FROM users WHERE userId = ? AND password = ?")) {
            statement.setString(1, userId);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    log("Password verified for userId: " + userId);
                    return true; // 返回true表示用户名和密码匹配
                } else {
                    log("Password verification failed for userId: " + userId);
                    return false;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            log("SQLException occurred while processing PASS request: " + ex.getMessage());
            return false;
        }
    }

    private static boolean withdraw(String userId, double amount) {
        try (Connection con = getConnection();
                PreparedStatement statement = con.prepareStatement("SELECT balance FROM users WHERE userId = ?")) {
            statement.setString(1, userId);
            System.out.println(userId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    double balance = rs.getDouble("balance");
                    if (balance >= amount) {
                        // 更新余额
                        String updateQuery = "UPDATE users SET balance=? WHERE userId=?";
                        try (PreparedStatement updateStmt = con.prepareStatement(updateQuery)) {
                            updateStmt.setDouble(1, balance - amount);
                            updateStmt.setString(2, userId);
                            updateStmt.executeUpdate();
                            log("BALA request processed successfully for userId: " + userId + ", amount: " + amount);
                            return true; // 取款成功
                        }
                    } else {
                        log("Insufficient funds for userId: " + userId + ", amount requested: " + amount);
                        return false; // 余额不足
                    }
                } else {
                    log("Account not found for userId: " + userId);
                    return false; // 账户不存在
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            log("SQLException occurred while processing WDRA request: " + ex.getMessage());
            return false;
        }
    }

    private static void log(String message) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String logMessage = timestamp + " : " + message;
        System.out.println(logMessage); // 在控制台打印日志信息

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(LOG_FILE, true)))) {
            writer.println(logMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}