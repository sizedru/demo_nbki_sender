package rd.rusdengi;

public class TUTDFRecord {
    public TUTDF_ID_Segment IDPasport;
    public TUTDF_ID_Segment IDSnils;
    public TUTDF_NA_Segment PersonName;
    public TUTDF_AD_Segment AddressRegistration;
    public TUTDF_AD_Segment AddressResidence;
    public TUTDF_TR_Segment Transaction;
    public TUTDF_ERR_Segment Errors;
    public Boolean CheckOldPasport;
    Integer RecordNumber;

    public TUTDFRecord() throws IllegalAccessException {
        IDPasport = new TUTDF_ID_Segment(1);
        IDSnils = new TUTDF_ID_Segment(2);
        PersonName = new TUTDF_NA_Segment();
        AddressRegistration = new TUTDF_AD_Segment(1);
        AddressResidence = new TUTDF_AD_Segment(2);
        Transaction = new TUTDF_TR_Segment();
        Errors = new TUTDF_ERR_Segment();
        CheckOldPasport = false;
    }

    public void SetRecordNumber(Integer recNum) {
        RecordNumber = recNum;
    }

    public TUTDFSegment GetSegmentByTag(String tag) {
        TUTDFSegment ret;
        if (tag.equals(Transaction.SegmentTag.Value)) ret = Transaction;
        else if (tag.equals(IDPasport.SegmentTag.Value)) ret = IDPasport;
        else if (tag.equals(IDSnils.SegmentTag.Value)) ret = IDSnils;
        else if (tag.equals(PersonName.SegmentTag.Value)) ret = PersonName;
        else if (tag.equals(Errors.SegmentTag.Value)) ret = Errors;
        else if (tag.equals(AddressRegistration.SegmentTag.Value)) ret = AddressRegistration;
        else if (tag.equals(AddressResidence.SegmentTag.Value)) ret = AddressResidence;
        else ret = null;
        return ret;
    }

}
