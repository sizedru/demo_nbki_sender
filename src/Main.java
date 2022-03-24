import rd.rusdengi.TUTDFDatabase;
import rd.rusdengi.TUTDFFile;
import rd.rusdengi.TUTDFFileGenerator;

public class Main {
    public static void main(String[] args) throws IllegalAccessException {

        if (args.length == 1) {
            String fn = args[0];
            String dir = "out/";
            TUTDFFile file = new TUTDFFile(dir, fn);
            for (var rec : file.Records) {
                rec.Transaction.Println();
                rec.Errors.Println();
            }

            var Db = new TUTDFDatabase(file);
            Db.UpdateDatabase();
            Db.Close();

        } else {
            String testId = "";
            if (args.length > 0) {
                testId = args[1];
            }
            TUTDFFileGenerator fileGenerator = new TUTDFFileGenerator();
            fileGenerator.GenerateFromOriginal(testId);
            fileGenerator.NewFile.WriteNew();
        }
    }
}
