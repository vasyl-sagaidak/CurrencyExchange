package model;

public class Currency {
    private int id;
    private final String code;
    private final String fullName;
    private final String sign;

    public Currency(int id, String code,
                    String fullName, String sign) {
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getFullName() {
        return fullName;
    }

    public String getSign() {
        return sign;
    }

    public void setId(int id) {
        if (id > 0) {
            this.id = id;
        }
    }

    @Override
    public String toString() {
        return "CurrencyEntity{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", fullName='" + fullName + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
