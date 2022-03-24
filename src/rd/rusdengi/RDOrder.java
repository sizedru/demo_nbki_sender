package rd.rusdengi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class RDOrder {
    public boolean Ok;
    public String ID;
    public String Number;
    public Date OpenDate;
    public Date CloseDate;
    public Boolean CloseFact;
    public Boolean BankrotFact;
    public Date BankrotDate;
    public Boolean CessionFact;
    public Date CessionDate;
    public String UUID;
    public float Sum;
    public Boolean DisableSend;
    public JSONObject Json;
    public int FineDays;
    public Date LastPayDate;
    public float LastPaySum;
    public Date LastReportDatePlusDay;
    public float AllPaySum;
    public Date CurrentDate;
    public float CurrNeedPay;
    public float AllNeedPay;
    public String Psk;
    public String PskMoney;
    // USER
    public String FirstName;
    public String SecondName;
    public String Surname;
    public Date Birthday;
    public String BirthPlace;

    public Boolean PresentOldPasportInBKI;

    public String PasportSer;
    public String PasportNum;
    public Date PasportDate;
    public String PasportIssued;
    public String ApartmentResidence;
    public String BlockSymbolResidence;
    public String HouseNumberResidence;
    public String StreetResidence;
    public String CityResidence;
    public String PostIndexResidence;
    public String ApartmentRegistration;
    public String BlockSymbolRegistration;
    public String HouseNumberRegistration;
    public String StreetRegistration;
    public String CityRegistration;
    public String PostIndexRegistration;
    //private int SumInt;

    public Boolean IsFakeP;

    public Boolean LastRecord;

    public Boolean OrderToDate;

    public RDOrder(String id, ResultSet resultSet, Boolean date) throws ParseException {
        ResultSet rs = null;
        Db db = null;
        if (resultSet == null) {
            if (id.isEmpty()) {
                ID = null;
                Number = "";
                OpenDate = null;
                CloseDate = null;
                return;
            }
            db = new Db();
            db.OpenRd();

            var SQL = "select .......";
            if (date) {
                //SQL += "DATE_FORMAT(NOW(), '%Y-%m-%d')";
                SQL += "'" + date + "'";
            }

            SQL += ".... where o.id=" + id;

            db.Query(SQL, false, false);
            rs = db.GetResultSet();
        } else {
            rs = resultSet;
        }

        if (rs != null) {
            try {
                if (rs.next()) {
                    ID = rs.getString("id");
                    Number = rs.getString("order_num");

                    // ......
                    // ......
                    // ......
                    // ......
                    

                    Ok = true;


                    try {
                        URL url = null;
                        String urlStr = "";
                        try {
                            urlStr = "http://............." + Number;
                            if (date) {
                                urlStr += "&date=" + CurrentDate.toString();
                            }
                            System.out.println(urlStr);
                            url = new URL(urlStr);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.connect();
                        if (conn.getResponseCode() == 200) {
                            Scanner scan = new Scanner(url.openStream());
                            while (scan.hasNext()) {
                                String temp = scan.nextLine();
                                //System.out.println(temp);
                                //parse json here
                                Json = new JSONObject(temp);
                            }
                        }
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    ID = null;
                    Number = "";
                    OpenDate = null;
                    CloseDate = null;
                    return;
                }
            } catch (SQLException e) {
                Number = "";
                OpenDate = null;
                CloseDate = null;
                e.printStackTrace();
            }
        } else {
            ID = null;
            Number = "";
            OpenDate = null;
            CloseDate = null;
            return;
        }

        if (db != null) {
            db.Close();
        }

        if (Json != null) {
            FineDays = Json.getJSONObject("OrderPaymentSchedule").getInt("FineDaysAbsolute");

            JSONArray ja = Json.getJSONArray("OrderCheckpoints");
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                if (jo.getBoolean("IsPay")) {
                    LastPayDate = GetSQLDate(jo.getString("Date").substring(0, 10));
                    LastPaySum = jo.getFloat("PaySum");
                    AllPaySum += jo.getFloat("PaySum");
                }

                JSONObject cp = jo.getJSONObject("CalcProfit");
                CurrNeedPay = cp.getFloat("Base") + cp.getFloat("Perc");
            }

            ja = Json.getJSONObject("OrderPaymentSchedule").getJSONArray("Schedule");
            for (int i = 0; i < ja.length(); i++) {
                JSONObject cp = ja.getJSONObject(i).getJSONObject("BalanceBefore");
                AllNeedPay = cp.getFloat("Base") + cp.getFloat("Perc");
                break;
            }

        } else {
            System.out.println(ID);
            System.out.println("Null JSON");
            Ok = false;
        }

    }


    public static String GetTUTDFDate(Date date) {
        if (date == null) {
            return "19000102";
        } else {
            return date.toString().replace("-", "");
        }
    }

    public Date GetSQLDate(String dd) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date parsed = null;
        try {
            parsed = format.parse(dd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        java.sql.Date sql = new java.sql.Date(parsed.getTime());

        return sql;
    }

    public Date ParseDate(ResultSet rs, String s) {
        Date d = null;
        String cd = null;
        try {
            cd = rs.getString(s);
            if (cd != null) {
                if (!cd.equals("0000-00-00 00:00:00")) {
                    d = rs.getDate(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return d;
    }

    public Date sqlDatePlusDays(Date date, int days) {
        return Date.valueOf(date.toLocalDate().plusDays(days));
    }

}


