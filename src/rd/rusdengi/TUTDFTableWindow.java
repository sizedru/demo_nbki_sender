package rd.rusdengi;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.text.ParseException;

public class TUTDFTableWindow extends JFrame {
    private final TableColumnModel columnModel;
    TUTDFFile File;

    private String LastCellValue;

    public TUTDFTableWindow(TUTDFFile file) {
        super(file.GetFile());
        File = file;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Создание таблицы
        // Данные для таблиц
        String[][] array = null;
        // Заголовки столбцов
        String[] columnsHeader = null;

        var k = 0;
        for (TUTDFRecord rec : File.Records) {
            if (columnsHeader == null) {
                columnsHeader = new String[rec.Transaction.GetFieldsCount()];
                array = new String[File.Records.size() * 4][rec.Transaction.GetFieldsCount()];
                for (int i = 1; i <= rec.Transaction.GetFieldsCount(); i++) {
                    TUTDFField f = rec.Transaction.GetFieldByPosition(i);
                    columnsHeader[i - 1] = f.SysName;

                    // var dStr = rec.Transaction.DateReported.Value;
                    // dStr = dStr.substring(0, 4) + "-" + dStr.substring(4, 6) + "-" + dStr.substring(6, 8);
                    // System.out.println(dStr);

                }
            }
            for (int i = 1; i <= rec.IDPasport.GetFieldsCount(); i++) {
                TUTDFField f = rec.IDPasport.GetFieldByPosition(i);
                array[k][i - 1] = f.Value;
            }
            k++;
            for (int i = 1; i <= rec.IDSnils.GetFieldsCount(); i++) {
                TUTDFField f = rec.IDSnils.GetFieldByPosition(i);
                array[k][i - 1] = f.Value;
            }
            k++;
            for (int i = 1; i <= rec.Transaction.GetFieldsCount(); i++) {
                TUTDFField f = rec.Transaction.GetFieldByPosition(i);
                array[k][i - 1] = f.Value;
            }
            k++;
        }

        final JTable table1 = new JTable(array, columnsHeader) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int rendererWidth = component.getPreferredSize().width;
                TableColumn tableColumn = getColumnModel().getColumn(column);
                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
                return component;
            }
        };
        // Получаем стандартную модель
        columnModel = table1.getColumnModel();
        // Определение минимального и максимального размеров столбцов
        // Enumeration<TableColumn> e = columnModel.getColumns();
        // int i = 1;
        // while (e.hasMoreElements()) {
        //     TableColumn column = e.nextElement();
        //     TUTDFField f = File.Records.GetByIndex(0).Transaction.GetFieldByPosition(i);
        //     System.out.println(f.GetLen());
        //     column.setWidth(f.GetLen());
        //
        //     i++;
        // }
        table1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table1.getTableHeader().setReorderingAllowed(false);

        // Таблица с автонастройкой размера последней колонки
        JTable table2 = new JTable(3, 5);
        table2.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        // Размещение таблиц в панели с блочным расположением
        Box contents = new Box(BoxLayout.Y_AXIS);
        contents.add(new JScrollPane(table1));
        contents.add(new JScrollPane(table2));

        // Кнопка добавления колонки в модель TableColumnModel
        JButton add = new JButton("Добавить колонку");
        // Слушатель обработки события
        add.addActionListener(e1 -> {
            // Добавление столбца к модели TableColumnModel
            TableColumn column = new TableColumn(3, 50);
            column.setHeaderValue("<html><b>Цена</b></html>");
            columnModel.addColumn(column);
        });
        // Кнопка перемещения колонки
        JButton move = new JButton("Переместить колонку");
        // Слушатель обработки события
        move.addActionListener(e12 -> {
            // Индекс первой колоки
            int first = table1.getSelectedColumn();
            // Индекс второй колонки
            int last = (first == columnModel.getColumnCount()) ? first + 1 : 0;
            // Перемещение столбцов
            columnModel.moveColumn(first, last);
        });
        // Панель кнопок
        JPanel pnlButtons = new JPanel();
        pnlButtons.add(add);
        pnlButtons.add(move);
        // Слушатель событий модели столбцов таблицы
        columnModel.addColumnModelListener(new TableColumnModelListener() {
            @Override
            public void columnAdded(TableColumnModelEvent arg0) {
                /*System.out.println("TableColumnModelListener.columnAdded()")*/
            }

            @Override
            public void columnMarginChanged(ChangeEvent arg0) {
                //System.out.println("TableColumnModelListener.columnMarginChanged()");
            }

            @Override
            public void columnMoved(TableColumnModelEvent arg0) {
                //System.out.println("TableColumnModelListener.columnMoved()");
            }

            @Override
            public void columnRemoved(TableColumnModelEvent arg0) {
            }

            @Override
            public void columnSelectionChanged(ListSelectionEvent arg0) {
                //System.out.println("TableColumnModelListener.columnSelectionChanged()");
            }
        });

        table1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                // do some actions here, for example
                // print first column value from selected row
                String NewCellValue = table1.getValueAt(table1.getSelectedRow(), 2).toString();
                if (!NewCellValue.equals(LastCellValue)) {
                    LastCellValue = NewCellValue;
                    System.out.println(NewCellValue);
                    RDOrder Order = null;
                    try {
                        Order = new RDOrder(NewCellValue, null, null);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Order.ID);
                    System.out.println(Order.Number);
                    System.out.println(Order.Sum);
                    System.out.println(Order.OpenDate);
                    System.out.println(Order.CloseFact);
                    System.out.println(Order.CloseDate);
                    System.out.println(Order.UUID);

                    int fineDays = Order.Json.getJSONObject("OrderPaymentSchedule").getInt("FineDaysAbsolute");
                    System.out.println(fineDays);
                    System.out.println(Order.LastPayDate);
                    System.out.println(Order.LastPaySum);
                    System.out.println(Order.AllPaySum);
                    System.out.println(Order.LastReportDatePlusDay);

                    try {
                        TUTDF_ID_Segment id = new TUTDF_ID_Segment();
                        id.IDType.Value = "21";
                        id.SeriesNumber.Value = Order.PasportSer;
                        id.IDNumber.Value = Order.PasportNum;
                        id.IssueDate.Value = RDOrder.GetTUTDFDate(Order.PasportDate);
                        id.IssueAuthority.Value = Order.PasportIssued;
                        id.Println();

                        TUTDF_ID_Segment id2 = new TUTDF_ID_Segment(2);
                        id2.IDType.Value = "97";
                        id2.Println();

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }


                    try {
                        TUTDF_TR_Segment tr = new TUTDF_TR_Segment();
                        tr.UserName.Value = "DR01FF000001";
                        tr.AccountNumber.Value = Order.ID;
                        tr.AccountType.Value = "16";
                        tr.AccountRelationship.Value = "1";
                        tr.DateAccountOpened.Value = RDOrder.GetTUTDFDate(Order.OpenDate);
                        tr.DateOfLastPayment.Value = RDOrder.GetTUTDFDate(Order.LastPayDate);
                        tr.DateAccountRating.Value = RDOrder.GetTUTDFDate(Order.CurrentDate);
                        if (Order.LastReportDatePlusDay == null) {
                            tr.DateReported.Value = RDOrder.GetTUTDFDate(Order.CurrentDate);
                        } else {
                            tr.DateReported.Value = RDOrder.GetTUTDFDate(Order.LastReportDatePlusDay);
                        }
                        if (Order.CloseFact) {
                            tr.DateOfLastPayment.Value = RDOrder.GetTUTDFDate(Order.CloseDate);
                            tr.DateAccountRating.Value = RDOrder.GetTUTDFDate(Order.CloseDate);
                            tr.AccountRating.Value = "13";
                            if (tr.DateReported.Value.compareTo(tr.DateAccountRating.Value) < 0) {
                                tr.DateReported.Value = tr.DateAccountRating.Value;
                            }
                        } else if (Order.FineDays <= 0) {
                            tr.AccountRating.Value = "00";
                            tr.DateReported.Value = RDOrder.GetTUTDFDate(Order.CurrentDate);
                        } else {
                            tr.AccountRating.Value = "52";
                            tr.DateReported.Value = RDOrder.GetTUTDFDate(Order.CurrentDate);
                            tr.PastDue.Value = String.valueOf(Order.CurrNeedPay);
                            if (Order.CurrNeedPay == 0) {
                                tr.PastDue.Value = "1";
                            }
                        }

                        tr.ContractAmount.Value = String.valueOf(Order.Sum);
                        tr.Balance.Value = String.valueOf(Order.AllPaySum);

                        tr.CreditPaymentFrequency.Value = "7";

                        if (tr.AccountRating.Value.equals("00") && tr.DateOfLastPayment.Value.equals("19000102")) {
                            tr.MOP.Value = "0";
                        } else if (Order.FineDays <= 0) {
                            tr.MOP.Value = "1";
                        } else if (Order.FineDays > 0 && Order.FineDays <= 29) {
                            tr.MOP.Value = "A";
                        } else if (Order.FineDays > 29 && Order.FineDays <= 59) {
                            tr.MOP.Value = "2";
                        } else if (Order.FineDays > 59 && Order.FineDays <= 89) {
                            tr.MOP.Value = "3";
                        } else if (Order.FineDays > 90 && Order.FineDays <= 119) {
                            tr.MOP.Value = "4";
                        } else if (Order.FineDays > 119) {
                            tr.MOP.Value = "5";
                        }

                        tr.CurrencyCode.Value = "RUB";

                        tr.DateOfContractTermination.Value = RDOrder.GetTUTDFDate(Order.sqlDatePlusDays(Order.CurrentDate, -Order.FineDays));
                        tr.DatePaymentDue.Value = tr.DateOfContractTermination.Value;
                        tr.DateInterestPaymentDue.Value = tr.DatePaymentDue.Value;

                        tr.InterestPaymentFrequency.Value = "7";

                        tr.AmountOutstanding.Value = String.valueOf(Order.AllNeedPay);

                        tr.GuarantorIndicator.Value = "N";
                        tr.BankGuaranteeIndicator.Value = "N";

                        tr.OverallValueOfCreditPercent.Value = Order.Psk;

                        if (tr.AccountRating.Value.equals("13")) {
                            tr.CompletePerformanceOfObligationsDate.Value = tr.DateAccountRating.Value;
                            tr.PrincipalAmountOutstandingAsOfLastPaymentDate.Value = "0";
                            tr.InterestAmountOutstandingAsOfLastPaymentDate.Value = "0";
                            tr.OtherAmountOutstandingAsOfLastPaymentDate.Value = "0";
                            tr.PrincipalAmountPastDueAsOfLastPaymentDate.Value = "0";
                            tr.InterestAmountPastDueAsOfLastPaymentDate.Value = "0";
                            tr.OtherAmountPastDueAsOfLastPaymentDate.Value = "0";

                            tr.TradeUniversallyUniqueID.Value = Order.UUID;

                            tr.OverallValueOfCreditMonetary.Value = Order.PskMoney;

                        }

                        tr.Println();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }


                }
            }
        });
        // Вывод окна на экран
        getContentPane().add(contents);
        getContentPane().add(pnlButtons, BorderLayout.SOUTH);

    }

    public void Show() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Вывод окна на экран
        setSize(1024, 768);
        setVisible(true);
    }
}
