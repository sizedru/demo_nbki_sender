package rd.rusdengi;

import java.lang.reflect.Field;

public class TUTDFSegment {
    public final String SegmentDescription;
    public final TUTDFField[] FieldsPos;
    public final TUTDFField SegmentTag;

    public TUTDFSegment(String val, String desc, String segDesc) {
        SegmentTag = new TUTDFField(1, "Наименование сегмента", "A/N", Math.max(4, val.length()), 1, desc);
        SegmentTag.Value = val;

        SegmentDescription = segDesc;

        int i = 0;
        for (Field field : getClass().getDeclaredFields()) {
            if (field.getType().equals(TUTDFField.class)) {
                i++;
            }
        }

        FieldsPos = new TUTDFField[i + 1];
    }

    public TUTDFField GetFieldByName(String name) {
        TUTDFField f = null;
        try {
            Class<? extends TUTDFSegment> cls = getClass();
            Field fld = cls.getField(name);
            f = (TUTDFField) fld.get(this);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {

        }
        return f;
    }

    void InitFieldsArray() throws IllegalAccessException {
        for (Field field : getClass().getFields()) {
            if (field.getType().equals(TUTDFField.class)) {
                TUTDFField f = (TUTDFField) field.get(this);
                f.SysName = field.getName();
                FieldsPos[f.GetPosition() - 1] = f;
            }
        }

    }

    public int GetFieldsCount() {
        return FieldsPos.length;
    }

    public TUTDFField GetFieldByPosition(Integer pos) {
        if (FieldsPos.length > pos - 1) {
            return FieldsPos[pos - 1];
        }
        return null;
    }

    public TUTDFField GetFieldByIndex(Integer pos) {
        return GetFieldByPosition(pos + 1);
    }

    public TUTDFField SetFieldValueByPosition(Integer pos, String val) {
        TUTDFField f = GetFieldByPosition(pos);
        if (f != null) {
            f.Value = val;
        }
        return f;
    }

    public void SetFieldValueByIndex(Integer pos, String val) {
        SetFieldValueByPosition(pos + 1, val);
    }

    public void Println() {
        if (IsFill()) {
            for (int i = 0; i < GetFieldsCount(); i++) {
                TUTDFField f = GetFieldByIndex(i);
                if (f.Value.length() > 0)
                    System.out.print(f.Value + " ");
                else if (f.GetType() == "D")
                    System.out.print(f.Value + "         ");
            }
            System.out.println();
        }
    }


    public boolean IsFill() {
        return !FieldsPos[1].Value.isEmpty();
    }

    public String AsTabString() {
        StringBuilder ret = new StringBuilder();
        String r = "";
        if (IsFill()) {
            boolean First = true;
            for (TUTDFField f : FieldsPos) {
                if (!First) {
                    ret.append("\t").append(f.Value);
                } else {
                    ret.append(f.Value);
                    First = false;
                }
            }
            //ret = new StringBuilder(ret.toString().trim());
            //ret.append("\n");
            r = ret.toString().replace("\n", " ").replace("\r", " ").replace("  ", " ") + "\n";
        }
        return r;
    }
}