package com.danil.crud.model;

import java.util.List;

public class Post {
    private Integer id;
    private String content;
    private Long created;
    private Long updated;
    private List<Label> labels;
    private PostStatus status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(Long updated) {
        this.updated = updated;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public void addLabel(Label label) {
        this.labels.add(label);
    }

    public void removeLabel(int labelId) {
        this.labels.removeIf(e -> e.getId() == labelId);
    }

    public PostStatus getStatus() {
        return status;
    }

    public void setStatus(PostStatus status) {
        this.status = status;
    }

    public void delete() {
        this.status = PostStatus.DELETED;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ID: " + this.id + ", ");
        builder.append("content: " + this.content + ", ");
        builder.append("created: " + this.created + ", ");
        builder.append("updated: " + this.updated + ", ");
        builder.append("status: " + this.status + ", ");
        builder.append("labels: [ ");
        for (Label label : labels) {
            builder.append("{" + label + "}, ");
        }
        builder.append("]");
        return builder.toString();
    }

    public boolean isDeleted() {
        return this.status == PostStatus.DELETED;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Post))
            return false;
        Post other = (Post) o;

        boolean idEquals = this.id == null && other.id == null || this.id.equals(other.id);
        boolean contentEquals = this.content == null && other.content == null
                || this.content != null && this.content.equals(other.content);
        boolean createdEquals = this.created == null && other.created == null
                || this.created != null && this.created.equals(other.created);
        boolean updatedEquals = this.updated == null && other.updated == null
                || this.updated != null && this.updated.equals(other.updated);
        boolean labelsEquals = this.labels == null && other.labels == null
                || this.labels != null && this.labels.equals(other.labels);
        boolean statusEquals = this.status == other.status;

        return idEquals && contentEquals && createdEquals && updatedEquals && labelsEquals && statusEquals;
    }
}
