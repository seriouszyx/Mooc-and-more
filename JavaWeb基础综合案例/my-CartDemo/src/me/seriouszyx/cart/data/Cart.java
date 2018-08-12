package me.seriouszyx.cart.data;

/**
 *  購物車
 * */
public class Cart {

    /** 购物车中ID */
    private Long id;
    /** 商品ID */
    private Long productId;
    /** 商品名称 */
    private String name;
    /** 商品单价 */
    private int price;
    /** 商品数量 */
    private int count;
    /** 购物车该类商品总价 */
    private int totalPrice;

    public void incrCount() {
        count++;
        this.totalPrice = price * count;
    }

    public boolean decrCount() {
        count--;
        this.totalPrice = price * count;
        if (0 == count) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", productId=" + productId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", count=" + count +
                ", totalPrice=" + totalPrice +
                '}';
    }

    public Cart(Long id, Long productId, String name, int price, int count) {
        this.id = id;
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.count = count;
        this.totalPrice = count * price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
