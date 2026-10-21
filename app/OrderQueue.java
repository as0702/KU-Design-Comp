import java.util.ArrayList;

public class OrderQueue {
    //Stores all of the orders that are not yet completed
    private ArrayList<BlacksmithOrder> list;

    //Creates an empty list when the queue is being made
    public OrderQueue() {
        list = new ArrayList<BlacksmithOrder>();
    }
    // Adds a new order to the end of the queue
    public void addOrder(BlacksmithOrder order) {
        list.add(order);
    }
    //Removes and returns the first order in the queue
    public BlacksmithOrder removeNextOrder() {
        if (list.size() > 0) {
            return list.remove(0);
        }
        //Returns null if there are no orders left
        return null;
    }
    //Returns the list of current orders
    public ArrayList<BlacksmithOrder> getList() {
        return list;
    }
    //Checks whether the current queue has any orders
    public boolean isEmpty() {
        return list.size() == 0;
    }
}
