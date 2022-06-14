package htw.berlin.microservices.core.data.persistence;

public class ModuleGradeObj {
    private double grade;
    private String module;
    private String label;
    private int credits;

    public ModuleGradeObj() {
        grade = 0.0;
        module = null;
        label = null;
        credits = 0;
    }

    public ModuleGradeObj(double grade, String module, String label, int credits) {
        this.grade = grade;
        this.module = module;
        this.label = label;
        this.credits = credits;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
}
