package rd.rusdengi;

public class TUTDF_AD_Segment extends TUTDFSegment {
    public final TUTDFField AddressType; // Тип адреса
    public final TUTDFField PostalCode; // Почтовый индекс
    public final TUTDFField Country; // Страна
    public final TUTDFField Region; // Регион
    public final TUTDFField Empty; // Поле не используется
    public final TUTDFField District; // Район
    public final TUTDFField Location; // Местоположение
    public final TUTDFField StreetType; // Тип улицы
    public final TUTDFField Street; // Улица
    public final TUTDFField HouseNumber; // Номер дома
    public final TUTDFField Block; // Корпус
    public final TUTDFField Building; // Строение
    public final TUTDFField Apartment; // Квартира
    public final TUTDFField Status; // Статус адреса
    public final TUTDFField Since; // Дата прописки/регистрации

    public TUTDF_AD_Segment(Integer val) throws IllegalAccessException {
        super("AD", "Данное поле должно содержать буквы AD01 для первого адреса и AD02 для второго адреса ",
                "Сегмент (сегменты) AD включает (включают) в себя известные адреса субъектов и:\n" +
                        "• Является обязательным сегментом.\n" +
                        "• Должен включать адрес прописки и адрес проживания физического лица (сегмент NA)\n" +
                        "• Должен включать фактический адрес юридического лица (сегмент BU)");
        String v = val.toString();
        if (v.length() == 1) {
            v = "0" + v;
        }
        SegmentTag.Value += v;

        AddressType = new TUTDFField(2, "Тип адреса", "N", 1, 4, "Допустимыми значениями являются:\n" + "1= Адрес регистрации (только для физлиц)\n" + "2= Адрес фактического местожительства (только для физлиц)\n" + "3= Юридический адрес (только для юрлиц)\n" + "4= Фактический адрес (только для юрлиц)");
        PostalCode = new TUTDFField(3, "Почтовый индекс", "N", 6, 4, "Должен состоять из шести цифр. Не заполняется для иностранных адресов.");
        Country = new TUTDFField(4, "Страна", "A", 2, 4, "Страны представлены в Приложении А");
        Region = new TUTDFField(5, "Регион", "N", 2, 4, "Регионы представлены в Приложении А");
        Empty = new TUTDFField(6, "Поле не используется.", "", 0, 4, "Оставьте поле пустым");
        District = new TUTDFField(7, "Район", "P", 80, 4, "Содержит наименование района");
        Location = new TUTDFField(8, "Местоположение", "P", 80, 4, "Содержит город или населённый пункт.");
        StreetType = new TUTDFField(9, "Тип улицы", "N", 2, 4, "Тип улицы см. Приложение А");
        Street = new TUTDFField(10, "Улица", "P", 80, 4, "Название улицы. В случае отсутствия улиц в населенном пункте продублировать его название из поля «Местоположение».");
        HouseNumber = new TUTDFField(11, "Номер дома", "P", 40, 4, "Номер дома");
        Block = new TUTDFField(12, "Корпус", "P", 10, 4, "Корпус");
        Building = new TUTDFField(13, "Строение", "P", 20, 4, "Строение");
        Apartment = new TUTDFField(14, "Квартира", "P", 40, 4, "Квартира");
        Status = new TUTDFField(15, "Статус", "N", 1, 4, "Статус");
        Since = new TUTDFField(16, "Дата прописки", "D", 8, 4, "Дата прописки/ регистрации");

        InitFieldsArray();
    }

    public TUTDF_AD_Segment() throws IllegalAccessException {
        this(1);
    }
}