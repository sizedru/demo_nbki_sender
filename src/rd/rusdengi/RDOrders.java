package rd.rusdengi;

import java.text.ParseException;
import java.util.TreeMap;

public class RDOrders {
    public final TreeMap<String, RDOrder> Orders = new TreeMap<>();


    public RDOrders(String ids) {
        var db = new Db();
        db.OpenRd();

        String SQL = "";

        Boolean toDate = false;

        if (ids.equals("SELECT")) {
            SQL = "select ............";

        } else {
            toDate = true;

            SQL = "select ............";
        }

        System.out.println(SQL);

        db.Query(SQL, false, false);
        var rs = db.GetResultSet();
        try {
            RDOrder prevorderLast = null;
            Integer i = 0;
            Integer cnt = 0;
            String lo = "";
            while (true) {
                RDOrder order = new RDOrder("", rs, toDate);
                if (order.ID == null) {
                    break;
                }
                //System.out.println(order.ID);
                if (order.Ok) {
                    if (lo.equals(order.ID)) {
                        i = 0;
                        lo = order.ID;
                        prevorderLast = null;
                    }
                    if (prevorderLast != null) {
                        if (order.LastRecord) {
                            prevorderLast.LastRecord = true;
                        }
                    }
                    if (order.LastRecord) {
                        prevorderLast = order;
                    }
                    var ind = "000000" + i;
                    Orders.put(order.ID + "_" + ind.substring(ind.length() - 6), order);
                    i++;
                    cnt++;
                }
                System.out.println("count: " + cnt);
            }

            // Orders.entrySet().forEach(System.out::println);

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

}
