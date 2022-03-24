package rd.rusdengi;

public enum TUTDF_TR_AccountType {
    AUTOCREDIT("01", "Кредит на автомобиль"),
    LIZING("04", "Лизинг"),
    IPOTEKA("06", "Ипотека"),
    CREDITKA("07", "Кредитная карта"),
    POTREBITELSKIY("09", "Потребительский кредит"),
    BIZNES("10", "На развитие бизнеса"),
    OBOROT("11", "На пополнение оборотных средств"),
    OBORUDOVANIE("12", "На покупку оборудования"),
    STROITELSTVO("13", "На строительство"),
    CENNIEBUMAGI("14", "На покупку ценных бумаг"),
    MICROCREDIT("16", "Микрокредит"),
    DEBETKARTA("17", "Дебетовая карта с овердрафтом"),
    OVERDRAFT("18", "Овердрафт");

    private final String Code;
    private final String Name;

    TUTDF_TR_AccountType(String code, String name) {
        this.Code = code;
        this.Name = name;
    }

}
