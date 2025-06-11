package esfe.dominio;

public class Students {
    private int studentId;       // StudentID
    private String code;         // Code
    private String fullName;     // FullName
    private int age;             // Age
    private Career career;       // Objeto Career (relación con CareerID)

    public Students() {
    }

    public Students(int studentId, String code, String fullName, int age, Career career) {
        this.studentId = studentId;
        this.code = code;
        this.fullName = fullName;
        this.age = age;
        this.career = career;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Career getCareer() {
        return career;
    }

    public void setCareer(Career career) {
        this.career = career;
    }

    @Override
    public String toString() {
        return fullName + " (" + code + ")";
    }
}
