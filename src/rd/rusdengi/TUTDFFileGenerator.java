package rd.rusdengi;

public class TUTDFFileGenerator {
    public TUTDFFile OriginalFile;
    public TUTDFFile NewFile;

    public TUTDFFileGenerator() {
        NewFile = new TUTDFFile("out/", "");
        OriginalFile = null;
    }

    public TUTDFFileGenerator(String file) {
        this();
        ReadFile(file);
    }

    private void AddNewRec(RDOrder Order) {
        try {
            TUTDFRecord newRec = new TUTDFRecord();

            newRec.IDPasport.IDType.Value = "21";
            newRec.IDPasport.SeriesNumber.Value = Order.PasportSer;
            newRec.IDPasport.IDNumber.Value = Order.PasportNum;
            newRec.IDPasport.IssueDate.Value = RDOrder.GetTUTDFDate(Order.PasportDate);
            newRec.IDPasport.IssueAuthority.Value = Order.PasportIssued.replace("\t", " ").replace("'", "");
            //rec.IDPasport.Println();
            //newRec.IDPasport.Println();

            newRec.IDSnils.IDType.Value = "97";
            //rec.IDSnils.Println();
            //newRec.IDSnils.Println();

            newRec.PersonName.FirstName.Value = Order.FirstName;
            newRec.PersonName.Surname.Value = Order.Surname;
            newRec.PersonName.PatronymicName.Value = Order.SecondName;
            newRec.PersonName.DateOfBirth.Value = RDOrder.GetTUTDFDate(Order.Birthday);
            newRec.PersonName.PlaceOfBirth.Value = Order.BirthPlace.replace("\t", " ").replace("'", "");

            //rec.PersonName.Println();
            //newRec.PersonName.Println();

            newRec.AddressRegistration.AddressType.Value = "1";
            newRec.AddressRegistration.PostalCode.Value = Order.PostIndexRegistration;
            if (newRec.AddressRegistration.PostalCode.Value.isEmpty()) {
                newRec.AddressRegistration.PostalCode.Value = newRec.AddressResidence.PostalCode.Value;
            }
            if (newRec.AddressRegistration.PostalCode.Value.isEmpty()) {
                newRec.AddressRegistration.PostalCode.Value = "398000";
            }
            newRec.AddressRegistration.Country.Value = "RU";
            newRec.AddressRegistration.Location.Value = Order.CityRegistration.replace("\t", " ").replace("'", "");
            if (newRec.AddressRegistration.Location.Value.isEmpty()) {
                newRec.AddressRegistration.Location.Value = "Москва";
            }
            newRec.AddressRegistration.Street.Value = Order.StreetRegistration.replace("\t", " ").replace("'", "");
            if (newRec.AddressRegistration.Street.Value.isEmpty()) {
                newRec.AddressRegistration.Street.Value = "Ленина";
            }
            newRec.AddressRegistration.HouseNumber.Value = Order.HouseNumberRegistration.replaceAll("\\D+", "");
            newRec.AddressRegistration.Block.Value = Order.BlockSymbolRegistration.replace("\t", " ").replace("'", "").replace("-", "").replace("нет", "");
            if (newRec.AddressRegistration.Block.Value.length() > 1) {
                newRec.AddressRegistration.Block.Value = "";
            }
            newRec.AddressRegistration.Apartment.Value = Order.ApartmentRegistration.replaceAll("\\D+", "");

            //rec.AddressRegistration.Println();
            //newRec.AddressRegistration.Println();

            newRec.AddressResidence.AddressType.Value = "2";
            newRec.AddressResidence.PostalCode.Value = Order.PostIndexResidence;
            if (newRec.AddressResidence.PostalCode.Value.isEmpty()) {
                newRec.AddressResidence.PostalCode.Value = newRec.AddressRegistration.PostalCode.Value;
            }
            if (newRec.AddressResidence.PostalCode.Value.isEmpty()) {
                newRec.AddressResidence.PostalCode.Value = "398000";
            }
            newRec.AddressResidence.Country.Value = "RU";
            newRec.AddressResidence.Location.Value = Order.CityResidence.replace("\t", " ").replace("'", "");
            if (newRec.AddressResidence.Location.Value.isEmpty()) {
                newRec.AddressResidence.Location.Value = "Москва";
            }
            newRec.AddressResidence.Street.Value = Order.StreetResidence.replace("\t", " ").replace("'", "");
            if (newRec.AddressResidence.Street.Value.isEmpty()) {
                newRec.AddressResidence.Street.Value = "Ленина";
            }
            newRec.AddressResidence.HouseNumber.Value = Order.HouseNumberResidence.replaceAll("\\D+", "");
            newRec.AddressResidence.Block.Value = Order.BlockSymbolResidence.replace("\t", " ").replace("'", "").replace("-", "").replace("нет", "");
            if (newRec.AddressRegistration.Block.Value.length() > 1) {
                newRec.AddressRegistration.Block.Value = "";
            }
            newRec.AddressResidence.Apartment.Value = Order.ApartmentResidence.replaceAll("\\D+", "");

            //rec.AddressResidence.Println();
            //newRec.AddressResidence.Println();

            newRec.Transaction.UserName.Value = "DR01FF000001";
            newRec.Transaction.AccountNumber.Value = Order.ID;
            newRec.Transaction.AccountType.Value = "16";
            newRec.Transaction.AccountRelationship.Value = "1";
            newRec.Transaction.DateAccountOpened.Value = RDOrder.GetTUTDFDate(Order.OpenDate);
            newRec.Transaction.DateOfLastPayment.Value = RDOrder.GetTUTDFDate(Order.LastPayDate);
            newRec.Transaction.DateAccountRating.Value = RDOrder.GetTUTDFDate(Order.CurrentDate);
            if (Order.LastReportDatePlusDay == null) {
                newRec.Transaction.DateReported.Value = RDOrder.GetTUTDFDate(Order.CurrentDate);
            } else {
                newRec.Transaction.DateReported.Value = RDOrder.GetTUTDFDate(Order.LastReportDatePlusDay);
            }

            newRec.Transaction.NextPayment.Value = "0";
            newRec.Transaction.PastDue.Value = "0";


            if (Order.CloseFact) {
                newRec.Transaction.DateOfLastPayment.Value = RDOrder.GetTUTDFDate(Order.CloseDate);
                newRec.Transaction.DateAccountRating.Value = RDOrder.GetTUTDFDate(Order.CloseDate);
                newRec.Transaction.AccountRating.Value = "13";
                if (newRec.Transaction.DateReported.Value.compareTo(newRec.Transaction.DateAccountRating.Value) < 0) {
                    newRec.Transaction.DateReported.Value = newRec.Transaction.DateAccountRating.Value;
                }
            } else if (Order.BankrotFact) {
                newRec.Transaction.DateOfLastPayment.Value = RDOrder.GetTUTDFDate(Order.BankrotDate);
                newRec.Transaction.DateAccountRating.Value = RDOrder.GetTUTDFDate(Order.BankrotDate);
                newRec.Transaction.AccountRating.Value = "95";
                if (newRec.Transaction.DateReported.Value.compareTo(newRec.Transaction.DateAccountRating.Value) < 0) {
                    newRec.Transaction.DateReported.Value = newRec.Transaction.DateAccountRating.Value;
                }
            } else if (Order.FineDays <= 0) {
                newRec.Transaction.AccountRating.Value = "00";
                newRec.Transaction.DateReported.Value = RDOrder.GetTUTDFDate(Order.CurrentDate);
            } else {
                newRec.Transaction.AccountRating.Value = "52";
                if (Order.CessionFact) {
                    newRec.Transaction.DateAccountRating.Value = newRec.Transaction.DateReported.Value;
                    if (newRec.Transaction.DateReported.Value.compareTo(newRec.Transaction.DateOfLastPayment.Value) < 0) {
                        newRec.Transaction.DateOfLastPayment.Value = newRec.Transaction.DateReported.Value;
                    }

                } else {
                    newRec.Transaction.DateReported.Value = RDOrder.GetTUTDFDate(Order.CurrentDate);
                }
                newRec.Transaction.PastDue.Value = String.valueOf((int) Math.floor(Order.CurrNeedPay));
                if (newRec.Transaction.PastDue.Value.equals("0")) {
                    newRec.Transaction.PastDue.Value = "1";
                }
            }

            newRec.Transaction.ContractAmount.Value = String.valueOf((int) Math.floor(Order.Sum));
            newRec.Transaction.Balance.Value = String.valueOf((int) Math.floor(Order.AllPaySum));

            newRec.Transaction.CreditPaymentFrequency.Value = "7";

            if (newRec.Transaction.AccountRating.Value.equals("00") && newRec.Transaction.DateOfLastPayment.Value.equals("19000102")) {
                newRec.Transaction.MOP.Value = "0";
            } else if (Order.FineDays <= 0) {
                newRec.Transaction.MOP.Value = "1";
            } else if (Order.FineDays > 0 && Order.FineDays <= 29) {
                newRec.Transaction.MOP.Value = "A";
            } else if (Order.FineDays > 29 && Order.FineDays <= 59) {
                newRec.Transaction.MOP.Value = "2";
            } else if (Order.FineDays > 59 && Order.FineDays <= 89) {
                newRec.Transaction.MOP.Value = "3";
            } else if (Order.FineDays > 89 && Order.FineDays <= 119) {
                newRec.Transaction.MOP.Value = "4";
            } else if (Order.FineDays > 119) {
                newRec.Transaction.MOP.Value = "5";
            }

            newRec.Transaction.CurrencyCode.Value = "RUB";

            if (Order.CloseFact) {
                if (Order.OrderToDate) {
                    newRec.Transaction.DateOfContractTermination.Value = RDOrder.GetTUTDFDate(Order.sqlDatePlusDays(Order.CurrentDate, -Order.FineDays));
                } else {
                    newRec.Transaction.DateOfContractTermination.Value = RDOrder.GetTUTDFDate(Order.sqlDatePlusDays(Order.CloseDate, -Order.FineDays));
                }
            } else {
                newRec.Transaction.DateOfContractTermination.Value = RDOrder.GetTUTDFDate(Order.sqlDatePlusDays(Order.CurrentDate, -Order.FineDays));
            }

            System.out.println(Order.FineDays);


            newRec.Transaction.DatePaymentDue.Value = newRec.Transaction.DateOfContractTermination.Value;
            newRec.Transaction.DateInterestPaymentDue.Value = newRec.Transaction.DatePaymentDue.Value;

            newRec.Transaction.InterestPaymentFrequency.Value = "7";

            newRec.Transaction.AmountOutstanding.Value = String.valueOf((int) Math.floor(Order.AllNeedPay));

            newRec.Transaction.GuarantorIndicator.Value = "N";
            newRec.Transaction.BankGuaranteeIndicator.Value = "N";

            newRec.Transaction.OverallValueOfCreditPercent.Value = Order.Psk;

            if (newRec.Transaction.AccountRating.Value.equals("13") || newRec.Transaction.AccountRating.Value.equals("95")) {
                newRec.Transaction.CompletePerformanceOfObligationsDate.Value = newRec.Transaction.DateAccountRating.Value;
                newRec.Transaction.AmountOutstanding.Value = "0";
            }
            newRec.Transaction.PrincipalAmountOutstandingAsOfLastPaymentDate.Value = "0";
            newRec.Transaction.InterestAmountOutstandingAsOfLastPaymentDate.Value = "0";
            newRec.Transaction.OtherAmountOutstandingAsOfLastPaymentDate.Value = "0";
            newRec.Transaction.PrincipalAmountPastDueAsOfLastPaymentDate.Value = "0";
            newRec.Transaction.InterestAmountPastDueAsOfLastPaymentDate.Value = "0";
            newRec.Transaction.OtherAmountPastDueAsOfLastPaymentDate.Value = "0";

            newRec.Transaction.TradeUniversallyUniqueID.Value = Order.UUID;

            newRec.Transaction.OverallValueOfCreditMonetary.Value = Order.PskMoney;

            if (newRec.Transaction.AccountRating.Value.equals("00") || newRec.Transaction.AccountRating.Value.equals("52")) {
                newRec.Transaction.NextPayment.Value = String.valueOf((int) Math.floor(Order.AllNeedPay));
            } else {
                newRec.Transaction.NextPayment.Value = "0";
            }

            var AcceptRecord = true;

            if (newRec.Transaction.AccountRating.Value.equals("13") || newRec.Transaction.AccountRating.Value.equals("95")) {
                var r1 = newRec.Transaction.DateReported.Value.substring(0, 6);
                var r2 = newRec.Transaction.DateAccountRating.Value.substring(0, 6);

                if (newRec.Transaction.DateReported.Value.substring(0, 6).compareTo(newRec.Transaction.DateAccountRating.Value.substring(0, 6)) != 0) {
                    System.out.println(newRec.Transaction.AccountNumber.Value + " в разных месяцах");
                    newRec.Transaction.DateAccountRating.Value = newRec.Transaction.DateReported.Value;
                    newRec.Transaction.CompletePerformanceOfObligationsDate.Value = newRec.Transaction.DateAccountRating.Value;
                    //AcceptRecord = false;
                }
            }

            if (newRec.Transaction.AccountRating.Value.equals("95")) {
                // В сегменте TR если заполнено поле 42 «Дата фактического исполнения обязательств в полном объеме»,
                // то поле 8 «Состояние счета» не может содержать значения 14 «Передан на обслуживание в другую организацию»,
                // 70 «Передача данных прекращена», 85 «Принудительное исполнение обязательств», 90 «Списан с баланса»,
                // 95 «Банкротство, освобождение от требований» или 96 «Возобновлена процедура банкротства».
                // Поэтому обнуляем принудительно
                newRec.Transaction.CompletePerformanceOfObligationsDate.Value = "";
                System.out.println(newRec.Transaction.AccountNumber.Value + " банкрот");
            }

            newRec.CheckOldPasport = Order.PresentOldPasportInBKI;

            if (Order.IsFakeP) {
                newRec.CheckOldPasport = false;
            }

            if (Order.UUID.isEmpty()) {
                System.out.println(newRec.Transaction.AccountNumber.Value + " UUID пустой");
                AcceptRecord = false;
            }
            if (AcceptRecord) {
                NewFile.Records.add(newRec);
            }

            System.out.println(newRec.Transaction.AccountNumber.Value + " " + newRec.Transaction.DateReported.Value + " " + newRec.Transaction.MOP.Value);

            //rec.Transaction.Println();
            //newRec.Transaction.Println();

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public boolean ReadFile(String file) {
        OriginalFile = new TUTDFFile("out/NBKI_FULL/", file);
        return !(OriginalFile == null);
    }

    public boolean GenerateFromOriginal(String testId) {
        boolean testIdRun = !testId.isEmpty();
        RDOrders orders = null;
        String s = "";

        if (OriginalFile != null) {

            for (TUTDFRecord rec : OriginalFile.Records) {
                if (testIdRun) {
                    if (!rec.Transaction.AccountNumber.Value.equals(testId)) {
                        continue;
                    }
                }
                s = s + rec.Transaction.AccountNumber.Value + ", ";
            }
            s = s + "0";
        } else {
            if (testIdRun) {
                s = testId;
            } else {
                s = "SELECT";
            }
        }

        orders = new RDOrders(s);

        if (orders.Orders.size() != 0) {
            if (OriginalFile != null) {
                for (TUTDFRecord rec : OriginalFile.Records) {
                    if (testIdRun) {
                        if (!rec.Transaction.AccountNumber.Value.equals(testId)) {
                            continue;
                        }
                    }

                    AddNewRec(orders.Orders.get(rec.Transaction.AccountNumber.Value));

                }
            } else {
                for (RDOrder ord : orders.Orders.values()) {
                    AddNewRec(ord);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean GenerateOneOrderHistory(String testId) {

        return true;
    }
}
