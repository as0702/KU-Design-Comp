import java.util.ArrayList;

public class OrderQueue {
    private ArrayList<BlacksmithOrder> list;

    public OrderQueue() {
        list = new ArrayList<BlacksmithOrder>();
    }

    public void addOrder(BlacksmithOrder order) {
        list.add(order);
    }

    public BlacksmithOrder removeNextOrder() {
        if (list.size() > 0) {
            return list.remove(0);
        }
        return null;
    }

    public ArrayList<BlacksmithOrder> getList() {
        return list;
    }

    public boolean isEmpty() {
        return list.size() == 0;
    }
}