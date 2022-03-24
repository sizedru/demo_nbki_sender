package rd.rusdengi;

import java.util.ArrayList;

public class TUTDF_ERR_Segment extends TUTDFSegment {
    public final TUTDFField RecordNumber;
    public final ArrayList<TUTDFField> Data = new ArrayList<>();

    public TUTDF_ERR_Segment() throws IllegalAccessException {
        super("ERROR", "Содержит буквы ERROR", "Сегмент ERROR содержит детальную информацию, которая раскрывает причину отклонения сегмента или записи. В отличие от всех предыдущих сегментов сегмент ERROR появляется только в файле отказа, возвращаемом отправителю данных.  Также в отличие от всех указанных выше данный сегмент имеет непостоянное количество столбцов.\n" +
                "Две первые позиции определяют сегмент и порядковый номер исходной записи с ошибкой. Далее следуют данные данные, раскрывающие причину ошибки. Они составляются как указание сегмента и один или нескольких кодов отказа. Сами коды отказа записываются в форме N-A, где \n" +
                "N -  номер поля с ошибкой;\n" +
                "A - буквы M, I, W, IL или Q, которые служат для обозначения ошибок - (M)issing (отсутствующий), (I)nvalid (недействителен), (W)arning (Внимание!), (Il)legal (недопустим), или (Q) - отсутствующая/ лишняя табуляция.\n" +
                "Коды возвращаются в случае любого обнаруженного поля с ошибкой. Также, если обязательный сегмент отсутствует, он обозначатся кодом 0-М. «Внимание!» используется для недопустимых и необязательных данных.\n" +
                "Все сегменты могут возвращать значение «0-Q», свидетельствующее об отклонении всего сегмента вследствие отсутствующих/ лишних табуляций в строке. Данная ошибка указывает на то, что сегмент не может быть обработан. Дальнейшая обработка сегмента производиться не будет");

        RecordNumber = new TUTDFField(2, "Порядковый номер записи", "N", 7, 4, "Порядковый номер записи в исходном файле TUTDF, содержащей отклонённые данные.");

        InitFieldsArray();
    }

    @Override
    public int GetFieldsCount() {
        return FieldsPos.length + Data.size();
    }

    @Override
    public TUTDFField GetFieldByPosition(Integer pos) {
        if (pos <= FieldsPos.length) {
            return super.GetFieldByPosition(pos);
        } else {
            int tmpPos = pos - FieldsPos.length;
            if (Data.size() < tmpPos) {
                for (int i = Data.size(); i <= tmpPos; i++) {
                    String ind = "000" + i;
                    ind = ind.substring(ind.length() - 3);
                    TUTDFField f = new TUTDFField(tmpPos, "столбец " + ind, "", 50, 4, "");
                    f.SysName = "field_" + ind;
                    Data.add(f);
                }
            }
            return Data.get(tmpPos - 1);
        }
    }

    @Override
    public TUTDFField GetFieldByName(String name) {
        TUTDFField f = super.GetFieldByName(name);
        if (f == null) {
            for (TUTDFField d : Data) {
                if (d.SysName.equals(name)) {
                    f = d;
                    break;
                }
            }
        }
        return f;
    }

}
