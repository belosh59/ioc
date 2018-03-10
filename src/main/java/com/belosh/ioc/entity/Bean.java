package com.belosh.ioc.entity;

public class Bean {
    private Object value;
    private String id;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bean bean = (Bean) o;

        return (value != null ? value.equals(bean.value) : bean.value == null) && id.equals(bean.id);
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + id.hashCode();
        return result;
    }
}
