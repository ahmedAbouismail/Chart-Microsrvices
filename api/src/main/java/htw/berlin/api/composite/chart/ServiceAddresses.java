package htw.berlin.api.composite.chart;

public class ServiceAddresses {

    private final String cmc;
    private final String cha;
    private final String dat;

    public ServiceAddresses() {
        cmc = null;
        cha = null;
        dat = null;
    }

    public ServiceAddresses(String cmc, String cha, String dat) {
        this.cmc = cmc;
        this.cha = cha;
        this.dat = dat;
    }

    public String getCmc() {
        return cmc;
    }

    public String getCha() {
        return cha;
    }

    public String getDat() {
        return dat;
    }
}
