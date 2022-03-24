package rd.rusdengi;

import java.util.ArrayList;

public class TUTDFArrayList<E> extends ArrayList<E> {
    public E GetByIndex(int index) {
        return super.get(index);
    }

    public E GetByNumber(int index) {
        return super.get(index - 1);
    }
}
