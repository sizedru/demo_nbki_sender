package rd.rusdengi;

import java.sql.*;

public class Db {
    public static final int ERR_TABLE_NOT_EXIST = 1146;
    public static final int ERR_FIELD_NOT_EXIST = 1054;

    // JDBC variables for opening and managing connection
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;
    private static int res;

    public void OpenNbki() {
        try {
            // opening database connection to MySQL server
            con = DriverManager.getConnection(urlNbki, user, password);

            con.setAutoCommit(false);
            // getting Statement object to execute query
            stmt = con.createStatement();

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    public void OpenRd() {
        try {
            // opening database connection to MySQL server
            con = DriverManager.getConnection(urlRd, user, password);

            // getting Statement object to execute query
            stmt = con.createStatement();

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }


    public void Close() {
        //close connection ,stmt and resultset here
        try {
            con.commit();
            CloseQuery();
        } catch (SQLException se) { /*can't do anything */ }
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException se) { /*can't do anything */ }
        try {
            if (con != null) con.close();
        } catch (SQLException se) { /*can't do anything */ }
    }

    public void Query(String query, boolean update, boolean batch) {
        try {
            CloseQuery();
            // executing SELECT query
            if (stmt != null) {
                if (update) {
                    if (batch) {
                        //con.setAutoCommit(false);
                        var queries = query.split(";");
                        for (String q : queries) {
                            if (!q.replaceAll("[\n\r]", "").trim().equals("")) {
                                stmt.addBatch(q + ";");
                            }
                        }
                        stmt.executeBatch();
                        con.commit();
                        //con.setAutoCommit(true);
                    } else {
                        res = stmt.executeUpdate(query);
                    }
                } else {
                    rs = stmt.executeQuery(query);
                }
            }
        } catch (SQLException sqlEx) {
            rs = null;
            res = sqlEx.getErrorCode();
            if (res != ERR_TABLE_NOT_EXIST && res != ERR_FIELD_NOT_EXIST) {
                System.out.println(res);
                sqlEx.printStackTrace();
            }
            // try {
            //     con.setAutoCommit(true);
            // } catch (SQLException sqlExEx) {
            //     sqlExEx.printStackTrace();
            // }
        }
    }

    public ResultSet GetResultSet() {
        return rs;
    }

    public int GetResult() {
        return res;
    }

    public void CloseQuery() throws SQLException {
        if (rs != null) rs.close();
        rs = null;
    }

    public void PrnCnt() {
        String query; //"SHOW TABLES FROM `nbki` LIKE 'tr'";
        query = "SHOW COLUMNS FROM `tr`";
        try {

            OpenNbki();

            Query(query, false, false);

            if (rs != null) {
                while (rs.next()) {
                    String count = rs.getString(1);
                    System.out.println("Total number of orders in the table : " + count);
                }
            }
            Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
