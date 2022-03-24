package rd.rusdengi;

public class TUTDFField implements ITUTDFField {
    private final Integer Position;
    private final String Name;
    private final String Type;
    private final Integer Length;
    private final Integer Must;
    private final String Description;

    public String Value;
    public String SysName;

    TUTDFField(Integer pos, String name, String type, Integer len, Integer must, String desc) {
        Value = "";
        Position = pos;
        Name = name;
        Type = type;
        Length = len;
        Must = must;
        Description = desc;
        SysName = "";
    }

    @Override
    public Integer GetLen() {
        return Length;
    }

    @Override
    public String GetName() {
        return Name;
    }

    @Override
    public String GetDescription() {
        String[] str = Description.split("\\. ");
        return String.join(". \n", str);
    }

    @Override
    public String GetType() {
        return Type;
    }

    @Override
    public Integer GetPosition() {
        return Position;
    }
}
