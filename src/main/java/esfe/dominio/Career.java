package esfe.dominio;

public class Career {
    private int careerId;        // CareerID en la base de datos
    private String careerName;   // CareerName en la base de datos

    public Career() {
    }

    public Career(int careerId, String careerName) {
        this.careerId = careerId;
        this.careerName = careerName;
    }

    public int getCareerId() {
        return careerId;
    }

    public void setCareerId(int careerId) {
        this.careerId = careerId;
    }

    public String getCareerName() {
        return careerName;
    }

    public void setCareerName(String careerName) {
        this.careerName = careerName;
    }

    @Override
    public String toString() {
        return careerName;
    }
}
