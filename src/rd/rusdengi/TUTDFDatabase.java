package rd.rusdengi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TUTDFDatabase {
    private final Db db;
    private final TUTDFFile File;


    public TUTDFDatabase(TUTDFFile file) {
        File = file;
        db = new Db();
        db.OpenNbki();
    }

    private boolean TableExist(String tbn) {
        boolean tableExist = false;
        String query = "SHOW TABLES FROM `nbki` LIKE '" + tbn + "'";
        db.Query(query, false, false);
        ResultSet rs = db.GetResultSet();
        try {
            if (rs != null) {
                if (rs.next()) {
                    tableExist = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableExist;
    }

    private void CheckTable(TUTDFSegment Segment) {
        var tableExist = false;
        boolean allFieldsExist = false;
        String query;
        ResultSet rs;
        ArrayList<String> columns = new ArrayList<>();

        tableExist = TableExist(Segment.SegmentTag.Value);

        if (!tableExist) {
            CreateTable(Segment);
        } else {
            query = "SHOW COLUMNS FROM `" + Segment.SegmentTag.Value + "` where not Field like 'SYSTEM_%'";
            db.Query(query, false, false);
            rs = db.GetResultSet();

            for (int i = 1; i <= Segment.GetFieldsCount(); i++) {
                TUTDFField f = Segment.GetFieldByPosition(i);
                columns.add(f.SysName);
            }

            try {
                if (rs != null) {
                    while (rs.next()) {
                        String fn = rs.getString(1);
                        if (fn != null) {
                            columns.remove(fn);
                        }
                    }
                    if (columns.size() == 0) {
                        allFieldsExist = true;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


            if (!allFieldsExist) {
                CreateFields(Segment, columns);
            }
        }
    }

    private void CreateFields(TUTDFSegment Segment, ArrayList<String> columns) {
        for (String col : columns) {
            TUTDFField f = Segment.GetFieldByName(col);
            if (f.Value.length() > 0) {
                String query = "alter table `" + Segment.SegmentTag.Value + "` add `" + f.SysName + "` varchar(" + f.GetLen() + ") NOT NULL DEFAULT '';";
                //System.out.println(query);
                db.Query(query, true, false);
            }
        }
    }

    private void CreateTable(TUTDFSegment Segment) {
        StringBuilder query = new StringBuilder("create table `" + Segment.SegmentTag.Value + "` (\n");
        query.append("SYSTEM_FileId int not null, \n").append("SYSTEM_RecordNumber int not null,\n");
        for (int i = 1; i <= Segment.GetFieldsCount(); i++) {
            TUTDFField f = Segment.GetFieldByPosition(i);
            query.append("`").append(f.SysName).append("` varchar(").append(f.GetLen()).append(") NOT NULL DEFAULT '',\n");
        }
        query = new StringBuilder(query.substring(0, query.length() - 2) + "\n);\n");
        //System.out.println(query);
        db.Query(query.toString(), true, false);
        CreateSpecialFieldsAndIndexes(Segment);
    }

    private void CreateSpecialTables() {
        String query;
        var tableExist = TableExist("FILES");
        if (!tableExist) {
            query = "create table FILES " +
                    "( " +
                    "ID int auto_increment, " +
                    "FileName varchar(30) not null, " +
                    "Description varchar(255) null, " +
                    "PRIMARY KEY (ID)" +
                    "); " +
                    "create unique index FILES_ID_uindex on FILES (ID);" +
                    "create index FILES_FileName_index on nbki.FILES (FileName);";
            // System.out.println(query);
            db.Query(query, true, true);
        }
    }

    private void CreateSpecialFieldsAndIndexes(TUTDFSegment Segment) {
        String query;
        if (Segment != null) {
            if (Segment.getClass().equals(TUTDF_TR_Segment.class)) {
                query = "create index " + Segment.SegmentTag.Value + "_AccountNumber_index on `" + Segment.SegmentTag.Value + "` (AccountNumber);";
                db.Query(query, true, false);
            }

            query = "alter table `" + Segment.SegmentTag.Value + "` add constraint `" + Segment.SegmentTag.Value + "_pk` unique (SYSTEM_FileId, SYSTEM_RecordNumber);";
            db.Query(query, true, true);
        }
    }

    private void InsertToTable(TUTDFSegment Segment, int idFileName, Integer recordNumber) {
        String maxTrRepDat = "";

        if (Segment != null && Segment.IsFill()) {
            String query = "insert ignore into `" + Segment.SegmentTag.Value + "`\n";
            StringBuilder queryFields = new StringBuilder("(SYSTEM_FileId, SYSTEM_RecordNumber,");
            StringBuilder queryValues = new StringBuilder("VALUES (").append(idFileName).append(", ").append(recordNumber).append(", ");
            if (Segment.getClass().equals(TUTDF_TR_Segment.class)) {
                queryFields.append("SYSTEM_AccountNumber,");
                queryValues.append(((TUTDF_TR_Segment) Segment).AccountNumber.Value).append(", ");

                queryFields.append("SYSTEM_ReportDate,");
                if (((TUTDF_TR_Segment) Segment).DateReported.Value.isEmpty()) {
                    maxTrRepDat = "(select FileTime from FILES where ID=" + idFileName + ")";
                } else {
                    maxTrRepDat = ((TUTDF_TR_Segment) Segment).DateReported.Value;
                    maxTrRepDat = "'" + maxTrRepDat.substring(0, 4) + "-" + maxTrRepDat.substring(4, 6) + "-" + maxTrRepDat.substring(6, 8) + "'";
                }
                queryValues.append(maxTrRepDat).append(", ");
            }
            for (int i = 1; i <= Segment.GetFieldsCount(); i++) {
                TUTDFField f = Segment.GetFieldByPosition(i);
                if (!f.Value.isEmpty()) {
                    queryFields.append("`").append(f.SysName).append("`, ");
                    queryValues.append("'").append(f.Value).append("', ");
                }
            }
            queryFields = new StringBuilder(queryFields.substring(0, queryFields.length() - 2) + ") ");
            queryValues = new StringBuilder(queryValues.substring(0, queryValues.length() - 2) + ")");
            query += queryFields + queryValues.toString() + ";\n";
            //System.out.println(query);
            db.Query(query, true, false);
            int res = db.GetResult();
            if (res == Db.ERR_FIELD_NOT_EXIST || res == Db.ERR_TABLE_NOT_EXIST) {
                CheckTable(Segment);
                db.Query(query, true, false);
                //res = db.GetResult();
            }

            if (Segment.getClass().equals(TUTDF_TR_Segment.class)) {
                query = "INSERT INTO rep_dat(SYSTEM_AccountNumber, max_report_date) VALUES(" + ((TUTDF_TR_Segment) Segment).AccountNumber.Value + ", " + maxTrRepDat + ") ON DUPLICATE KEY UPDATE max_report_date = GREATEST(max_report_date, VALUES(max_report_date));";
                //System.out.println(query);
                db.Query(query, true, false);
            }
        }
    }

    public void Close() {
        db.Close();
    }

    private void UpdateDatabaseRecord(TUTDFRecord Record, int idFileName) {
        //if(!CurrentFile.equal(Record.FileName))
        InsertToTable(Record.Transaction, idFileName, Record.RecordNumber);
        InsertToTable(Record.Errors, idFileName, Record.RecordNumber);
        InsertToTable(Record.IDPasport, idFileName, Record.RecordNumber);
        InsertToTable(Record.IDSnils, idFileName, Record.RecordNumber);
        InsertToTable(Record.PersonName, idFileName, Record.RecordNumber);
    }

    public void UpdateDatabase() {
        String query;
        int idFileName = 0;

        CreateSpecialTables();


        try {
            query = "select * from FILES where FileName='" + File.GetFile() + "'";
            db.Query(query, false, false);
            var rs = db.GetResultSet();
            if (rs != null) {
                while (rs.next()) {
                    idFileName = rs.getInt("ID");
                }
            }
            if (idFileName == 0) {
                var dt = File.GetFile();
                dt = dt.substring(13, 21);
                dt = dt.substring(0, 4) + "-" + dt.substring(4, 6) + "-" + dt.substring(6, 8);
                query = "insert into FILES (FileName, FileTime, RequestTime) VALUES('" + File.GetFile() + "', '" + dt + "', '" + dt + "');";
                db.Query(query, true, false);
            }
            query = "select * from FILES where FileName='" + File.GetFile() + "'";
            db.Query(query, false, false);
            rs = db.GetResultSet();
            if (rs != null) {
                while (rs.next()) {
                    idFileName = rs.getInt("ID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (TUTDFRecord rec : File.Records) {
            if (rec.Transaction.IsFill()) {
                UpdateDatabaseRecord(rec, idFileName);
            }
        }
    }
}
