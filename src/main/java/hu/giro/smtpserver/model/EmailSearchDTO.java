package hu.giro.smtpserver.model;

public class EmailSearchDTO {

    TrueFalseAllEnum readFilter=TrueFalseAllEnum.ALL;
    String textFilter;
    String domainFilter;

    public TrueFalseAllEnum getReadFilter() {
        return readFilter;
    }

    public void setReadFilter(TrueFalseAllEnum readFilter) {
        this.readFilter = readFilter;
    }

    public String getTextFilter() {
        return textFilter;
    }

    public void setTextFilter(String textFilter) {
        this.textFilter = textFilter;
    }

    public String getDomainFilter() {
        return domainFilter;
    }

    public void setDomainFilter(String domainFilter) {
        this.domainFilter = domainFilter;
    }

    @Override
    public String toString() {
        return "EmailSearchDTO{" +
                "readFilter=" + readFilter +
                ", textFilter='" + textFilter + '\'' +
                ", domainFilter='" + domainFilter + '\'' +
                '}';
    }
}
