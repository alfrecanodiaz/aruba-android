package py.com.aruba.profesionales.data.helpers;

import com.google.gson.annotations.SerializedName;

public class Paginator<T> {

    @SerializedName("total")
    int total;
    @SerializedName("from")
    int from;
    @SerializedName("to")
    int to;
    @SerializedName("per_page")
    int per_page;
    @SerializedName("current_page")
    int current_page;
    @SerializedName("last_page")
    int last_page;
    @SerializedName("data")
    T data;

    public Paginator(int total, int from, int to, int per_page, int current_page, int last_page, T data) {
        this.total = total;
        this.from = from;
        this.to = to;
        this.per_page = per_page;
        this.current_page = current_page;
        this.last_page = last_page;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    // Si el current_page es menor al last_page, retornamos true para volver a llamar al endpoint
    public boolean hasNextPage() {
        return this.getCurrent_page() < this.getLast_page();
    }
}
