package io.metadew.iesi.metadata.definition;

public class ErrorObject {

    private String status;
    // Error, Warning, Info
    private String code;
    // internal code
    private String title;
    // short description of the error, what might have cause it and possibly a
    // fixing proposal
    private String link;
    // points to an online resource, where more details can be found about the
    // error
    private String detail;
    // detailed message, containing additional data that might be relevant to
    // the developer. This should only be available when the â€œdebugâ€� mode is
    // switched on and could potentially contain stack trace information or
    // something similar

    // Constructors
    public ErrorObject() {

    }

    public ErrorObject(String code, String detail) {
        this.setStatus("");
        this.setCode(code);
        this.setTitle("");
        this.setLink("");
        this.setDetail(detail);
    }

    public ErrorObject(String status, String code, String title, String link, String detail) {
        this.setStatus(status);
        this.setCode(code);
        this.setTitle(title);
        this.setLink(link);
        this.setDetail(detail);
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
