import java.util.ArrayList;

public class OrderQueue {
    //Store orders in a uncompleted and completed list
    private ArrayList<BlacksmithOrder> list;
    private ArrayList<BlacksmithOrder> completedOrders;

    //Creates an empty list when the queue is being made
    public OrderQueue() {
        list = new ArrayList<BlacksmithOrder>();
        completedOrders = new ArrayList<BlacksmithOrder>();
    }

    // Adds a new order to the end of the queue
    public void addOrder(BlacksmithOrder order) {
        list.add(order);
    }

    //Removes and returns the first order in the queue
    public BlacksmithOrder removeNextOrder() {
        if (list.size() > 0) {
            BlacksmithOrder finishedOrder = list.remove(0);
            completedOrders.add(finishedOrder);
            return finishedOrder;
        }
        //Returns null if there are no orders left
        return null;
    }
    //Getter functions
    public ArrayList<BlacksmithOrder> getList() {
        return list;
    }

    public ArrayList<BlacksmithOrder> getCompleted() {
        return completedOrders;
    }

    public boolean isEmpty() {
        return list.size() == 0;
    }

    public int getSize() {
        return list.size();
    }

    //use to tell status of an order, whether it is completed or not
    public boolean isCompleted(int orderNumber) {
        for (BlacksmithOrder order : completedOrders) {
            if (order.getOrderNumber() == orderNumber) {
                return true;
            }
        }
        return false;
    }

    //searches through boths lists to find an order and returns the order based on index
    public BlacksmithOrder findOrder(int orderNumber) {
        for (BlacksmithOrder order : list) {
            if (order.getOrderNumber() == orderNumber) {
                return order;
            }
        }
        for (BlacksmithOrder order : completedOrders) {
            if (order.getOrderNumber() == orderNumber) {
                return order;
            }
        }
        return null;
    }
}