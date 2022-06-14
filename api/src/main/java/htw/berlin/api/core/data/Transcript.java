package htw.berlin.api.core.data;

import java.util.Collection;

public class Transcript {
    private String semester;
    private Collection<ModuleGrade> grades;
    private int creditsTotal;
    private double average;


    public Transcript() {
        semester = null;
        grades = null;
        creditsTotal = 0;
        average = 0.0;
    }

    public Transcript(String semester, Collection<ModuleGrade> grades, int creditsTotal, double average) {
        this.semester = semester;
        this.grades = grades;
        this.creditsTotal = creditsTotal;
        this.average = average;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Collection<ModuleGrade> getGrades() {
        return grades;
    }

    public void setGrades(Collection<ModuleGrade> grades) {
        this.grades = grades;
    }

    public int getCreditsTotal() {
        return creditsTotal;
    }

    public void setCreditsTotal(int creditsTotal) {
        this.creditsTotal = creditsTotal;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }
}
