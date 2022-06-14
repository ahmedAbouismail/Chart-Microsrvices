package htw.berlin.microservices.core.data.persistence;

import java.util.Collection;

public class TranscriptObj {

    private String semester;
    private Collection<ModuleGradeObj> grades;
    private int creditsTotal;
    private double average;



    public TranscriptObj() {
        semester = null;
        grades = null;
        creditsTotal = 0;
        average = 0;
    }

    public TranscriptObj(String semester, Collection<ModuleGradeObj> grades, int creditsTotal, double average) {
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

    public Collection<ModuleGradeObj> getGrades() {
        return grades;
    }

    public void setGrades(Collection<ModuleGradeObj> grades) {
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
