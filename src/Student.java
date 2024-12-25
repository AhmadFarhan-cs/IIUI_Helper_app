public class Student {
    private int regnum;
    private String name;
    private int activeCourses;
    private float cgpa;
    private int cr_hrs;

    Student(int regnum,String name,int activeCourses,int cgpa,int cr_hrs){
        this.regnum=regnum;
        this.name=name;
        this.activeCourses=activeCourses;
        this.cgpa=cgpa;
        this.cr_hrs=cr_hrs;
    }

    public int getRegnum() {
        return regnum;
    }

    public void setRegnum(int regnum) {
        this.regnum = regnum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getActiveCourses() {
        return activeCourses;
    }

    public void setActiveCourses(int activeCourses) {
        this.activeCourses = activeCourses;
    }

    public float getCgpa() {
        return cgpa;
    }

    public void setCgpa(float cgpa) {
        this.cgpa = cgpa;
    }

    public int getCr_hrs() {
        return cr_hrs;
    }

    public void setCr_hrs(int cr_hrs) {
        this.cr_hrs = cr_hrs;
    }
}
