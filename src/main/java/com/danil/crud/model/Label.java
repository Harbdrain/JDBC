package com.danil.crud.model;

public class Label {
    private Integer id;
    private String name;
    private LabelStatus status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LabelStatus getStatus() {
        return this.status;
    }

    public void setStatus(LabelStatus status) {
        this.status = status;
    }

    public void delete() {
        this.status = LabelStatus.DELETED;
    }

    public boolean isDeleted() {
        return this.status == LabelStatus.DELETED;
    }

    @Override
    public String toString() {
        return "ID: " + this.id + ", " + "name: " + this.name + ", " + "status: " + this.status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Label))
            return false;
        Label other = (Label) o;
        boolean idEquals = this.id == null && other.id == null || this.id != null && this.id.equals(other.id);
        boolean nameEquals = this.name == null && other.name == null || this.name != null && this.name.equals(other.name);
        return idEquals && nameEquals && this.status == other.status;
    }
}
