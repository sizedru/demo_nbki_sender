package rd.rusdengi;

public class TUTDF_ID_Segment extends TUTDFSegment {
    public final TUTDFField IDType;
    public final TUTDFField SeriesNumber;
    public final TUTDFField IDNumber;
    public final TUTDFField IssueDate;
    public final TUTDFField IssueAuthority;
    public final TUTDFField EmptyField1;
    public final TUTDFField EmptyField2;

    public TUTDF_ID_Segment(Integer val) throws IllegalAccessException {
        super("ID", "Данное поле должно содержать значение ID01 для первого ID, ID02 для второго ID и далее таким же образом. Максимальное допустимое число ID – 99",
                "Идентифицирующий сегмент ID содержит идентифицирующие номера, присваиваемые физическому лицу, юридическому лицу или частному предпринимателю (ПБЮЛ):\n" +
                        "• Является обязательным сегментом. \n" +
                        "• Должен быть уникальным в пределах данной записи, то есть не должно быть сегментов ID, содержащих одинаковые значения в полях номера серии (если имеется) и номера документа. В противном случае вся запись будет отвергнута. \n" +
                        "• В случае, если сегментов ID несколько, то нумерация сегментов (ID01, ID02, ID03 и т.д.) должна начинаться с ID01 для каждой строки и последовательно увеличиваться.");
        String v = val.toString();
        if (v.length() == 1) {
            v = "0" + v;
        }
        SegmentTag.Value += v;

        IDType = new TUTDFField(2, "Тип ID (тип документа)", "N", 2, 4, "См. Приложение A");
        SeriesNumber = new TUTDFField(3, "Номер серии", "P", 20, 4, "Как указано в официальном юридическом документе. Должен использоваться для ID Type 01, 02, 21 и 22");
        IDNumber = new TUTDFField(4, "Номер документа", "P", 20, 4, "Как указано в документе.\nПоле не заполняется (остается пустым) только при указании Типа ID 97 (Без указания СНИЛС)");
        IssueDate = new TUTDFField(5, "Когда выдан", "D", 8, 4, "Это поле должно быть если значение поля ID Type меньше 28. Данное поле должно содержать фактическую дату по календарю в формате ГГГГММДД.\nНе может быть ранее Даты рождения сегмента имени NA.");
        IssueAuthority = new TUTDFField(6, "Когда выдан", "P", 510, 4, "Название отделения внутренних дел МВД РФ или другого органа выдачи, код подразделения, выдавшего паспорт или иной документ, удостоверяющий личность. Это поле должно быть, если значение поля ID Type меньше 28.\nПоле будет удалено, если передаваемые данные некорректны. См. раздел «Искажение данных»");
        EmptyField1 = new TUTDFField(7, "Поле не используется", "", 0, 4, "Не используется с 31.01.2019.");
        EmptyField2 = new TUTDFField(8, "Поле не используется", "", 0, 4, "Оставьте поле пустым.");

        InitFieldsArray();
    }

    public TUTDF_ID_Segment() throws IllegalAccessException {
        this(1);
    }
}
