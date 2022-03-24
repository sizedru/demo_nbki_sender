package rd.rusdengi;

import java.io.*;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TUTDFFile {
    public final TUTDFArrayList<TUTDFRecord> Records = new TUTDFArrayList<>();
    private final String Dir;
    private final String File;
    private final String CurdateStr;
    private Db db;

    public TUTDFFile(String dir, String file) {
        Dir = dir;
        CurdateStr = RDOrder.GetTUTDFDate(new Date(System.currentTimeMillis()));
        if (!file.isEmpty()) {
            File = file;
            ReadAll();
        } else {
            File = "DR01FF000001_" + CurdateStr + "_" + java.time.LocalTime.now().toString().substring(0, 8).replace(":", "");
        }
    }

    private boolean Read(String fn, boolean isErrFile) {
        String strLine;
        int recNumber = 0;
        TUTDFRecord rec = null;
        String segId;

        if (isErrFile) {
            segId = "ERROR";
        } else {
            segId = "ID01";
        }

        try {
            // Open the file
            FileInputStream stream = new FileInputStream(fn);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, "windows-1251"));

            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
                String[] str = strLine.split("\t");
                boolean isFirstRecSegment = false;
                if (str[0].equals(segId)) {
                    isFirstRecSegment = true;
                    rec = new TUTDFRecord();
                }

                if (rec != null) {
                    TUTDFSegment segment = rec.GetSegmentByTag(str[0]);
                    if (segment != null) {
                        for (int i = 1; i < str.length; i++) {
                            segment.SetFieldValueByIndex(i, str[i]);
                        }
                    }

                    if (isFirstRecSegment) {
                        if (isErrFile) {
                            if (rec.Errors.IsFill()) {
                                TUTDFRecord mRec = Records.GetByNumber(Integer.parseInt(rec.Errors.RecordNumber.Value));
                                mRec.Errors = rec.Errors;
                            }
                        } else {
                            recNumber++;
                            rec.SetRecordNumber(recNumber);
                            Records.add(rec);
                        }
                    }
                }
            }
            stream.close();
        } catch (IllegalAccessException | IOException e) {
            return false;
        }
        return true;
    }

    private boolean ReadOutFile() {
        if (File.isEmpty()) {
            return false;
        } else {
            return Read(Dir + File /*+ ".txt"*/, false);
        }
    }

    private void ReadErrFile() {
        if (!File.isEmpty()) {
            Read(Dir + File + "_reject", true);
        }
    }

    private void ReadAllRejectFile() {
        if (!File.isEmpty()) {
            File f = new File(Dir + File + "_all_reject");
            if (f.exists() && !f.isDirectory()) {
                for (TUTDFRecord rec : Records) {
                    if (rec.Errors.IsFill()) {
                        var max_i = rec.Errors.GetFieldsCount();
                        for (int i = max_i; i >= 3; i--) {
                            rec.Errors.SetFieldValueByPosition(i + 1, rec.Errors.GetFieldByPosition(i).Value);
                        }
                    } else {
                        rec.Errors.RecordNumber.Value = Integer.toString(rec.RecordNumber);
                    }
                    rec.Errors.SetFieldValueByPosition(3, "@ALLREJECT@");
                }
            }
        }
    }

    private boolean ReadAll() {
        boolean ret = false;
        if (ReadOutFile()) {
            ReadErrFile();
            ReadAllRejectFile();
            ret = true;
        }
        return ret;
    }

    public boolean WriteNew() {
        if (Records.size() == 0) {
            System.out.println("FileName: " + "EMPTY");
            return false;
        }

        try {
            String fn = Dir + File /*+ ".txt"*/;
            File f = new File(fn);
            if (f.exists() && !f.isDirectory()) {
                fn = Dir + File + "_NEW"; //.txt";
            }
            File targetDir = new File(Dir);
            File targetFile = new File(targetDir, File);

            System.out.println("FileName: " + fn);

            FileOutputStream stream = new FileOutputStream(targetFile);
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(stream, "windows-1251"));

            br.write("TUTDF\t8.0R\t20210419\tDR0000000000\t\t" + CurdateStr + "\tPWDDDDD\t\n\n");

            for (TUTDFRecord rec : Records) {
                String s = "";
                s += rec.IDPasport.AsTabString();

                var checkPasport = rec.CheckOldPasport;

                if (checkPasport) {
                    Integer id = 2;
                    TUTDFFile fff = new TUTDFFile("", "");
                    fff.GetFromDatabaseByAccountNumber(rec.Transaction.AccountNumber.Value);
                    Map<String, Integer> myMap = new HashMap<String, Integer>();
                    for (TUTDFRecord r : fff.Records) {
                        if (!r.IDPasport.SeriesNumber.Value.equals(rec.IDPasport.SeriesNumber.Value) || !r.IDPasport.IDNumber.Value.equals(rec.IDPasport.IDNumber.Value)) {
                            if (!myMap.containsKey(r.IDPasport.SeriesNumber.Value + "-" + r.IDPasport.IDNumber.Value)) {
                                myMap.put(r.IDPasport.SeriesNumber.Value + "-" + r.IDPasport.IDNumber.Value, 1);
                                s += r.IDPasport.AsTabString().replace("ID01", "ID0" + id);
                                id++;
                            }
                        }
                    }
                    s += rec.IDSnils.AsTabString().replace("ID02", "ID0" + id);
                } else {
                    s += rec.IDSnils.AsTabString();
                }

                s += rec.PersonName.AsTabString();
                s += rec.AddressRegistration.AsTabString();
                s += rec.AddressResidence.AsTabString();
                s += rec.Transaction.AsTabString();
                s += "\n";
                br.write(s);
            }

            br.write("TRLR\t");

            br.close();

            stream.close();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public String GetFile() {
        return File;
    }

    private String GetColumnsFromTable(String table) {
        String ret = "";
        String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'nbki' AND NOT COLUMN_NAME LIKE 'SYSTEM_%' AND TABLE_NAME = '" + table + "'";

        try {
            db.CloseQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
        db.Query(query, false, false);
        ResultSet rs = db.GetResultSet();

        try {
            if (rs != null) {
                while (rs.next()) {
                    ret += " " + table + "." + rs.getString("COLUMN_NAME") + " AS " + table + "__" + rs.getString("COLUMN_NAME") + ", \n";
                }
            } else {
                return "";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
        return ret;
    }

    public boolean GetFromDatabaseByAccountNumber(String accNum) {
        db = new Db();
        db.OpenNbki();

        String tmpQuery = "";
        String query = "select ";

        tmpQuery = GetColumnsFromTable("TR01");
        if (tmpQuery.isEmpty()) return false;
        query += tmpQuery;

        tmpQuery = GetColumnsFromTable("ID01");
        if (tmpQuery.isEmpty()) return false;
        query += tmpQuery;

        tmpQuery = GetColumnsFromTable("ID02");
        if (tmpQuery.isEmpty()) return false;
        query += tmpQuery;

        tmpQuery = GetColumnsFromTable("NA01");
        if (tmpQuery.isEmpty()) return false;
        query += tmpQuery;

        tmpQuery = GetColumnsFromTable("ERROR");
        if (tmpQuery.isEmpty()) return false;
        query += tmpQuery;


        query += " f.FileName, f.FileTime, TR01.SYSTEM_ReportDate as SYSTEM_ReportDate from TR01 \n" +
                " left join ID01 on ID01.SYSTEM_RecordNumber = TR01.SYSTEM_RecordNumber and ID01.SYSTEM_FileId = TR01.SYSTEM_FileId \n" +
                " left join ID02 on ID02.SYSTEM_RecordNumber = TR01.SYSTEM_RecordNumber and ID02.SYSTEM_FileId = TR01.SYSTEM_FileId \n" +
                " left join NA01 on NA01.SYSTEM_RecordNumber = TR01.SYSTEM_RecordNumber and NA01.SYSTEM_FileId = TR01.SYSTEM_FileId \n" +
                " left join ERROR on ERROR.SYSTEM_RecordNumber = TR01.SYSTEM_RecordNumber and ERROR.SYSTEM_FileId = TR01.SYSTEM_FileId \n" +
                " left join FILES f on f.id = TR01.SYSTEM_FileId \n";

        query += " where TR01.SYSTEM_AccountNumber = " + accNum + "\n " +
                " order by TR01.SYSTEM_ReportDate, f.FileName, f.FileTime ;";


        //System.out.println(query);

        try {
            db.CloseQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        db.Query(query, false, false);
        ResultSet rs = db.GetResultSet();

        try {
            if (rs != null) {
                ResultSetMetaData md = rs.getMetaData();
                int columns = md.getColumnCount();

                while (rs.next()) {
                    TUTDFRecord rec = new TUTDFRecord();
                    for (int i = 1; i <= columns; i++) {
                        var s = md.getColumnName(i);
                        var t = md.getTableName(i);
                        if (!t.isEmpty() && !s.isEmpty()) {
                            TUTDFSegment segment = rec.GetSegmentByTag(t);
                            if (segment != null) {
                                var r = segment.GetFieldByName(s);
                                if (r != null) {
                                    r.Value = rs.getString(i);
                                    // System.out.println(md.getColumnLabel(i) + " = " + rs.getString(i));
                                }
                            }
                        }
                    }
                    Records.add(rec);
                    //rec.Transaction.Println();
                }
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }


        db.Close();
        return true;
    }
}
