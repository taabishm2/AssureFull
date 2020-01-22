package model.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class OrderReceiptData {

    Long orderId;
    String channelName;
    String channelOrderId;
    String orderCreationTime;
    String clientDetails;
    String customerDetails;

    List<OrderItemReceiptData> orderItems;

    public Long getOrderId() {
        return orderId;
    }

    @XmlElement
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getChannelName() {
        return channelName;
    }

    @XmlElement
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelOrderId() {
        return channelOrderId;
    }

    @XmlElement
    public void setChannelOrderId(String channelOrderId) {
        this.channelOrderId = channelOrderId;
    }

    public String getOrderCreationTime() {
        return orderCreationTime;
    }

    @XmlElement
    public void setOrderCreationTime(String orderCreationTime) {
        this.orderCreationTime = orderCreationTime;
    }

    public String getClientDetails() {
        return clientDetails;
    }

    @XmlElement
    public void setClientDetails(String clientDetails) {
        this.clientDetails = clientDetails;
    }

    public String getCustomerDetails() {
        return customerDetails;
    }

    @XmlElement
    public void setCustomerDetails(String customerDetails) {
        this.customerDetails = customerDetails;
    }

    public List<OrderItemReceiptData> getOrderItems() {
        return orderItems;
    }

    @XmlElement
    public void setOrderItems(List<OrderItemReceiptData> orderItems) {
        this.orderItems = orderItems;
    }
}